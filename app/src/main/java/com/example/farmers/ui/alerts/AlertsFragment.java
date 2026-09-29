package com.example.farmers.ui.alerts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.farmers.data.api.RetrofitClient;
import com.example.farmers.data.model.WeatherResponse;
import com.example.farmers.databinding.FragmentAlertsBinding;
import com.example.farmers.util.PrefsManager;
import com.example.farmers.util.WindHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlertsFragment extends Fragment {

    private FragmentAlertsBinding binding;
    private PrefsManager prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAlertsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = PrefsManager.getInstance(requireContext());

        setupControls();
        fetchCurrentConditions();
    }

    private void setupControls() {
        binding.switchAlerts.setChecked(prefs.alertsEnabled());
        binding.switchAlerts.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.setAlertsEnabled(isChecked);
        });

        // Rain threshold
        float rainThresh = prefs.getRainThreshold();
        binding.sliderRainThreshold.setValue(Math.min(50f, Math.max(2f, rainThresh)));
        binding.tvRainThresholdLabel.setText(String.format("Alert me when rainfall exceeds: %.0f mm/day", rainThresh));
        binding.sliderRainThreshold.addOnChangeListener((slider, value, fromUser) -> {
            prefs.setRainThreshold(value);
            binding.tvRainThresholdLabel.setText(String.format("Alert me when rainfall exceeds: %.0f mm/day", value));
        });

        // Wind threshold
        float windThresh = prefs.getWindThreshold();
        binding.sliderWindThreshold.setValue(Math.min(80f, Math.max(15f, windThresh)));
        binding.tvWindThresholdLabel.setText(String.format("Alert me when wind gusts exceed: %.0f km/h", windThresh));
        binding.sliderWindThreshold.addOnChangeListener((slider, value, fromUser) -> {
            prefs.setWindThreshold(value);
            binding.tvWindThresholdLabel.setText(String.format("Alert me when wind gusts exceed: %.0f km/h", value));
        });

        binding.btnTestAlert.setOnClickListener(v -> {
            android.widget.Toast.makeText(requireContext(),
                    "🚨 Test Alert: Wind gust of 42 km/h detected! Pause spraying and protect field covers.",
                    android.widget.Toast.LENGTH_LONG).show();
        });
    }

    private void fetchCurrentConditions() {
        RetrofitClient.getWeatherService().getWeather(
                prefs.getLatitude(),
                prefs.getLongitude(),
                "precipitation,wind_speed_10m,wind_direction_10m,wind_gusts_10m",
                null,
                "precipitation_probability_max,precipitation_sum",
                "auto",
                1
        ).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    populateConditions(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {}
        });
    }

    private void populateConditions(WeatherResponse weather) {
        if (weather.daily != null && weather.daily.precipitationProbabilityMax != null && !weather.daily.precipitationProbabilityMax.isEmpty()) {
            Integer val = weather.daily.precipitationProbabilityMax.get(0);
            if (val != null) {
                binding.tvAlertRainChance.setText(val + " %");
            }
        }

        if (weather.current != null) {
            String dir = WindHelper.getDirectionText(weather.current.windDirection10m);
            binding.tvAlertWindSpeed.setText(String.format("%.1f km/h (%s) • Gusts: %.1f km/h",
                    weather.current.windSpeed10m, dir, weather.current.windGusts10m));

            WindHelper.WindCondition cond = WindHelper.evaluateWind(weather.current.windSpeed10m);
            binding.tvSprayingSafety.setText("🌾 " + cond.beaufort + ": " + cond.farmingImpact);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
