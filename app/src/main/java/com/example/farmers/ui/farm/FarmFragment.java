package com.example.farmers.ui.farm;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.farmers.data.api.RetrofitClient;
import com.example.farmers.data.model.WeatherResponse;
import com.example.farmers.databinding.FragmentFarmBinding;
import com.example.farmers.util.PrefsManager;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FarmFragment extends Fragment {

    private FragmentFarmBinding binding;
    private PrefsManager prefs;
    private String farmReportText = "Farm Analysis Report";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFarmBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = PrefsManager.getInstance(requireContext());

        setupCharts();
        fetchAnalytics();

        binding.btnShareReport.setOnClickListener(v -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, farmReportText);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Share Farm Climate Summary"));
        });
    }

    private void setupCharts() {
        // Temp Chart
        binding.chartTempAnalytics.getDescription().setEnabled(false);
        binding.chartTempAnalytics.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        binding.chartTempAnalytics.getXAxis().setDrawGridLines(false);
        binding.chartTempAnalytics.getAxisRight().setEnabled(false);

        // Rain Chart
        binding.chartRainAnalytics.getDescription().setEnabled(false);
        binding.chartRainAnalytics.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        binding.chartRainAnalytics.getXAxis().setDrawGridLines(false);
        binding.chartRainAnalytics.getAxisRight().setEnabled(false);
    }

    private void fetchAnalytics() {
        RetrofitClient.getWeatherService().getWeather(
                prefs.getLatitude(),
                prefs.getLongitude(),
                null,
                null,
                "temperature_2m_max,temperature_2m_min,precipitation_sum,et0_fao_evapotranspiration",
                "auto",
                7
        ).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    populateAnalytics(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {}
        });
    }

    private void populateAnalytics(WeatherResponse weather) {
        if (weather.daily == null) return;

        double sumGdd = 0;
        double totalRain = 0;
        double sumTemp = 0;
        double et0 = 4.2;

        List<Entry> maxTempEntries = new ArrayList<>();
        List<Entry> minTempEntries = new ArrayList<>();
        List<BarEntry> rainEntries = new ArrayList<>();

        int days = weather.daily.time != null ? weather.daily.time.size() : 0;
        for (int i = 0; i < days; i++) {
            double maxT = weather.daily.temperatureMax != null && weather.daily.temperatureMax.size() > i && weather.daily.temperatureMax.get(i) != null ? weather.daily.temperatureMax.get(i) : 28;
            double minT = weather.daily.temperatureMin != null && weather.daily.temperatureMin.size() > i && weather.daily.temperatureMin.get(i) != null ? weather.daily.temperatureMin.get(i) : 18;
            double rain = weather.daily.precipitationSum != null && weather.daily.precipitationSum.size() > i && weather.daily.precipitationSum.get(i) != null ? weather.daily.precipitationSum.get(i) : 0;

            // GDD formula: max(0, (maxT + minT)/2 - 10)
            double meanT = (maxT + minT) / 2.0;
            double gdd = Math.max(0, meanT - 10.0);
            sumGdd += gdd;
            totalRain += rain;
            sumTemp += meanT;

            maxTempEntries.add(new Entry(i + 1, (float) maxT));
            minTempEntries.add(new Entry(i + 1, (float) minT));
            rainEntries.add(new BarEntry(i + 1, (float) rain));
        }

        if (weather.daily.evapotranspiration != null && !weather.daily.evapotranspiration.isEmpty()) {
            Double val = weather.daily.evapotranspiration.get(0);
            if (val != null) et0 = val;
        }

        binding.tvGDD.setText(String.format("%.1f °C-d", sumGdd));
        binding.tvGddCropMaturity.setText(String.format("Wheat Stage: ~%.0f%% GDD", Math.min(100.0, (sumGdd / 120.0) * 100)));
        binding.tvET0.setText(String.format("%.1f mm/day", et0));
        binding.tvMonthlyRain.setText(String.format("%.1f mm", totalRain));
        if (days > 0) {
            binding.tvMeanTemp.setText(String.format("%.1f °C", sumTemp / days));
        }

        // Set temp chart data
        LineDataSet maxSet = new LineDataSet(maxTempEntries, "Max Temp (°C)");
        maxSet.setColor(Color.parseColor("#F57C00"));
        maxSet.setLineWidth(2.5f);
        maxSet.setCircleRadius(4f);
        maxSet.setCircleColor(Color.parseColor("#F57C00"));

        LineDataSet minSet = new LineDataSet(minTempEntries, "Min Temp (°C)");
        minSet.setColor(Color.parseColor("#1976D2"));
        minSet.setLineWidth(2f);
        minSet.setCircleRadius(3.5f);
        minSet.setCircleColor(Color.parseColor("#1976D2"));

        binding.chartTempAnalytics.setData(new LineData(maxSet, minSet));
        binding.chartTempAnalytics.invalidate();

        // Set rain chart data
        BarDataSet rainSet = new BarDataSet(rainEntries, "Precipitation (mm)");
        rainSet.setColor(Color.parseColor("#0288D1"));
        binding.chartRainAnalytics.setData(new BarData(rainSet));
        binding.chartRainAnalytics.invalidate();

        // Prepare report text for sharing
        farmReportText = String.format(
                "🌾 Farm Climate & Crop Report - %s\n" +
                "📍 Location: %s\n" +
                "🌡️ 7-Day Cumulative GDD: %.1f °C-d\n" +
                "💧 Evapotranspiration (ET₀): %.1f mm/day\n" +
                "🌧️ Total Precipitation: %.1f mm\n" +
                "☀️ Mean Temp: %.1f °C\n\n" +
                "Generated by FarmWeather Pro",
                prefs.getFarmName(), prefs.getLocationName(), sumGdd, et0, totalRain, days > 0 ? (sumTemp / days) : 25.0
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
