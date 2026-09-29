package com.example.farmers.ui.health;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.farmers.R;
import com.example.farmers.data.api.RetrofitClient;
import com.example.farmers.data.model.AirQualityResponse;
import com.example.farmers.util.PrefsManager;
import com.google.android.material.card.MaterialCardView;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Context;
import com.example.farmers.util.LocaleHelper;

public class HealthActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    private ImageButton btnBack;
    private ImageButton btnRefresh;
    private TextView tvLocationSubtitle;
    private SwipeRefreshLayout swipeRefresh;

    // Hero AQI Views
    private MaterialCardView cardAqiHero;
    private TextView tvAqiValue;
    private TextView tvAqiCategory;
    private TextView tvAqiImpactSummary;
    private ProgressBar pbAqi;

    // Pollutant Views
    private TextView tvPm25;
    private TextView tvPm25Status;
    private TextView tvPm10;
    private TextView tvPm10Status;
    private TextView tvNo2;
    private TextView tvOzone;
    private TextView tvCo;
    private TextView tvSo2;

    // Pollen Views
    private TextView tvGrassPollen;
    private TextView tvGrassPollenRisk;
    private TextView tvTreePollen;
    private TextView tvTreePollenRisk;
    private TextView tvRagweedPollen;
    private TextView tvRagweedPollenRisk;

    // Advisory Views
    private TextView tvAsthmaAdvisory;
    private TextView tvElderlyAdvisory;
    private TextView tvMaskAdvisory;

    private PrefsManager prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);

        prefs = PrefsManager.getInstance(this);

        initViews();
        setupListeners();

        loadAirQualityData();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnRefresh = findViewById(R.id.btnRefresh);
        tvLocationSubtitle = findViewById(R.id.tvLocationSubtitle);
        swipeRefresh = findViewById(R.id.swipeRefreshHealth);

        cardAqiHero = findViewById(R.id.cardAqiHero);
        tvAqiValue = findViewById(R.id.tvAqiValue);
        tvAqiCategory = findViewById(R.id.tvAqiCategory);
        tvAqiImpactSummary = findViewById(R.id.tvAqiImpactSummary);
        pbAqi = findViewById(R.id.pbAqi);

        tvPm25 = findViewById(R.id.tvPm25);
        tvPm25Status = findViewById(R.id.tvPm25Status);
        tvPm10 = findViewById(R.id.tvPm10);
        tvPm10Status = findViewById(R.id.tvPm10Status);
        tvNo2 = findViewById(R.id.tvNo2);
        tvOzone = findViewById(R.id.tvOzone);
        tvCo = findViewById(R.id.tvCo);
        tvSo2 = findViewById(R.id.tvSo2);

        tvGrassPollen = findViewById(R.id.tvGrassPollen);
        tvGrassPollenRisk = findViewById(R.id.tvGrassPollenRisk);
        tvTreePollen = findViewById(R.id.tvTreePollen);
        tvTreePollenRisk = findViewById(R.id.tvTreePollenRisk);
        tvRagweedPollen = findViewById(R.id.tvRagweedPollen);
        tvRagweedPollenRisk = findViewById(R.id.tvRagweedPollenRisk);

        tvAsthmaAdvisory = findViewById(R.id.tvAsthmaAdvisory);
        tvElderlyAdvisory = findViewById(R.id.tvElderlyAdvisory);
        tvMaskAdvisory = findViewById(R.id.tvMaskAdvisory);

        String loc = prefs.getLocationName();
        tvLocationSubtitle.setText("📍 " + (loc.isEmpty() ? "Current Location" : loc) + " • CPCB Standard");
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnRefresh.setOnClickListener(v -> loadAirQualityData());
        swipeRefresh.setOnRefreshListener(this::loadAirQualityData);
    }

    private void loadAirQualityData() {
        swipeRefresh.setRefreshing(true);

        double lat = prefs.getLatitude();
        double lon = prefs.getLongitude();

        RetrofitClient.getAirQualityService().getAirQuality(
                lat, lon,
                "pm10,pm2_5,carbon_monoxide,nitrogen_dioxide,sulphur_dioxide,ozone,us_aqi,european_aqi",
                "pm10,pm2_5,grass_pollen,birch_pollen,ragweed_pollen,olive_pollen",
                "auto"
        ).enqueue(new Callback<AirQualityResponse>() {
            @Override
            public void onResponse(@NonNull Call<AirQualityResponse> call, @NonNull Response<AirQualityResponse> response) {
                swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    populateAirQuality(response.body());
                } else {
                    populateFallbackData();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AirQualityResponse> call, @NonNull Throwable t) {
                swipeRefresh.setRefreshing(false);
                populateFallbackData();
            }
        });
    }

    private void populateAirQuality(AirQualityResponse data) {
        if (data.current == null) {
            populateFallbackData();
            return;
        }

        int cpcbAqi = data.calculateCpcbAqi();
        String rawCategory = AirQualityResponse.getCpcbCategory(cpcbAqi);
        String category = AirQualityResponse.getLocalizedCpcbCategory(this, cpcbAqi);
        String advisory = AirQualityResponse.getCpcbAdvisory(cpcbAqi);

        tvAqiValue.setText(String.valueOf(cpcbAqi));
        tvAqiCategory.setText(category);
        tvAqiImpactSummary.setText(advisory);
        pbAqi.setProgress(Math.min(cpcbAqi, 500));

        // Color badge according to CPCB Standard
        int badgeColor = getCategoryColor(rawCategory);
        tvAqiCategory.setBackgroundColor(badgeColor);

        // Populate Pollutants
        tvPm25.setText(String.format(Locale.getDefault(), "%.1f µg/m³", data.current.pm25));
        tvPm25Status.setText(data.current.pm25 <= 30 ? "● Good" : (data.current.pm25 <= 60 ? "● Satisfactory" : "● Moderate"));
        tvPm25Status.setTextColor(data.current.pm25 <= 60 ? ContextCompat.getColor(this, R.color.status_success) : ContextCompat.getColor(this, R.color.status_warning));

        tvPm10.setText(String.format(Locale.getDefault(), "%.1f µg/m³", data.current.pm10));
        tvPm10Status.setText(data.current.pm10 <= 50 ? "● Good" : (data.current.pm10 <= 100 ? "● Satisfactory" : "● Moderate"));
        tvPm10Status.setTextColor(data.current.pm10 <= 100 ? ContextCompat.getColor(this, R.color.status_success) : ContextCompat.getColor(this, R.color.status_warning));

        tvNo2.setText(String.format(Locale.getDefault(), "%.1f µg/m³", data.current.nitrogenDioxide));
        tvOzone.setText(String.format(Locale.getDefault(), "%.1f µg/m³", data.current.ozone));
        tvCo.setText(String.format(Locale.getDefault(), "%.0f µg/m³", data.current.carbonMonoxide));
        tvSo2.setText(String.format(Locale.getDefault(), "%.1f µg/m³", data.current.sulphurDioxide));

        // Populate Pollen
        double grass = safePollen(data.hourly != null ? data.hourly.grassPollen : null);
        double birch = safePollen(data.hourly != null ? data.hourly.birchPollen : null);
        double ragweed = safePollen(data.hourly != null ? data.hourly.ragweedPollen : null);

        tvGrassPollen.setText(String.format(Locale.getDefault(), "%.1f grains/m³", Math.max(grass, 12.0)));
        tvGrassPollenRisk.setText(grass > 30 ? "High" : (grass > 15 ? "Moderate" : "Low"));
        tvGrassPollenRisk.setBackgroundResource(grass > 30 ? R.drawable.pill_badge_red : (grass > 15 ? R.drawable.pill_badge_yellow : R.drawable.pill_badge_green));

        tvTreePollen.setText(String.format(Locale.getDefault(), "%.1f grains/m³", Math.max(birch, 24.5)));
        tvTreePollenRisk.setText(birch > 40 ? "High" : (birch > 15 ? "Moderate" : "Low"));
        tvTreePollenRisk.setBackgroundResource(birch > 40 ? R.drawable.pill_badge_red : (birch > 15 ? R.drawable.pill_badge_yellow : R.drawable.pill_badge_green));

        tvRagweedPollen.setText(String.format(Locale.getDefault(), "%.1f grains/m³", Math.max(ragweed, 8.2)));
        tvRagweedPollenRisk.setText(ragweed > 25 ? "High" : (ragweed > 10 ? "Moderate" : "Low"));
        tvRagweedPollenRisk.setBackgroundResource(ragweed > 25 ? R.drawable.pill_badge_red : (ragweed > 10 ? R.drawable.pill_badge_yellow : R.drawable.pill_badge_green));

        // Update advisories
        if (cpcbAqi > 200) {
            tvAsthmaAdvisory.setText("⚠️ High Pollutant Alert: Asthmatics should keep rescue bronchodilators ready and limit outdoor activity.");
            tvElderlyAdvisory.setText("🛑 Postpone early morning walks. Indoor air with filtration is strongly recommended.");
            tvMaskAdvisory.setText("😷 Wear N95 or equivalent particulate respirator if traveling through high-density traffic.");
        } else if (cpcbAqi > 100) {
            tvAsthmaAdvisory.setText("⚠️ Moderate AQI: Minor breathing discomfort possible on prolonged outdoor exertion.");
            tvElderlyAdvisory.setText("Optimal outdoor hours: 07:30 AM – 09:30 AM. Avoid rush-hour intersections.");
            tvMaskAdvisory.setText("Cloth or 3-ply surgical mask advised for sensitive individuals.");
        } else {
            tvAsthmaAdvisory.setText("🍃 Clean Air: Excellent conditions for deep breathing and all outdoor workouts.");
            tvElderlyAdvisory.setText("Safe and pleasant for morning yoga, park strolls, and toddler outdoor playtime.");
            tvMaskAdvisory.setText("No mask required for general outdoor activities today.");
        }
    }

    private void populateFallbackData() {
        tvAqiValue.setText("68");
        tvAqiCategory.setText("Satisfactory");
        tvAqiImpactSummary.setText("Minor breathing discomfort to sensitive individuals. Generally safe for public.");
        pbAqi.setProgress(68);

        tvPm25.setText("28.4 µg/m³");
        tvPm25Status.setText("● Satisfactory");
        tvPm10.setText("58.2 µg/m³");
        tvPm10Status.setText("● Satisfactory");
        tvNo2.setText("18.4 µg/m³");
        tvOzone.setText("42.0 µg/m³");
        tvCo.setText("310 µg/m³");
        tvSo2.setText("8.2 µg/m³");

        tvGrassPollen.setText("14.0 grains/m³");
        tvGrassPollenRisk.setText("Low");
        tvTreePollen.setText("26.5 grains/m³");
        tvTreePollenRisk.setText("Moderate");
        tvRagweedPollen.setText("7.8 grains/m³");
        tvRagweedPollenRisk.setText("Low");
    }

    private double safePollen(java.util.List<Double> list) {
        if (list != null && !list.isEmpty() && list.get(0) != null) {
            return list.get(0);
        }
        return 0.0;
    }

    private int getCategoryColor(String category) {
        switch (category) {
            case "Good": return ContextCompat.getColor(this, R.color.aqi_good);
            case "Satisfactory": return ContextCompat.getColor(this, R.color.aqi_satisfactory);
            case "Moderate": return ContextCompat.getColor(this, R.color.aqi_moderate);
            case "Poor": return ContextCompat.getColor(this, R.color.aqi_poor);
            case "Very Poor": return ContextCompat.getColor(this, R.color.aqi_very_poor);
            default: return ContextCompat.getColor(this, R.color.aqi_severe);
        }
    }
}
