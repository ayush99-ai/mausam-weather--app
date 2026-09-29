package com.example.farmers.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.farmers.LoginActivity;
import com.example.farmers.R;
import com.example.farmers.data.api.RetrofitClient;
import com.example.farmers.data.model.GeocodingResponse;
import com.example.farmers.databinding.FragmentSettingsBinding;
import com.example.farmers.util.LocationHelper;
import com.example.farmers.util.LocaleHelper;
import com.example.farmers.util.PrefsManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private PrefsManager prefs;
    private LocationHelper locationHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = PrefsManager.getInstance(requireContext());
        locationHelper = new LocationHelper(requireContext());

        loadCurrentSettings();
        setupLanguageRadioGroup();

        binding.btnSearchCity.setOnClickListener(v -> searchCity());
        binding.btnGpsDetect.setOnClickListener(v -> detectGps());
        binding.btnSaveSettings.setOnClickListener(v -> saveSettings());
        binding.btnLogout.setOnClickListener(v -> logoutUser());
    }

    private void setupLanguageRadioGroup() {
        String currentLang = prefs.getLanguage();
        if (LocaleHelper.LANG_HINDI.equalsIgnoreCase(currentLang)) {
            binding.rbLangHi.setChecked(true);
        } else if (LocaleHelper.LANG_MARATHI.equalsIgnoreCase(currentLang)) {
            binding.rbLangMr.setChecked(true);
        } else {
            binding.rbLangEn.setChecked(true);
        }

        binding.rgLanguageSettings.setOnCheckedChangeListener((group, checkedId) -> {
            String selectedLang = LocaleHelper.LANG_ENGLISH;
            if (checkedId == R.id.rbLangHi) {
                selectedLang = LocaleHelper.LANG_HINDI;
            } else if (checkedId == R.id.rbLangMr) {
                selectedLang = LocaleHelper.LANG_MARATHI;
            }

            if (!selectedLang.equalsIgnoreCase(prefs.getLanguage())) {
                LocaleHelper.setLocale(requireContext(), selectedLang);
                Toast.makeText(requireContext(), R.string.language_updated, Toast.LENGTH_SHORT).show();
                requireActivity().recreate();
            }
        });
    }

    private void loadCurrentSettings() {
        binding.etFarmName.setText(prefs.getFarmName());
        binding.etFarmerName.setText(prefs.getUserName());
        binding.etGeminiKey.setText(prefs.getGeminiApiKey());

        String accountInfo = !prefs.getEmail().isEmpty() ? prefs.getEmail() :
                (!prefs.getPhone().isEmpty() ? prefs.getPhone() : "Not logged in");
        binding.tvLoggedInAccount.setText(getString(R.string.logged_in_as, accountInfo));

        updateLocationLabel();
    }

    private void updateLocationLabel() {
        binding.tvCurrentSettingLocation.setText(String.format("Active Location: %s (Lat: %.2f, Lon: %.2f)",
                prefs.getLocationName(), prefs.getLatitude(), prefs.getLongitude()));
    }

    private void searchCity() {
        String query = binding.etCitySearch.getText() != null ? binding.etCitySearch.getText().toString().trim() : "";
        if (query.isEmpty()) {
            binding.etCitySearch.setError("Enter a city name");
            return;
        }

        binding.btnSearchCity.setEnabled(false);
        RetrofitClient.getGeocodingService().searchCity(query, 1, "en", "json")
                .enqueue(new Callback<GeocodingResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GeocodingResponse> call, @NonNull Response<GeocodingResponse> response) {
                        if (!isAdded()) return;
                        binding.btnSearchCity.setEnabled(true);

                        if (response.isSuccessful() && response.body() != null &&
                                response.body().results != null && !response.body().results.isEmpty()) {
                            GeocodingResponse.GeocodingResult result = response.body().results.get(0);
                            String fullName = result.name + (result.country != null ? ", " + result.country : "");
                            prefs.setLocation(result.latitude, result.longitude, fullName);
                            updateLocationLabel();
                            Toast.makeText(requireContext(), "Location set to: " + fullName, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "City not found. Try another spelling.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GeocodingResponse> call, @NonNull Throwable t) {
                        if (!isAdded()) return;
                        binding.btnSearchCity.setEnabled(true);
                        Toast.makeText(requireContext(), "Search error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void detectGps() {
        Toast.makeText(requireContext(), "Locating device via GPS...", Toast.LENGTH_SHORT).show();
        locationHelper.getCurrentLocation(new LocationHelper.LocationResultListener() {
            @Override
            public void onLocationFound(double lat, double lon, String locationName) {
                if (!isAdded()) return;
                prefs.setLocation(lat, lon, locationName);
                updateLocationLabel();
                Toast.makeText(requireContext(), "GPS location updated!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveSettings() {
        String farmName = binding.etFarmName.getText() != null ? binding.etFarmName.getText().toString().trim() : "My Farm";
        String farmerName = binding.etFarmerName.getText() != null ? binding.etFarmerName.getText().toString().trim() : "Farmer";
        String apiKey = binding.etGeminiKey.getText() != null ? binding.etGeminiKey.getText().toString().trim() : "";

        prefs.setFarmName(farmName);
        prefs.setUserName(farmerName);
        prefs.setGeminiApiKey(apiKey);

        Toast.makeText(requireContext(), "Settings saved successfully!", Toast.LENGTH_SHORT).show();
    }

    private void logoutUser() {
        prefs.logout();
        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
