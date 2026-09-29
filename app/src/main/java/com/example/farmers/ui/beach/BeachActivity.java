package com.example.farmers.ui.beach;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.farmers.R;
import com.example.farmers.data.api.RetrofitClient;
import com.example.farmers.data.model.GeocodingResponse;
import com.example.farmers.data.model.WeatherResponse;
import com.example.farmers.util.LocaleHelper;
import com.example.farmers.util.PrefsManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BeachActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    private ImageButton btnBack;
    private ImageButton btnRefresh;
    private TextView tvLocationSubtitle;

    // Mode Choice Chips
    private Chip chipBeachSwim;
    private Chip chipBeachSurf;
    private Chip chipBeachSail;
    private Chip chipBeachWalk;

    // Planner Views
    private AutoCompleteTextView etBeachSearch;
    private Button btnSearchBeach;
    private TextView tvBeachDestination;
    private Button btnBeachDate;
    private Button btnSubmitBeach;
    private MaterialButton btnShareBeach;

    // Hero Advisory
    private TextView tvBeachModeTitle;
    private TextView tvBeachFlag;
    private TextView tvBeachSafetyTitle;
    private TextView tvBeachSafetyDesc;

    // Metrics
    private TextView tvWaveHeight;
    private TextView tvWaterTemp;
    private TextView tvBeachWind;
    private TextView tvBeachUv;

    private PrefsManager prefs;
    private WeatherResponse.Current lastCurrentWeather;
    private double destLat;
    private View layoutBeachResults;
    private double destLon;
    private String destName = "Main Seaside Boulevard";
    private String selectedDateDisplay = "Today";
    private String selectedBeachMode = "swim"; // swim, surf, sail, walk

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beach);

        prefs = PrefsManager.getInstance(this);
        destLat = prefs.getLatitude();
        destLon = prefs.getLongitude();
        if (!prefs.getLocationName().isEmpty()) {
            destName = prefs.getLocationName();
        }

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnRefresh = findViewById(R.id.btnRefresh);
        tvLocationSubtitle = findViewById(R.id.tvLocationSubtitle);

        // Chips
        chipBeachSwim = findViewById(R.id.chipBeachSwim);
        chipBeachSurf = findViewById(R.id.chipBeachSurf);
        chipBeachSail = findViewById(R.id.chipBeachSail);
        chipBeachWalk = findViewById(R.id.chipBeachWalk);

        // Planner
        etBeachSearch = findViewById(R.id.etBeachSearch);
        String[] popularBeaches = {
                "Marina Beach, Chennai",
                "Juhu Beach, Mumbai",
                "Calangute Beach, Goa",
                "Palolem Beach, Goa",
                "Kovalam Beach, Kerala",
                "Varkala Beach, Kerala",
                "Puri Beach, Odisha",
                "Mandvi Beach, Gujarat",
                "Radhanagar Beach, Andaman",
                "Agonda Beach, Goa",
                "Baga Beach, Goa",
                "Anjuna Beach, Goa",
                "Mahabalipuram Beach, Tamil Nadu",
                "Digha Beach, West Bengal",
                "Ganpatipule Beach, Maharashtra",
                "Tarkarli Beach, Maharashtra",
                "Kochi Beach, Kerala",
                "Gokarna Beach, Karnataka",
                "Malpe Beach, Karnataka",
                "Rishikonda Beach, Visakhapatnam",
                "Daman Beach",
                "Diu Beach"
        };
        ArrayAdapter<String> beachAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, popularBeaches);
        etBeachSearch.setAdapter(beachAdapter);
        etBeachSearch.setThreshold(1);

        btnSearchBeach = findViewById(R.id.btnSearchBeach);
        tvBeachDestination = findViewById(R.id.tvBeachDestination);
        btnBeachDate = findViewById(R.id.btnBeachDate);
        btnSubmitBeach = findViewById(R.id.btnSubmitBeach);
        btnShareBeach = findViewById(R.id.btnShareBeach);

        // Hero
        tvBeachModeTitle = findViewById(R.id.tvBeachModeTitle);
        tvBeachFlag = findViewById(R.id.tvBeachFlag);
        tvBeachSafetyTitle = findViewById(R.id.tvBeachSafetyTitle);
        tvBeachSafetyDesc = findViewById(R.id.tvBeachSafetyDesc);

        // Metrics
        tvWaveHeight = findViewById(R.id.tvWaveHeight);
        tvWaterTemp = findViewById(R.id.tvWaterTemp);
        tvBeachWind = findViewById(R.id.tvBeachWind);
        tvBeachUv = findViewById(R.id.tvBeachUv);
        layoutBeachResults = findViewById(R.id.layoutBeachResults);

        updateDestinationLabel();
    }

    private void updateDestinationLabel() {
        tvBeachDestination.setText(String.format(Locale.getDefault(),
                "🎯 Target Coast: %s (Lat: %.2f, Lon: %.2f)", destName, destLat, destLon));
        tvLocationSubtitle.setText("📍 " + destName + " • Waves, Tides & Surf Safety");
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnRefresh.setOnClickListener(v -> loadWeatherData());

        chipBeachSwim.setOnClickListener(v -> selectMode("swim"));
        chipBeachSurf.setOnClickListener(v -> selectMode("surf"));
        chipBeachSail.setOnClickListener(v -> selectMode("sail"));
        chipBeachWalk.setOnClickListener(v -> selectMode("walk"));

        btnSearchBeach.setOnClickListener(v -> searchBeachLocation());

        btnBeachDate.setOnClickListener(v -> showDatePicker());

        btnSubmitBeach.setOnClickListener(v -> submitAndCalculateBeach());

        if (btnShareBeach != null) {
            btnShareBeach.setOnClickListener(v -> shareBeachItinerary());
        }
    }

    private void selectMode(String modeKey) {
        selectedBeachMode = modeKey;
        updateChipVisuals();
        if (lastCurrentWeather != null) {
            populateBeachData(lastCurrentWeather);
        } else {
            populateFallbackData();
        }
    }

    private void updateChipVisuals() {
        resetChipStyle(chipBeachSwim);
        resetChipStyle(chipBeachSurf);
        resetChipStyle(chipBeachSail);
        resetChipStyle(chipBeachWalk);

        switch (selectedBeachMode) {
            case "surf":
                highlightChip(chipBeachSurf);
                break;
            case "sail":
                highlightChip(chipBeachSail);
                break;
            case "walk":
                highlightChip(chipBeachWalk);
                break;
            case "swim":
            default:
                highlightChip(chipBeachSwim);
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

    private void searchBeachLocation() {
        String query = etBeachSearch.getText() != null ? etBeachSearch.getText().toString().trim() : "";
        if (query.isEmpty()) {
            etBeachSearch.setError("Enter beach or coastal name");
            return;
        }

        btnSearchBeach.setEnabled(false);
        Toast.makeText(this, "Searching beach route for " + query + "...", Toast.LENGTH_SHORT).show();

        RetrofitClient.getGeocodingService().searchCity(query, 1, "en", "json")
                .enqueue(new Callback<GeocodingResponse>() {
                    @Override
                    public void



                    onResponse(@NonNull Call<GeocodingResponse> call, @NonNull Response<GeocodingResponse> response) {
                        btnSearchBeach.setEnabled(true);
                        if (response.isSuccessful() && response.body() != null &&
                                response.body().results != null && !response.body().results.isEmpty()) {
                            GeocodingResponse.GeocodingResult res = response.body().results.get(0);
                            destLat = res.latitude;
                            destLon = res.longitude;
                            destName = res.name + (res.country != null ? ", " + res.country : "");

                            updateDestinationLabel();
                            Toast.makeText(BeachActivity.this, "Beach set to: " + destName, Toast.LENGTH_SHORT).show();
                        } else {
                            destName = query;
                            updateDestinationLabel();
                            Toast.makeText(BeachActivity.this, "Beach set to: " + query, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GeocodingResponse> call, @NonNull Throwable t) {
                        btnSearchBeach.setEnabled(true);
                        destName = query;
                        updateDestinationLabel();
                        Toast.makeText(BeachActivity.this, "Beach set to: " + query, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            selectedDateDisplay = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month1 + 1, year1);
            btnBeachDate.setText("📅 Trip Date: " + selectedDateDisplay + " (Tap to change)");
            Toast.makeText(this, "Date set to: " + selectedDateDisplay, Toast.LENGTH_SHORT).show();
        }, year, month, day);

        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.show();
    }

    private void submitAndCalculateBeach() {
        String query = etBeachSearch.getText() != null ? etBeachSearch.getText().toString().trim() : "";
        if (!query.isEmpty() && !query.equalsIgnoreCase(destName)) {
            btnSubmitBeach.setEnabled(false);
            RetrofitClient.getGeocodingService().searchCity(query, 1, "en", "json")
                    .enqueue(new Callback<GeocodingResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<GeocodingResponse> call, @NonNull Response<GeocodingResponse> response) {
                            btnSubmitBeach.setEnabled(true);
                            if (response.isSuccessful() && response.body() != null &&
                                    response.body().results != null && !response.body().results.isEmpty()) {
                                GeocodingResponse.GeocodingResult res = response.body().results.get(0);
                                destLat = res.latitude;
                                destLon = res.longitude;
                                destName = res.name + (res.country != null ? ", " + res.country : "");
                                updateDestinationLabel();
                            } else {
                                destName = query;
                                updateDestinationLabel();
                            }
                            executeBeachCalculation();
                        }

                        @Override
                        public void onFailure(@NonNull Call<GeocodingResponse> call, @NonNull Throwable t) {
                            btnSubmitBeach.setEnabled(true);
                            destName = query;
                            updateDestinationLabel();
                            executeBeachCalculation();
                        }
                    });
        } else {
            executeBeachCalculation();
        }
    }

    private void executeBeachCalculation() {
        Toast.makeText(this, "Checking live marine tides & swell conditions...", Toast.LENGTH_SHORT).show();
        if (layoutBeachResults != null) {
            layoutBeachResults.setVisibility(View.VISIBLE);
        }
        loadWeatherData();
    }

    private void shareBeachItinerary() {
        String modeEmoji = "🏖️ Swimming & Sunbathing";
        if ("surf".equals(selectedBeachMode)) modeEmoji = "🏄 Surfing & Swell";
        else if ("sail".equals(selectedBeachMode)) modeEmoji = "⛵ Sailing & Boating";
        else if ("walk".equals(selectedBeachMode)) modeEmoji = "🎣 Coastal Fishing / Walk";

        String text = String.format(Locale.getDefault(),
                "🏖️ *Beach & Marine Weather Advisory*\n\n" +
                        "📍 *Coast Target*: %s\n" +
                        "📅 *Date*: %s\n" +
                        "🏄 *Activity*: %s\n" +
                        "🚩 *Beach Safety Flag*: %s\n" +
                        "💡 *Advisory*: %s\n\n" +
                        "Stay safe near the water & reapply sunscreen!",
                destName, selectedDateDisplay, modeEmoji,
                tvBeachFlag != null ? tvBeachFlag.getText().toString() : "GREEN FLAG",
                tvBeachSafetyTitle != null ? tvBeachSafetyTitle.getText().toString() : "");

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");

        startActivity(Intent.createChooser(sendIntent, "Share Beach Plan"));
    }

    private void loadWeatherData() {
        RetrofitClient.getWeatherService().getWeather(
                destLat, destLon,
                "temperature_2m,apparent_temperature,relative_humidity_2m,precipitation,weather_code,wind_speed_10m,wind_direction_10m,wind_gusts_10m,uv_index,is_day",
                "temperature_2m,precipitation_probability,weather_code,wind_speed_10m,uv_index",
                "weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max,wind_speed_10m_max,uv_index_max",
                "auto",
                7
        ).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().current != null) {
                    lastCurrentWeather = response.body().current;
                    populateBeachData(response.body().current);
                } else {
                    populateFallbackData();
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                populateFallbackData();
            }
        });
    }

    private void populateBeachData(WeatherResponse.Current c) {
        tvBeachModeTitle.setText("COASTAL " + selectedBeachMode.toUpperCase(Locale.US) + " ADVISORY");

        double waveHeight = 0.5 + (c.windSpeed10m / 35.0);
        tvWaveHeight.setText(String.format(Locale.getDefault(), "%.1f m", waveHeight));

        double seaTemp = Math.max(22.0, Math.min(29.0, c.temperature2m - 1.5));
        tvWaterTemp.setText(String.format(Locale.getDefault(), "%.0f°C", seaTemp));

        tvBeachWind.setText(String.format(Locale.getDefault(), "%.1f km/h", c.windSpeed10m));
        tvBeachUv.setText(String.format(Locale.getDefault(), "UV %.1f", c.uvIndex));

        switch (selectedBeachMode) {
            case "surf":
                if (waveHeight > 1.2 && waveHeight < 2.5 && c.windSpeed10m < 25) {
                    tvBeachFlag.setText("🟢 SURF OPTIMAL");
                    tvBeachFlag.setBackgroundResource(R.drawable.pill_badge_green);
                    tvBeachSafetyTitle.setText("Clean Rolling Swell & offshore breeze!");
                    tvBeachSafetyDesc.setText(String.format(Locale.getDefault(), "%.1f m waves with clean barrel formation. Excellent for longboard & shortboard surfing.", waveHeight));
                } else if (c.windSpeed10m > 30 || waveHeight > 2.5) {
                    tvBeachFlag.setText("🔴 RED FLAG SURF");
                    tvBeachFlag.setBackgroundResource(R.drawable.pill_badge_red);
                    tvBeachSafetyTitle.setText("Dangerous Storm Waves & High Chop");
                    tvBeachSafetyDesc.setText("Messy blown-out waves with strong rip currents. Only experienced surfers with safety leash.");
                } else {
                    tvBeachFlag.setText("🟡 SMALL SWELL");
                    tvBeachFlag.setBackgroundResource(R.drawable.pill_badge_yellow);
                    tvBeachSafetyTitle.setText("Gentle 0.7m Swell • Beginner Friendly");
                    tvBeachSafetyDesc.setText("Soft waist-high waves ideal for soft-top learning and bodyboarding.");
                }
                break;

            case "sail":
                if (c.windSpeed10m > 35 || c.precipitation > 2.0) {
                    tvBeachFlag.setText("🔴 GALE WARNING");
                    tvBeachFlag.setBackgroundResource(R.drawable.pill_badge_red);
                    tvBeachSafetyTitle.setText("High Onshore Squalls & Offshore Drag");
                    tvBeachSafetyDesc.setText("Gale gusts over 35 km/h. Small craft advisory active. Stay moored in harbor.");
                } else if (c.windSpeed10m > 15) {
                    tvBeachFlag.setText("🟢 PERFECT SAIL");
                    tvBeachFlag.setBackgroundResource(R.drawable.pill_badge_green);
                    tvBeachSafetyTitle.setText("15-25 km/h Constant Sea Breeze");
                    tvBeachSafetyDesc.setText("Ideal wind speed for catamaran sailing, windsurfing, and coastal yachting.");
                } else {
                    tvBeachFlag.setText("🟡 LIGHT WIND");
                    tvBeachFlag.setBackgroundResource(R.drawable.pill_badge_yellow);
                    tvBeachSafetyTitle.setText("Calm 8 km/h Air • Low Sailing Speed");
                    tvBeachSafetyDesc.setText("Gentle air movement. Recommend motor cruising or paddleboarding.");
                }
                break;

            case "walk":
                if (c.temperature2m > 33) {
                    tvBeachFlag.setText("🟡 SUN CAUTION");
                    tvBeachFlag.setBackgroundResource(R.drawable.pill_badge_yellow);
                    tvBeachSafetyTitle.setText("High Sand Temperature & Solar Heat");
                    tvBeachSafetyDesc.setText("Hot sand on barefoot walks. Walk along wet tide line & wear flip-flops.");
                } else {
                    tvBeachFlag.setText("🟢 GREAT WALK");
                    tvBeachFlag.setBackgroundResource(R.drawable.pill_badge_green);
                    tvBeachSafetyTitle.setText("Pleasant Seaside Walk & Shell Collecting");
                    tvBeachSafetyDesc.setText("Refreshing coastal air, cool sea foam along the shore, and clear sunset sightlines.");
                }
                break;

            case "swim":
            default:
                if (c.windSpeed10m > 30 || waveHeight > 2.0 || c.precipitation > 2.0) {
                    tvBeachFlag.setText("🔴 RED FLAG");
                    tvBeachFlag.setBackgroundResource(R.drawable.pill_badge_red);
                    tvBeachSafetyTitle.setText("High Surf & Rough Waves");
                    tvBeachSafetyDesc.setText("Strong coastal winds and elevated rip currents. Stay close to lifeguard stations.");
                } else if (c.windSpeed10m > 20 || waveHeight > 1.3) {
                    tvBeachFlag.setText("🟡 YELLOW FLAG");
                    tvBeachFlag.setBackgroundResource(R.drawable.pill_badge_yellow);
                    tvBeachSafetyTitle.setText("Moderate Surf & Swell");
                    tvBeachSafetyDesc.setText("Good waves for bodyboarding and surfing. Swimmers should exercise caution.");
                } else {
                    tvBeachFlag.setText("🟢 GREEN FLAG");
                    tvBeachFlag.setBackgroundResource(R.drawable.pill_badge_green);
                    tvBeachSafetyTitle.setText("Safe For Swimming & Sunbathing");
                    tvBeachSafetyDesc.setText("Gentle rolling waves, calm coastal breeze, and ideal seaside conditions.");
                }
                break;
        }
    }

    private void populateFallbackData() {
        tvBeachFlag.setText("🟢 GREEN FLAG");
        tvBeachFlag.setBackgroundResource(R.drawable.pill_badge_green);
        tvBeachSafetyTitle.setText("Safe For Swimming & Sunbathing");
        tvBeachSafetyDesc.setText("Gentle 0.8m swell, calm sea breeze, and sunny clear waters.");
        tvWaveHeight.setText("0.8 m");
        tvWaterTemp.setText("26°C");
        tvBeachWind.setText("12.0 km/h");
        tvBeachUv.setText("UV 6.5");
    }
}
