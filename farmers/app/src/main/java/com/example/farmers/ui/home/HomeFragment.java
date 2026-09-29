package com.example.farmers.ui.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.farmers.FarmerActivity;
import com.example.farmers.MainActivity;
import com.example.farmers.R;
import com.example.farmers.data.api.RetrofitClient;
import com.example.farmers.data.model.PersonaItem;
import com.example.farmers.data.model.WeatherResponse;
import com.example.farmers.databinding.FragmentHomeBinding;
import com.example.farmers.ui.adapter.ForecastDayAdapter;
import com.example.farmers.ui.adapter.ForecastHourAdapter;
import com.example.farmers.ui.adapter.PersonaAdapter;
import com.example.farmers.util.LocationHelper;
import com.example.farmers.util.PrefsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ForecastHourAdapter hourAdapter;
    private ForecastDayAdapter dayAdapter;
    private PersonaAdapter personaAdapter;
    private LocationHelper locationHelper;
    private PrefsManager prefs;
    private boolean weatherLoaded = false;
    private WeatherResponse.Current lastCurrentWeather;

    // Called after system permission dialog result
    private final ActivityResultLauncher<String[]> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean fine = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                Boolean coarse = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);
                if ((fine != null && fine) || (coarse != null && coarse)) {
                    // Permission granted — silently refresh location in background
                    refreshLocationInBackground();
                } else {
                    // Permission denied — already showing data from saved coords
                    if (!weatherLoaded) {
                        fetchWeather(prefs.getLatitude(), prefs.getLongitude());
                    }
                    Toast.makeText(requireContext(),
                            "📍 Location denied. Showing weather for last known location.",
                            Toast.LENGTH_LONG).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = PrefsManager.getInstance(requireContext());
        locationHelper = new LocationHelper(requireContext());

        setupRecyclerViews();

        // Show saved location name immediately
        binding.tvLocationName.setText(prefs.getLocationName());

        // Swipe to refresh — always fetch fresh location + weather
        binding.swipeRefresh.setOnRefreshListener(() -> {
            weatherLoaded = false;
            checkPermissionAndProceed();
        });

        // Manual refresh button
        binding.btnRefreshLocation.setOnClickListener(v -> {
            weatherLoaded = false;
            checkPermissionAndProceed();
        });

        // STEP 1: Immediately load weather from saved/default coords (no waiting)
        fetchWeather(prefs.getLatitude(), prefs.getLongitude());

        // STEP 2: Check/request location permission to get accurate live location
        checkPermissionAndProceed();
    }

    // ─── Permission Flow ──────────────────────────────────────────────────────

    private void checkPermissionAndProceed() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted — just silently get fresh location
            refreshLocationInBackground();
        } else {
            // Show custom explanation dialog first
            showLocationPermissionDialog();
        }
    }

    private void showLocationPermissionDialog() {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_location_permission, null);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(false)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        Button btnAllow = dialogView.findViewById(R.id.btnAllowLocation);
        Button btnDeny = dialogView.findViewById(R.id.btnDenyLocation);

        btnAllow.setOnClickListener(v -> {
            dialog.dismiss();
            locationPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        });

        btnDeny.setOnClickListener(v -> {
            dialog.dismiss();
            Toast.makeText(requireContext(),
                    "📍 Using last known location. Tap 🔄 to retry.",
                    Toast.LENGTH_LONG).show();
        });

        dialog.show();
    }

    private void refreshLocationInBackground() {
        locationHelper.getCurrentLocation(new LocationHelper.LocationResultListener() {
            @Override
            public void onLocationFound(double lat, double lon, String locationName) {
                if (!isAdded()) return;
                prefs.setLocation(lat, lon, locationName);
                binding.tvLocationName.setText(locationName);
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).updateLocationSubtitle();
                }
                fetchWeather(lat, lon);
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                if (binding.swipeRefresh.isRefreshing()) {
                    binding.swipeRefresh.setRefreshing(false);
                }
            }
        });
    }

    // ─── Weather Fetch ────────────────────────────────────────────────────────

    private void fetchWeather(double lat, double lon) {
        if (!isAdded()) return;
        binding.swipeRefresh.setRefreshing(true);

        RetrofitClient.getWeatherService().getWeather(
                lat, lon,
                "temperature_2m,apparent_temperature,relative_humidity_2m,precipitation,weather_code,surface_pressure,wind_speed_10m,wind_direction_10m,wind_gusts_10m,uv_index,is_day",
                "temperature_2m,precipitation_probability,precipitation,weather_code,wind_speed_10m,uv_index",
                "weather_code,temperature_2m_max,temperature_2m_min,precipitation_sum,precipitation_probability_max,wind_speed_10m_max,uv_index_max,sunrise,sunset",
                "auto",
                7
        ).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call,
                                   @NonNull Response<WeatherResponse> response) {
                if (!isAdded()) return;
                binding.swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    weatherLoaded = true;
                    populateWeather(response.body());
                } else {
                    binding.tvWeatherCondition.setText("Unable to load weather");
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                binding.swipeRefresh.setRefreshing(false);
                binding.tvWeatherCondition.setText("Network error — swipe to retry");
            }
        });
    }

    // ─── UI Population ────────────────────────────────────────────────────────

    private void setupRecyclerViews() {
        // User Type / Persona RecyclerView
        personaAdapter = new PersonaAdapter(item -> {
            if (item != null) {
                if ("agri".equals(item.getId())) {
                    Intent intent = new Intent(requireContext(), FarmerActivity.class);
                    startActivity(intent);
                } else if (lastCurrentWeather != null) {
                    updatePersonaInsight(lastCurrentWeather);
                }
            }
        });
        binding.rvPersona.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvPersona.setAdapter(personaAdapter);

        List<PersonaItem> personas = new ArrayList<>();
        personas.add(new PersonaItem("health", "Health conscious", "🩺"));
        personas.add(new PersonaItem("fitness", "Outdoor fitness", "🏃"));
        personas.add(new PersonaItem("beach", "Beachgoers & surfers", "🏄"));
        personas.add(new PersonaItem("travel", "Traveler", "🧳"));
        personas.add(new PersonaItem("parents", "Parents & families", "👨‍👩‍👧‍👦"));
        personas.add(new PersonaItem("agri", "Agriculture & gardener", "🌱"));
        personas.add(new PersonaItem("commuter", "Commuters", "🚆"));
        personas.add(new PersonaItem("events", "Event planners", "🎪"));
        personaAdapter.setItems(personas);

        // Hourly forecast RecyclerView
        hourAdapter = new ForecastHourAdapter();
        binding.rvHourly.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvHourly.setAdapter(hourAdapter);

        // Daily forecast RecyclerView
        dayAdapter = new ForecastDayAdapter();
        binding.rvDaily.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvDaily.setAdapter(dayAdapter);
    }

    private void populateWeather(WeatherResponse weather) {
        if (weather.current != null) {
            lastCurrentWeather = weather.current;
            binding.tvCurrentTemp.setText(String.format(Locale.getDefault(), "%.0f°C", weather.current.temperature2m));
            binding.tvFeelsLike.setText(String.format(Locale.getDefault(), "Feels like: %.0f°C", weather.current.apparentTemperature));
            binding.tvMetricHumidity.setText(String.format(Locale.getDefault(), "%d %%", weather.current.relativeHumidity2m));
            binding.tvMetricWind.setText(String.format(Locale.getDefault(), "%.1f km/h", weather.current.windSpeed10m));
            binding.tvMetricUV.setText(String.format(Locale.getDefault(), "%.1f", weather.current.uvIndex));
            binding.tvWeatherCondition.setText(getWeatherDescription(weather.current.weatherCode));

            evaluateAgriAdvisory(weather.current);
            updatePersonaInsight(weather.current);
        }

        // Rain chance from daily
        if (weather.daily != null && weather.daily.precipitationProbabilityMax != null
                && !weather.daily.precipitationProbabilityMax.isEmpty()) {
            Integer val = weather.daily.precipitationProbabilityMax.get(0);
            if (val != null) {
                binding.tvMetricRainChance.setText(String.format(Locale.getDefault(), "%d %%", val));
            }
        }

        // Hourly forecast
        if (weather.hourly != null && weather.hourly.time != null) {
            List<ForecastHourAdapter.HourItem> hourList = new ArrayList<>();
            int max = Math.min(weather.hourly.time.size(), 24);
            for (int i = 0; i < max; i++) {
                double t = safeDouble(weather.hourly.temperature2m, i);
                int r = safeInt(weather.hourly.precipitationProbability, i);
                int c = safeInt(weather.hourly.weatherCode, i);
                hourList.add(new ForecastHourAdapter.HourItem(weather.hourly.time.get(i), t, r, c));
            }
            hourAdapter.setItems(hourList);
        }

        // 7-day daily forecast
        if (weather.daily != null && weather.daily.time != null) {
            List<ForecastDayAdapter.DayItem> dayList = new ArrayList<>();
            for (int i = 0; i < weather.daily.time.size(); i++) {
                double maxT = safeDouble(weather.daily.temperatureMax, i);
                double minT = safeDouble(weather.daily.temperatureMin, i);
                int prob = safeInt(weather.daily.precipitationProbabilityMax, i);
                int c = safeInt(weather.daily.weatherCode, i);
                dayList.add(new ForecastDayAdapter.DayItem(
                        weather.daily.time.get(i), maxT, minT, prob, c));
            }
            dayAdapter.setItems(dayList);
        }
    }

    private void updatePersonaInsight(WeatherResponse.Current c) {
        PersonaItem selected = personaAdapter.getSelectedPersona();
        if (selected == null || c == null) return;

        binding.tvPersonaInsightHeader.setText(String.format("%s %s Outlook", selected.getIcon(), selected.getTitle()));

        String insight;
        switch (selected.getId()) {
            case "health":
                if (c.uvIndex > 6.0) {
                    insight = String.format(Locale.getDefault(),
                            "⚠️ High UV Index (%.1f): Wear SPF 30+ sunscreen, sunglasses, and limit midday direct sun exposure.", c.uvIndex);
                } else if (c.relativeHumidity2m > 75) {
                    insight = String.format(Locale.getDefault(),
                            "💧 High Humidity (%d%%): Stay hydrated and monitor air comfort if sensitive to moisture.", c.relativeHumidity2m);
                } else {
                    insight = "🍃 Excellent Air & Climate: Ideal weather for outdoor health and breathing comfort.";
                }
                break;

            case "fitness":
                if (c.temperature2m > 32) {
                    insight = String.format(Locale.getDefault(),
                            "🔥 High Temperature (%.0f°C): Schedule workouts early in the morning or after sunset.", c.temperature2m);
                } else if (c.windSpeed10m > 25) {
                    insight = String.format(Locale.getDefault(),
                            "💨 Strong Winds (%.1f km/h): Expect wind resistance during outdoor cycling or running.", c.windSpeed10m);
                } else if (c.precipitation > 0) {
                    insight = "🌧️ Light Rain Active: Wear waterproof athletic gear or opt for indoor training.";
                } else {
                    insight = "🏃 Perfect Fitness Weather: Great conditions for running, cycling, and outdoor workouts!";
                }
                break;

            case "beach":
                if (c.precipitation > 1.0) {
                    insight = "🌧️ Rainfall Active: Rainy conditions near coastal areas. Keep rain gear handy.";
                } else if (c.windSpeed10m > 25) {
                    insight = String.format(Locale.getDefault(),
                            "🏄 High Breeze (%.1f km/h): Great wind & wave conditions for windsurfing and sailing!", c.windSpeed10m);
                } else {
                    insight = "🏖️ Pleasant Coastal Breeze: Ideal conditions for beach walks, sunbathing, and swimming.";
                }
                break;

            case "travel":
                if (c.weatherCode >= 51 && c.weatherCode <= 82) {
                    insight = "🌧️ Wet Road Conditions: Rain active. Drive carefully and check live travel updates.";
                } else if (c.weatherCode == 45 || c.weatherCode == 48) {
                    insight = "🌫️ Reduced Visibility: Foggy roads. Use fog lights and maintain safe distance.";
                } else {
                    insight = "🧳 Smooth Travel Conditions: Clear skies and pleasant road conditions along transit routes.";
                }
                break;

            case "parents":
                if (c.temperature2m < 12) {
                    insight = String.format(Locale.getDefault(),
                            "🧥 Chilly Weather (%.0f°C): Dress children in warm layers for outdoor playground trips.", c.temperature2m);
                } else if (c.uvIndex > 5.0) {
                    insight = "🧢 Sun Protection Recommended: Higher solar radiation—use sun hats and child-safe sunscreen.";
                } else {
                    insight = "🎈 Wonderful Family Weather: Great day for park visits, playground fun, and outdoor picnics!";
                }
                break;

            case "agri":
                if (c.windSpeed10m > 30) {
                    insight = "💨 High Wind Warning: Avoid aerial spraying & secure greenhouse covers.";
                } else if (c.precipitation > 2.0) {
                    insight = "🌧️ Rainfall Advisory: Hold off irrigation and verify field drainage channels.";
                } else {
                    insight = "🌱 Optimal Farming & Gardening: Excellent conditions for sowing, watering, and soil work.";
                }
                break;

            case "commuter":
                if (c.precipitation > 0 || c.weatherCode >= 51) {
                    insight = "☔ Wet Commute: Carry an umbrella and allow extra travel buffer for traffic.";
                } else {
                    insight = "🚆 Clear Commute: Dry conditions with no weather-related transit delays expected.";
                }
                break;

            case "events":
                if (c.precipitation > 1.0 || c.weatherCode >= 80) {
                    insight = "⛺ Active Rain: Outdoor events require covered marquees or indoor arrangements.";
                } else {
                    insight = "🎪 Excellent Event Weather: Dry skies and comfortable breezes for outdoor gatherings!";
                }
                break;

            default:
                insight = "☀️ Enjoy current weather conditions tailored to your daily activities.";
                break;
        }

        binding.tvPersonaInsightBody.setText(insight);
    }

    private void evaluateAgriAdvisory(WeatherResponse.Current c) {
        if (c.windSpeed10m > 30) {
            binding.tvAgriStatus.setText("💨 High Wind Warning: Avoid aerial spraying & secure greenhouse covers.");
        } else if (c.precipitation > 2.0) {
            binding.tvAgriStatus.setText("🌧️ Rainy: Hold off irrigation & ensure field drainage channels are clear.");
        } else if (c.temperature2m > 36) {
            binding.tvAgriStatus.setText("🔥 Heat Stress: Irrigate crops during evening to minimize solar loss.");
        } else if (c.temperature2m < 5) {
            binding.tvAgriStatus.setText("❄️ Frost Warning: Protect sensitive nursery beds and vegetable patches.");
        } else {
            binding.tvAgriStatus.setText("🌱 Sowing & Field Work: Optimal conditions for farming and planting.");
        }
    }

    private String getWeatherDescription(int code) {
        switch (code) {
            case 0:  return "Clear Sky";
            case 1:  return "Mainly Clear";
            case 2:  return "Partly Cloudy";
            case 3:  return "Overcast";
            case 45: case 48: return "Foggy";
            case 51: case 53: case 55: return "Light Drizzle";
            case 61: case 63: return "Moderate Rain";
            case 65: return "Heavy Rain";
            case 71: case 73: case 75: return "Snowfall";
            case 80: case 81: case 82: return "Rain Showers";
            case 95: case 96: case 99: return "Thunderstorm";
            default: return "Partly Cloudy";
        }
    }

    // ─── Safe list accessors ──────────────────────────────────────────────────

    private double safeDouble(List<Double> list, int i) {
        if (list != null && list.size() > i && list.get(i) != null) return list.get(i);
        return 0.0;
    }

    private int safeInt(List<Integer> list, int i) {
        if (list != null && list.size() > i && list.get(i) != null) return list.get(i);
        return 0;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
