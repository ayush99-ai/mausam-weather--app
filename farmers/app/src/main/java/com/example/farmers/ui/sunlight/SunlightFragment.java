package com.example.farmers.ui.sunlight;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.farmers.data.api.RetrofitClient;
import com.example.farmers.data.model.WeatherResponse;
import com.example.farmers.databinding.FragmentSunlightBinding;
import com.example.farmers.util.PrefsManager;
import com.example.farmers.util.UVHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SunlightFragment extends Fragment {

    private FragmentSunlightBinding binding;
    private PrefsManager prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSunlightBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = PrefsManager.getInstance(requireContext());
        fetchSunlightData();
    }

    private void fetchSunlightData() {
        RetrofitClient.getWeatherService().getWeather(
                prefs.getLatitude(),
                prefs.getLongitude(),
                "uv_index,is_day",
                null,
                "uv_index_max,sunrise,sunset",
                "auto",
                1
        ).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    populateData(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {}
        });
    }

    private void populateData(WeatherResponse weather) {
        double uv = 5.4;
        if (weather.current != null) {
            uv = weather.current.uvIndex;
        } else if (weather.daily != null && weather.daily.uvIndexMax != null && !weather.daily.uvIndexMax.isEmpty()) {
            Double val = weather.daily.uvIndexMax.get(0);
            if (val != null) uv = val;
        }

        binding.tvUVIndex.setText(String.format("%.1f", uv));

        UVHelper.UVCategory category = UVHelper.getCategory(uv);
        binding.tvUVCategory.setText(category.level);
        binding.tvUVCategory.setTextColor(category.color);
        binding.tvUVIndex.setTextColor(category.color);
        binding.tvUVAdvice.setText(category.advice);

        if (weather.daily != null) {
            if (weather.daily.sunrise != null && !weather.daily.sunrise.isEmpty()) {
                binding.tvSunrise.setText(formatTime(weather.daily.sunrise.get(0)));
            }
            if (weather.daily.sunset != null && !weather.daily.sunset.isEmpty()) {
                binding.tvSunset.setText(formatTime(weather.daily.sunset.get(0)));
            }
        }
    }

    private String formatTime(String isoTime) {
        if (isoTime != null && isoTime.contains("T")) {
            return isoTime.substring(isoTime.indexOf("T") + 1);
        }
        return isoTime != null ? isoTime : "--";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
