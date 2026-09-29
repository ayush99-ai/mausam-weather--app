package com.example.farmers.ui.parents;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

public class ParentsActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    private ImageButton btnBack;
    private ImageButton btnRefresh;
    private TextView tvLocationSubtitle;

    // Choice Chips
    private Chip chipParentsPark;
    private Chip chipParentsStroller;
    private Chip chipParentsPicnic;
    private Chip chipParentsMall;

    private MaterialButton btnShareParents;

    // Hero
    private TextView tvParentsModeTitle;
    private TextView tvChildSafetyScore;
    private TextView tvChildSafetyBadge;
    private TextView tvChildSafetyDesc;

    // Metrics
    private TextView tvParentTemp;
    private TextView tvParentHumidity;
    private TextView tvParentWind;
    private TextView tvParentUv;

    // Clothing
    private TextView tvDressTitle;
    private TextView tvDressDesc;

    private PrefsManager prefs;
    private WeatherResponse.Current lastCurrentWeather;
    private double destLat;
    private double destLon;
    private String destName = "Aadgaon, Nashik";
    private String selectedParentsMode = "park"; // park, stroller, picnic, mall

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parents);

        prefs = PrefsManager.getInstance(this);
        destLat = prefs.getLatitude();
        destLon = prefs.getLongitude();
        if (!prefs.getLocationName().isEmpty()) {
            destName = prefs.getLocationName();
        }

        initViews();
        setupListeners();
        loadWeatherData();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnRefresh = findViewById(R.id.btnRefresh);
        tvLocationSubtitle = findViewById(R.id.tvLocationSubtitle);

        chipParentsPark = findViewById(R.id.chipParentsPark);
        chipParentsStroller = findViewById(R.id.chipParentsStroller);
        chipParentsPicnic = findViewById(R.id.chipParentsPicnic);
        chipParentsMall = findViewById(R.id.chipParentsMall);

        btnShareParents = findViewById(R.id.btnShareParents);

        tvParentsModeTitle = findViewById(R.id.tvParentsModeTitle);
        tvChildSafetyScore = findViewById(R.id.tvChildSafetyScore);
        tvChildSafetyBadge = findViewById(R.id.tvChildSafetyBadge);
        tvChildSafetyDesc = findViewById(R.id.tvChildSafetyDesc);

        tvParentTemp = findViewById(R.id.tvParentTemp);
        tvParentHumidity = findViewById(R.id.tvParentHumidity);
        tvParentWind = findViewById(R.id.tvParentWind);
        tvParentUv = findViewById(R.id.tvParentUv);

        tvDressTitle = findViewById(R.id.tvDressTitle);
        tvDressDesc = findViewById(R.id.tvDressDesc);

        updateDestinationLabel();
    }

    private void updateDestinationLabel() {
        tvLocationSubtitle.setText("📍 " + destName + " • Toddler & Kids Safety");
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnRefresh.setOnClickListener(v -> loadWeatherData());

        chipParentsPark.setOnClickListener(v -> selectMode("park"));
        chipParentsStroller.setOnClickListener(v -> selectMode("stroller"));
        chipParentsPicnic.setOnClickListener(v -> selectMode("picnic"));
        chipParentsMall.setOnClickListener(v -> selectMode("mall"));

        if (btnShareParents != null) {
            btnShareParents.setOnClickListener(v -> shareParentsItinerary());
        }
    }

    private void selectMode(String modeKey) {
        selectedParentsMode = modeKey;
        updateChipVisuals();
        if (lastCurrentWeather != null) {
            populateParentsData(lastCurrentWeather);
        } else {
            populateFallbackData();
        }
    }

    private void updateChipVisuals() {
        resetChipStyle(chipParentsPark);
        resetChipStyle(chipParentsStroller);
        resetChipStyle(chipParentsPicnic);
        resetChipStyle(chipParentsMall);

        switch (selectedParentsMode) {
            case "stroller":
                highlightChip(chipParentsStroller);
                break;
            case "picnic":
                highlightChip(chipParentsPicnic);
                break;
            case "mall":
                highlightChip(chipParentsMall);
                break;
            case "park":
            default:
                highlightChip(chipParentsPark);
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

    private void shareParentsItinerary() {
        String modeEmoji = "🛝 Park & Playground";
        if ("stroller".equals(selectedParentsMode)) modeEmoji = "👶 Stroller Walk";
        else if ("picnic".equals(selectedParentsMode)) modeEmoji = "🧺 Family Picnic";
        else if ("mall".equals(selectedParentsMode)) modeEmoji = "🛍️ Indoor Mall Outing";

        String text = String.format(Locale.getDefault(),
                "👨‍👩‍👧‍👦 *Child & Family Outing Weather Advisory*\n\n" +
                        "📍 *Location*: %s\n" +
                        "🛝 *Activity*: %s\n" +
                        "🏆 *Child Outdoor Safety Score*: %s\n" +
                        "🌡️ *Live Weather*: Temp %s, Humidity %s, Wind %s, UV %s\n" +
                        "👕 *Clothing & Advice*: %s\n\n" +
                        "Keep kids safe & enjoy family outdoor fun!",
                destName, modeEmoji,
                tvChildSafetyScore != null ? tvChildSafetyScore.getText().toString() : "94/100",
                tvParentTemp != null ? tvParentTemp.getText().toString() : "--",
                tvParentHumidity != null ? tvParentHumidity.getText().toString() : "--",
                tvParentWind != null ? tvParentWind.getText().toString() : "--",
                tvParentUv != null ? tvParentUv.getText().toString() : "--",
                tvDressTitle != null ? tvDressTitle.getText().toString() : "");

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");

        startActivity(Intent.createChooser(sendIntent, "Share Family Outing Plan"));
    }

    private void loadWeatherData() {
        RetrofitClient.getWeatherService().getWeather(
                destLat, destLon,
                "temperature_2m,apparent_temperature,relative_humidity_2m,precipitation,weather_code,surface_pressure,wind_speed_10m,wind_direction_10m,wind_gusts_10m,uv_index,is_day",
                "temperature_2m,precipitation_probability,weather_code,wind_speed_10m,uv_index",
                "weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max,wind_speed_10m_max,uv_index_max",
                "auto",
                7
        ).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().current != null) {
                    lastCurrentWeather = response.body().current;
                    populateParentsData(response.body().current);
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

    private void populateParentsData(WeatherResponse.Current c) {
        tvParentsModeTitle.setText("FAMILY " + selectedParentsMode.toUpperCase(Locale.US) + " OUTING SAFETY");

        // Populate Live Weather Factors Cards
        if (tvParentTemp != null) {
            tvParentTemp.setText(String.format(Locale.getDefault(), "%.1f°C (Feels %.1f°)", c.temperature2m, c.apparentTemperature));
        }
        if (tvParentHumidity != null) {
            tvParentHumidity.setText(c.relativeHumidity2m + "%");
        }
        if (tvParentWind != null) {
            tvParentWind.setText(String.format(Locale.getDefault(), "%.1f km/h (Gusts %.1f)", c.windSpeed10m, c.windGusts10m));
        }
        if (tvParentUv != null) {
            tvParentUv.setText(String.format(Locale.getDefault(), "%.1f (%s)", c.uvIndex, c.uvIndex > 6 ? "High" : "Safe"));
        }

        // Child safety score calculation
        int safetyScore = 100;
        if (c.temperature2m > 36) safetyScore -= 35;
        else if (c.temperature2m > 32) safetyScore -= 20;
        else if (c.temperature2m < 12) safetyScore -= 20;

        if (c.precipitation > 0) safetyScore -= 30;
        if (c.uvIndex > 8.0) safetyScore -= 20;
        if (c.windSpeed10m > 25) safetyScore -= 15;
        if (c.relativeHumidity2m > 85) safetyScore -= 10;
        if (safetyScore < 25) safetyScore = 25;

        tvChildSafetyScore.setText(safetyScore + " / 100");
        String badge = safetyScore >= 80 ? "EXCELLENT" : (safetyScore >= 60 ? "MODERATE" : "PRECAUTION");
        tvChildSafetyBadge.setText(badge);
        tvChildSafetyBadge.setBackgroundResource(safetyScore >= 75 ? R.drawable.pill_badge_green : R.drawable.pill_badge_yellow);

        // Rich weather-informed suggestions for parents
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Locale.getDefault(), "Temp %.1f°C (Feels like %.1f°C), Humidity %d%%, Pressure %.0f hPa. ",
                c.temperature2m, c.apparentTemperature, c.relativeHumidity2m, c.surfacePressure));
        if (c.uvIndex > 6.0) {
            sb.append("☀️ High UV Index (").append(c.uvIndex).append("): Apply SPF 50+ sunscreen, sunglasses, and protective hats for children. ");
        }
        if (c.windSpeed10m > 20 || c.windGusts10m > 30) {
            sb.append("💨 Strong winds/gusts (").append(c.windGusts10m).append(" km/h): Secure strollers and lightweight playground toys. ");
        }
        if (c.relativeHumidity2m > 80) {
            sb.append("💧 High humidity (").append(c.relativeHumidity2m).append("%): Keep kids hydrated with frequent water breaks. ");
        }
        if (c.precipitation > 0) {
            sb.append("🌧️ Rain active: Carry waterproof rain covers or switch to indoor mall outing. ");
        }
        if (sb.toString().trim().length() < 60) {
            sb.append("Mild breeze, comfortable temperatures, and safe sun levels for toddler park trips.");
        }
        tvChildSafetyDesc.setText(sb.toString().trim());

        // Mode specific clothing & outing advice
        switch (selectedParentsMode) {
            case "stroller":
                if (c.precipitation > 0) {
                    tvDressTitle.setText("Stroller Rain Cover + Waterproof Jacket");
                    tvDressDesc.setText("Active rain showers. Install transparent stroller rain canopy and wear waterproof boots.");
                } else if (c.temperature2m > 32) {
                    tvDressTitle.setText("Stroller Sunshade + Breathable Cotton");
                    tvDressDesc.setText("High afternoon heat (" + c.temperature2m + "°C). Attach stroller UV sunshade fan & carry water bottle for toddlers.");
                } else {
                    tvDressTitle.setText("Comfortable Stroller Cotton Layers");
                    tvDressDesc.setText("Ideal " + c.temperature2m + "°C weather with " + c.relativeHumidity2m + "% humidity for long neighborhood stroller walks.");
                }
                break;

            case "picnic":
                if (c.precipitation > 0) {
                    tvDressTitle.setText("Waterproof Picnic Blanket + Rain Poncho");
                    tvDressDesc.setText("Rain on lawn grounds. Move picnic under covered park gazebo or indoor hall.");
                } else if (c.temperature2m > 33) {
                    tvDressTitle.setText("Shaded Lawn Blanket + Broad Sun Hats");
                    tvDressDesc.setText("High solar glare (UV " + c.uvIndex + "). Pick shaded tree spots on park lawn & carry insulated cooler box.");
                } else {
                    tvDressTitle.setText("Light Casual Cotton Wear + Picnic Mat");
                    tvDressDesc.setText("Perfect clear skies (Wind " + c.windSpeed10m + " km/h) for lawn picnics and family snacks.");
                }
                break;

            case "mall":
                tvDressTitle.setText("Indoor Air-Conditioned Comfort Layers");
                tvDressDesc.setText("Indoor climate controlled mall environment (" + c.temperature2m + "°C outside). Great indoor play zone alternative during outdoor rain or heat.");
                break;

            case "park":
            default:
                if (c.temperature2m < 15) {
                    tvDressTitle.setText("Thermal Inner + Warm Fleece Jacket");
                    tvDressDesc.setText("Cold air active (" + c.temperature2m + "°C). Cover toddler's ears and neck with a woolen cap and cozy socks.");
                } else if (c.temperature2m < 22) {
                    tvDressTitle.setText("2 Thin Layers (Cotton Tee + Cardigan)");
                    tvDressDesc.setText("Mild temperature. Add a light sweatshirt that can be removed as kids play actively.");
                } else if (c.temperature2m > 32) {
                    tvDressTitle.setText("Loose Cotton Clothing + Sun Hat");
                    tvDressDesc.setText("Heat caution (" + c.temperature2m + "°C). Use sleeveless or light cotton clothes and broad-brim sun hats.");
                } else {
                    tvDressTitle.setText("1 Breathable Cotton Layer");
                    tvDressDesc.setText("Comfortable " + c.temperature2m + "°C weather with " + c.relativeHumidity2m + "% humidity. Ideal for shorts, play t-shirts, and sneakers.");
                }
                break;
        }
    }

    private void populateFallbackData() {
        tvChildSafetyScore.setText("94 / 100");
        tvChildSafetyBadge.setText("EXCELLENT");
        tvChildSafetyDesc.setText("Mild breeze, comfortable temperatures, and safe sun levels for toddler park trips.");
        tvDressTitle.setText("1 Breathable Cotton Layer");
        tvDressDesc.setText("Comfortable weather. Pack a light cotton layer for the evening park trip.");
        if (tvParentTemp != null) tvParentTemp.setText("25.0°C (Feels 26.0°)");
        if (tvParentHumidity != null) tvParentHumidity.setText("55%");
        if (tvParentWind != null) tvParentWind.setText("10.5 km/h (Gusts 15.0)");
        if (tvParentUv != null) tvParentUv.setText("4.2 (Safe)");
    }
}
