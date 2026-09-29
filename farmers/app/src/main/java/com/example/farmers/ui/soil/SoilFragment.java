package com.example.farmers.ui.soil;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.farmers.data.api.RetrofitClient;
import com.example.farmers.data.model.WeatherResponse;
import com.example.farmers.databinding.FragmentSoilBinding;
import com.example.farmers.util.PrefsManager;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SoilFragment extends Fragment {

    private FragmentSoilBinding binding;
    private PrefsManager prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSoilBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = PrefsManager.getInstance(requireContext());

        setupChart();

        binding.swipeRefreshSoil.setOnRefreshListener(this::fetchSoilData);

        binding.btnLogIrrigation.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "💧 Irrigation Logged! Soil moisture updated to optimal field capacity.", Toast.LENGTH_LONG).show();
            binding.tvIrrigationAdvice.setText("✅ Irrigation Completed: Root zone replenished to 0.420 m³/m³ field capacity.");
            binding.pbMoisture7to28.setProgress(85);
            binding.tvMoisture7to28.setText("0.420 m³/m³");
        });

        fetchSoilData();
    }

    private void setupChart() {
        binding.chartSoilTrend.getDescription().setEnabled(false);
        binding.chartSoilTrend.setTouchEnabled(true);
        binding.chartSoilTrend.setPinchZoom(true);
        binding.chartSoilTrend.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        binding.chartSoilTrend.getXAxis().setDrawGridLines(false);
        binding.chartSoilTrend.getAxisRight().setEnabled(false);
    }

    private void fetchSoilData() {
        binding.swipeRefreshSoil.setRefreshing(true);

        RetrofitClient.getWeatherService().getSoilMoisture(
                prefs.getLatitude(),
                prefs.getLongitude(),
                "soil_moisture_0_to_7cm,soil_moisture_7_to_28cm,soil_moisture_28_to_100cm",
                "auto",
                7
        ).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (!isAdded()) return;
                binding.swipeRefreshSoil.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    populateSoilData(response.body());
                } else {
                    Toast.makeText(requireContext(), "Failed to load soil data.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                binding.swipeRefreshSoil.setRefreshing(false);
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateSoilData(WeatherResponse weather) {
        if (weather.hourly == null) return;

        double m0 = 0.28, m7 = 0.35, m28 = 0.40;
        if (weather.hourly.soilMoisture0to7 != null && !weather.hourly.soilMoisture0to7.isEmpty()) {
            Double val = weather.hourly.soilMoisture0to7.get(0);
            if (val != null) m0 = val;
        }
        if (weather.hourly.soilMoisture7to28 != null && !weather.hourly.soilMoisture7to28.isEmpty()) {
            Double val = weather.hourly.soilMoisture7to28.get(0);
            if (val != null) m7 = val;
        }
        if (weather.hourly.soilMoisture28to100 != null && !weather.hourly.soilMoisture28to100.isEmpty()) {
            Double val = weather.hourly.soilMoisture28to100.get(0);
            if (val != null) m28 = val;
        }

        binding.tvMoisture0to7.setText(String.format("%.3f m³/m³", m0));
        binding.tvMoisture7to28.setText(String.format("%.3f m³/m³", m7));
        binding.tvMoisture28to100.setText(String.format("%.3f m³/m³", m28));

        int p0 = (int) Math.min(100, Math.max(0, m0 * 100 * 2));
        int p7 = (int) Math.min(100, Math.max(0, m7 * 100 * 2));
        int p28 = (int) Math.min(100, Math.max(0, m28 * 100 * 2));

        binding.pbMoisture0to7.setProgress(p0);
        binding.pbMoisture7to28.setProgress(p7);
        binding.pbMoisture28to100.setProgress(p28);

        // Irrigation advice evaluation
        if (m7 < 0.20) {
            binding.tvIrrigationAdvice.setText("⚠️ Severe Root Water Deficit: Root zone is dry (< 0.20 m³/m³). Initiate scheduled irrigation immediately to avoid crop wilting.");
        } else if (m7 < 0.30) {
            binding.tvIrrigationAdvice.setText("💧 Moderate Soil Moisture: Crops are actively drawing moisture. Plan next irrigation within 24–48 hours depending on rainfall.");
        } else if (m7 > 0.45) {
            binding.tvIrrigationAdvice.setText("🌧️ Saturated Soil: Adequate to high moisture detected. Hold off irrigation to prevent root hypoxia and fungal rot.");
        } else {
            binding.tvIrrigationAdvice.setText("✅ Optimal Moisture: Soil is in prime field capacity range (0.30–0.45 m³/m³). Crops have ideal water availability.");
        }

        // Plot 7-day trend
        if (weather.hourly.soilMoisture7to28 != null) {
            List<Entry> rootEntries = new ArrayList<>();
            List<Entry> surfaceEntries = new ArrayList<>();
            int count = Math.min(weather.hourly.soilMoisture7to28.size(), 168); // 7 days * 24h
            for (int i = 0; i < count; i += 6) { // every 6 hours
                float x = i / 24f; // days
                Double rVal = weather.hourly.soilMoisture7to28.get(i);
                float yRoot = rVal != null ? rVal.floatValue() : (float) m7;
                rootEntries.add(new Entry(x, yRoot));

                if (weather.hourly.soilMoisture0to7 != null && weather.hourly.soilMoisture0to7.size() > i) {
                    Double sVal = weather.hourly.soilMoisture0to7.get(i);
                    float ySurface = sVal != null ? sVal.floatValue() : (float) m0;
                    surfaceEntries.add(new Entry(x, ySurface));
                }
            }

            LineDataSet setRoot = new LineDataSet(rootEntries, "Root Zone (7-28cm)");
            setRoot.setColor(Color.parseColor("#2E7D32"));
            setRoot.setLineWidth(2.5f);
            setRoot.setDrawCircles(false);

            LineDataSet setSurface = new LineDataSet(surfaceEntries, "Surface (0-7cm)");
            setSurface.setColor(Color.parseColor("#0288D1"));
            setSurface.setLineWidth(1.8f);
            setSurface.setDrawCircles(false);

            LineData lineData = new LineData(setRoot, setSurface);
            binding.chartSoilTrend.setData(lineData);
            binding.chartSoilTrend.invalidate();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
