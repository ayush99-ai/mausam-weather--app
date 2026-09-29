package com.example.farmers.ui.fitness;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.farmers.R;
import com.example.farmers.data.api.RetrofitClient;
import com.example.farmers.data.model.WeatherResponse;
import com.example.farmers.util.LocaleHelper;
import com.example.farmers.util.PrefsManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FitnessActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    private ImageButton btnBack;
    private ImageButton btnRefresh;
    private TextView tvLocationSubtitle;

    // Sport Chips
    private Chip chipSportRunning;
    private Chip chipSportCycling;
    private Chip chipSportYoga;
    private Chip chipSportTrek;

    // Hero Score & Conditions
    private TextView tvSportTitle;
    private TextView tvFitnessScore;
    private TextView tvFitnessScoreBadge;
    private TextView tvCurrentConditions;
    private TextView tvFitnessHeroMsg;

    // Windows
    private TextView tvMorningWindowBadge;
    private TextView tvMorningWindowDesc;
    private TextView tvEveningWindowBadge;
    private TextView tvEveningWindowDesc;

    // Metrics
    private TextView tvSweatRate;
    private TextView tvHeatStrain;
    private TextView tvCyclingWind;
    private TextView tvUvSafeExposure;

    // Share Button
    private MaterialButton btnShareFitnessPlan;

    private PrefsManager prefs;
    private WeatherResponse.Current lastCurrentWeather;
    private String selectedSport = "running"; // running, cycling, yoga, trek

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness);

        prefs = PrefsManager.getInstance(this);

        initViews();
        setupListeners();
        loadWeatherData();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnRefresh = findViewById(R.id.btnRefresh);
        tvLocationSubtitle = findViewById(R.id.tvLocationSubtitle);

        chipSportRunning = findViewById(R.id.chipSportRunning);
        chipSportCycling = findViewById(R.id.chipSportCycling);
        chipSportYoga = findViewById(R.id.chipSportYoga);
        chipSportTrek = findViewById(R.id.chipSportTrek);

        tvSportTitle = findViewById(R.id.tvSportTitle);
        tvFitnessScore = findViewById(R.id.tvFitnessScore);
        tvFitnessScoreBadge = findViewById(R.id.tvFitnessScoreBadge);
        tvCurrentConditions = findViewById(R.id.tvCurrentConditions);
        tvFitnessHeroMsg = findViewById(R.id.tvFitnessHeroMsg);

        tvMorningWindowBadge = findViewById(R.id.tvMorningWindowBadge);
        tvMorningWindowDesc = findViewById(R.id.tvMorningWindowDesc);
        tvEveningWindowBadge = findViewById(R.id.tvEveningWindowBadge);
        tvEveningWindowDesc = findViewById(R.id.tvEveningWindowDesc);

        tvSweatRate = findViewById(R.id.tvSweatRate);
        tvHeatStrain = findViewById(R.id.tvHeatStrain);
        tvCyclingWind = findViewById(R.id.tvCyclingWind);
        tvUvSafeExposure = findViewById(R.id.tvUvSafeExposure);

        btnShareFitnessPlan = findViewById(R.id.btnShareFitnessPlan);

        String loc = prefs.getLocationName();
        tvLocationSubtitle.setText("📍 " + (loc.isEmpty() ? "Current Location" : loc) + " • Cardio & Strength");
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnRefresh.setOnClickListener(v -> loadWeatherData());

        chipSportRunning.setOnClickListener(v -> selectSport("running"));
        chipSportCycling.setOnClickListener(v -> selectSport("cycling"));
        chipSportYoga.setOnClickListener(v -> selectSport("yoga"));
        chipSportTrek.setOnClickListener(v -> selectSport("trek"));

        if (btnShareFitnessPlan != null) {
            btnShareFitnessPlan.setOnClickListener(v -> shareFitnessPlan());
        }
    }

    private void selectSport(String sportKey) {
        selectedSport = sportKey;
        updateChipVisuals();
        if (lastCurrentWeather != null) {
            populateFitnessData(lastCurrentWeather);
        } else {
            populateFallbackData();
        }
    }

    private void updateChipVisuals() {
        resetChipStyle(chipSportRunning);
        resetChipStyle(chipSportCycling);
        resetChipStyle(chipSportYoga);
        resetChipStyle(chipSportTrek);

        switch (selectedSport) {
            case "cycling":
                highlightChip(chipSportCycling);
                break;
            case "yoga":
                highlightChip(chipSportYoga);
                break;
            case "trek":
                highlightChip(chipSportTrek);
                break;
            case "running":
            default:
                highlightChip(chipSportRunning);
                break;
        }
    }

    private void resetChipStyle(Chip chip) {
        if (chip != null) {
            chip.setChecked(false);
            chip.setChipBackgroundColorResource(R.color.primary_green_dark);
            chip.setTextColor(Color.WHITE);
        }
    }

    private void highlightChip(Chip chip) {
        if (chip != null) {
            chip.setChecked(true);
            chip.setChipBackgroundColorResource(R.color.white);
            chip.setTextColor(ContextCompat.getColor(this, R.color.primary_green_dark));
        }
    }

    private void shareFitnessPlan() {
        String sportName = "🏃 Running";
        if ("cycling".equals(selectedSport)) sportName = "🚴 Cycling";
        else if ("yoga".equals(selectedSport)) sportName = "🧘 Outdoor Yoga";
        else if ("trek".equals(selectedSport)) sportName = "🚶 Trekking";

        String text = String.format(Locale.getDefault(),
                "🏃 *Outdoor Fitness Weather Plan*\n\n" +
                        "🏋️ *Activity*: %s\n" +
                        "🏆 *Fitness Score*: %s\n" +
                        "🌡️ *Conditions*: %s\n" +
                        "💧 *Sweat Rate*: %s\n" +
                        "💡 *Advisory*: %s\n\n" +
                        "Train smart and stay hydrated!",
                sportName,
                tvFitnessScore != null ? tvFitnessScore.getText().toString() : "90/100",
                tvCurrentConditions != null ? tvCurrentConditions.getText().toString() : "",
                tvSweatRate != null ? tvSweatRate.getText().toString() : "600 ml/hr",
                tvFitnessHeroMsg != null ? tvFitnessHeroMsg.getText().toString() : "");

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");

        startActivity(Intent.createChooser(sendIntent, "Share Workout Plan"));
    }

    private void loadWeatherData() {
        double lat = prefs.getLatitude();
        double lon = prefs.getLongitude();

        RetrofitClient.getWeatherService().getWeather(
                lat, lon,
                "temperature_2m,apparent_temperature,relative_humidity_2m,precipitation,weather_code,surface_pressure,wind_speed_10m,wind_direction_10m,wind_gusts_10m,uv_index,is_day",
                "temperature_2m,precipitation_probability,precipitation,weather_code,wind_speed_10m,uv_index",
                "weather_code,temperature_2m_max,temperature_2m_min,precipitation_sum,precipitation_probability_max,wind_speed_10m_max,uv_index_max,sunrise,sunset",
                "auto",
                7
        ).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().current != null) {
                    lastCurrentWeather = response.body().current;
                    populateFitnessData(response.body().current);
                } else {
                    populateFallbackData();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                populateFallbackData();
            }
        });
    }

    private void populateFitnessData(WeatherResponse.Current c) {
        tvSportTitle.setText("OUTDOOR " + selectedSport.toUpperCase(Locale.US) + " SUITABILITY");

        double temp = c.temperature2m;
        double wind = c.windSpeed10m;
        int humidity = c.relativeHumidity2m;
        double rain = c.precipitation;
        double uv = c.uvIndex;

        // Base sport scores & metrics calculation
        int score = 100;
        double sweatRateMl = 500;
        String dragText = "10 km/h Breeze";

        switch (selectedSport) {
            case "cycling":
                // Cycling is sensitive to wind and rain
                if (wind > 35) score -= 35;
                else if (wind > 20) score -= 15;
                if (rain > 0) score -= 30;
                if (temp > 34) score -= 25;
                else if (temp < 12) score -= 10;

                sweatRateMl = 650 + Math.max(0, (temp - 20) * 45) + (wind * 8);
                dragText = String.format(Locale.getDefault(), "%.1f km/h Headwind Drag", wind);

                tvCurrentConditions.setText(String.format(Locale.getDefault(), "%.0f°C • Wind %.1f km/h • Road Grip Index 88", temp, wind));

                if (wind > 30) {
                    tvFitnessHeroMsg.setText("💨 High Crosswinds (" + String.format(Locale.getDefault(), "%.1f", wind) + " km/h). Maintain aero tuck and hold handlebar firmly on open highway bridges.");
                } else if (rain > 0) {
                    tvFitnessHeroMsg.setText("🌧️ Wet Asphalt: Reduced tire cornering grip. Lower tire pressure by 5 PSI and avoid paint lines.");
                } else {
                    tvFitnessHeroMsg.setText("🚴 Excellent Road Cycling Conditions! Smooth pavement grip, low aerodynamic drag, and comfortable temp.");
                }

                tvMorningWindowBadge.setText("05:30 – 08:00 AM (98/100)");
                tvMorningWindowDesc.setText("Low highway traffic, smooth asphalt temperatures, and minimal wind drag.");
                tvEveningWindowBadge.setText("06:00 – 08:00 PM (88/100)");
                tvEveningWindowDesc.setText("Sunset cooling breeze with low sun glare on open biking corridors.");
                break;

            case "yoga":
                // Yoga is sensitive to heat, rain, and UV
                if (temp > 32) score -= 25;
                else if (temp < 15) score -= 20;
                if (rain > 0) score -= 40;
                if (uv > 7.0) score -= 15;

                sweatRateMl = 350 + Math.max(0, (temp - 22) * 25);
                dragText = String.format(Locale.getDefault(), "%.1f km/h Gentle Breeze", Math.min(wind, 12.0));

                tvCurrentConditions.setText(String.format(Locale.getDefault(), "%.0f°C • UV %.1f • Shaded Park Air", temp, uv));

                if (temp > 32) {
                    tvFitnessHeroMsg.setText("🧘 High Heat: Move outdoor yoga or calisthenics under shaded park trees. Keep hydration flask ready.");
                } else if (rain > 0) {
                    tvFitnessHeroMsg.setText("🌧️ Rain Active: Move mat session to a covered park gazebo or indoor studio.");
                } else {
                    tvFitnessHeroMsg.setText("🧘 Serene Outdoor Air! Perfect temperature for morning park yoga, stretching, and bodyweight training.");
                }

                tvMorningWindowBadge.setText("06:30 – 08:30 AM (96/100)");
                tvMorningWindowDesc.setText("Fresh park oxygen, shaded lawn mats, and zero UV radiation damage.");
                tvEveningWindowBadge.setText("05:00 – 06:30 PM (90/100)");
                tvEveningWindowDesc.setText("Gentle sunset ambient breeze and calm lawn park atmosphere.");
                break;

            case "trek":
                // Trekking is sensitive to rain and mud
                if (rain > 0) score -= 35;
                if (temp > 33) score -= 25;
                if (wind > 30) score -= 15;

                sweatRateMl = 480 + Math.max(0, (temp - 20) * 30);
                dragText = String.format(Locale.getDefault(), "%.1f km/h Trail Breeze", wind);

                tvCurrentConditions.setText(String.format(Locale.getDefault(), "%.0f°C • Trail Sightlines 10 km • Humidity %d%%", temp, humidity));

                if (rain > 0) {
                    tvFitnessHeroMsg.setText("🚶 Muddy Trails: Active rain on trekking paths. Use trekking poles and waterproof trail shoes.");
                } else {
                    tvFitnessHeroMsg.setText("🚶 Optimal Trail Trekking Weather! Clear trail sightlines and fresh outdoor mountain air.");
                }

                tvMorningWindowBadge.setText("06:00 – 09:00 AM (94/100)");
                tvMorningWindowDesc.setText("Clear trail visibility, cool hill shade, and low risk of afternoon heat exhaustion.");
                tvEveningWindowBadge.setText("04:30 – 06:30 PM (86/100)");
                tvEveningWindowDesc.setText("Pre-dusk return window with comfortable temperature and clear footing.");
                break;

            case "running":
            default:
                // Running is sensitive to heat and humidity
                if (temp > 33) score -= 30;
                else if (temp > 28) score -= 15;
                if (humidity > 80) score -= 15;
                if (rain > 0) score -= 20;

                sweatRateMl = 650 + Math.max(0, (temp - 20) * 40) + (humidity > 70 ? 100 : 0);
                dragText = String.format(Locale.getDefault(), "%.1f km/h Runner Resistance", wind);

                tvCurrentConditions.setText(String.format(Locale.getDefault(), "%.0f°C • Wind %.1f km/h • Humidity %d%%", temp, wind, humidity));

                if (rain > 0) {
                    tvFitnessHeroMsg.setText("🌧️ Active Rain: Opt for indoor treadmill or wear waterproof running shell with anti-slip soles.");
                } else if (temp > 33) {
                    tvFitnessHeroMsg.setText("🔥 High Solar Heat: Shift long endurance runs to early morning before 08:30 AM.");
                } else {
                    tvFitnessHeroMsg.setText("🏃 Crisp & Clear! Optimal temperature for marathon training, tempo runs, and hill intervals.");
                }

                tvMorningWindowBadge.setText("06:00 – 08:30 AM (95/100)");
                tvMorningWindowDesc.setText("Crisp 21°C air, lowest particulate pollution, and zero UV damage risk.");
                tvEveningWindowBadge.setText("05:30 – 07:30 PM (88/100)");
                tvEveningWindowDesc.setText("Cooling breeze, diminished solar radiation, and safe road temperatures.");
                break;
        }

        if (score < 20) score = 20;

        tvFitnessScore.setText(score + " / 100");
        String badge = score >= 80 ? "OPTIMAL" : (score >= 60 ? "GOOD" : "CHALLENGING");
        tvFitnessScoreBadge.setText(badge);
        tvFitnessScoreBadge.setBackgroundResource(score >= 70 ? R.drawable.pill_badge_green : R.drawable.pill_badge_yellow);

        // Sweat Rate
        tvSweatRate.setText(String.format(Locale.getDefault(), "%.0f ml / hr", sweatRateMl));

        // Heat Strain
        if (temp > 34 || c.apparentTemperature > 36) {
            tvHeatStrain.setText("⚠️ High Strain");
            tvHeatStrain.setTextColor(ContextCompat.getColor(this, R.color.status_danger));
        } else if (temp > 28) {
            tvHeatStrain.setText("Moderate Strain");
            tvHeatStrain.setTextColor(ContextCompat.getColor(this, R.color.status_warning));
        } else {
            tvHeatStrain.setText("Low Strain");
            tvHeatStrain.setTextColor(ContextCompat.getColor(this, R.color.status_success));
        }

        // Cycling / Drag Wind
        tvCyclingWind.setText(dragText);

        // UV Safe exposure
        if (uv > 8.0) {
            tvUvSafeExposure.setText("15 mins max");
            tvUvSafeExposure.setTextColor(ContextCompat.getColor(this, R.color.status_danger));
        } else if (uv > 5.0) {
            tvUvSafeExposure.setText("30 mins max");
            tvUvSafeExposure.setTextColor(ContextCompat.getColor(this, R.color.status_warning));
        } else {
            tvUvSafeExposure.setText("60+ mins safe");
            tvUvSafeExposure.setTextColor(ContextCompat.getColor(this, R.color.status_success));
        }
    }

    private void populateFallbackData() {
        switch (selectedSport) {
            case "cycling":
                tvSportTitle.setText("OUTDOOR CYCLING SUITABILITY");
                tvFitnessScore.setText("94 / 100");
                tvFitnessScoreBadge.setText("OPTIMAL");
                tvCurrentConditions.setText("25°C • Wind 8 km/h • Road Grip Index 92");
                tvFitnessHeroMsg.setText("Low aerodynamic drag and dry pavement. Ideal for long distance road biking.");
                tvSweatRate.setText("750 ml / hr");
                tvHeatStrain.setText("Low Strain");
                tvCyclingWind.setText("8.0 km/h Headwind Drag");
                tvUvSafeExposure.setText("60+ mins safe");
                tvMorningWindowBadge.setText("05:30 – 08:00 AM (98/100)");
                tvMorningWindowDesc.setText("Low highway traffic, smooth asphalt temperatures, and minimal wind drag.");
                tvEveningWindowBadge.setText("06:00 – 08:00 PM (88/100)");
                tvEveningWindowDesc.setText("Sunset cooling breeze with low sun glare on open biking corridors.");
                break;
            case "yoga":
                tvSportTitle.setText("OUTDOOR YOGA SUITABILITY");
                tvFitnessScore.setText("98 / 100");
                tvFitnessScoreBadge.setText("OPTIMAL");
                tvCurrentConditions.setText("24°C • UV 3.2 • Shaded Park Air");
                tvFitnessHeroMsg.setText("Serene park air and comfortable lawn temperatures for mat practice.");
                tvSweatRate.setText("380 ml / hr");
                tvHeatStrain.setText("Low Strain");
                tvCyclingWind.setText("5.0 km/h Gentle Breeze");
                tvUvSafeExposure.setText("60+ mins safe");
                tvMorningWindowBadge.setText("06:30 – 08:30 AM (96/100)");
                tvMorningWindowDesc.setText("Fresh park oxygen, shaded lawn mats, and zero UV radiation damage.");
                tvEveningWindowBadge.setText("05:00 – 06:30 PM (90/100)");
                tvEveningWindowDesc.setText("Gentle sunset ambient breeze and calm lawn park atmosphere.");
                break;
            case "trek":
                tvSportTitle.setText("OUTDOOR TREKKING SUITABILITY");
                tvFitnessScore.setText("88 / 100");
                tvFitnessScoreBadge.setText("OPTIMAL");
                tvCurrentConditions.setText("24°C • Trail Sightlines 10 km • Humidity 50%");
                tvFitnessHeroMsg.setText("Clear mountain trail sightlines and fresh outdoor air for hill trekking.");
                tvSweatRate.setText("520 ml / hr");
                tvHeatStrain.setText("Low Strain");
                tvCyclingWind.setText("10.0 km/h Trail Breeze");
                tvUvSafeExposure.setText("45+ mins safe");
                tvMorningWindowBadge.setText("06:00 – 09:00 AM (94/100)");
                tvMorningWindowDesc.setText("Clear trail visibility, cool hill shade, and low risk of afternoon heat exhaustion.");
                tvEveningWindowBadge.setText("04:30 – 06:30 PM (86/100)");
                tvEveningWindowDesc.setText("Pre-dusk return window with comfortable temperature and clear footing.");
                break;
            case "running":
            default:
                tvSportTitle.setText("OUTDOOR RUNNING SUITABILITY");
                tvFitnessScore.setText("92 / 100");
                tvFitnessScoreBadge.setText("OPTIMAL");
                tvCurrentConditions.setText("25°C • Wind 10 km/h • Humidity 55%");
                tvFitnessHeroMsg.setText("Clear skies and mild temperatures. Perfect for endurance running and marathon training.");
                tvSweatRate.setText("650 ml / hr");
                tvHeatStrain.setText("Low Strain");
                tvCyclingWind.setText("10.0 km/h Runner Resistance");
                tvUvSafeExposure.setText("45+ mins safe");
                tvMorningWindowBadge.setText("06:00 – 08:30 AM (95/100)");
                tvMorningWindowDesc.setText("Crisp 21°C air, lowest particulate pollution, and zero UV damage risk.");
                tvEveningWindowBadge.setText("05:30 – 07:30 PM (88/100)");
                tvEveningWindowDesc.setText("Cooling breeze, diminished solar radiation, and safe road temperatures.");
                break;
        }
    }
}
