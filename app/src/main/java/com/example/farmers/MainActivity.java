package com.example.farmers;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.farmers.databinding.ActivityMainBinding;
import com.example.farmers.util.PrefsManager;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import androidx.appcompat.app.AlertDialog;
import com.example.farmers.util.LocaleHelper;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup top level destinations so back arrow appears on sub-screens
        Set<Integer> topLevelDestinations = new HashSet<>();
        topLevelDestinations.add(R.id.navigation_home);
        topLevelDestinations.add(R.id.navigation_profile);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(topLevelDestinations).build();

        // Setup NavController with NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
        }

        updateLocationSubtitle();

        // Language Switcher button
        binding.btnLanguage.setOnClickListener(v -> showLanguageDialog());

        // Top Toolbar action buttons
        binding.btnSunlight.setOnClickListener(v -> {
            if (navController != null) navController.navigate(R.id.navigation_sunlight);
        });

        binding.btnAlerts.setOnClickListener(v -> {
            if (navController != null) navController.navigate(R.id.navigation_alerts);
        });

        binding.btnFarm.setOnClickListener(v -> {
            if (navController != null) navController.navigate(R.id.navigation_farm);
        });

        binding.btnSettings.setOnClickListener(v -> {
            if (navController != null) navController.navigate(R.id.navigation_settings);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLocationSubtitle();
    }

    public void updateLocationSubtitle() {
        if (binding != null) {
            String loc = PrefsManager.getInstance(this).getLocationName();
            binding.toolbar.setSubtitle("📍 " + loc);
        }
    }

    private void showLanguageDialog() {
        String[] languages = new String[]{
                getString(R.string.lang_english),
                getString(R.string.lang_hindi),
                getString(R.string.lang_marathi)
        };
        final String[] langCodes = new String[]{
                LocaleHelper.LANG_ENGLISH,
                LocaleHelper.LANG_HINDI,
                LocaleHelper.LANG_MARATHI
        };

        String currentLang = PrefsManager.getInstance(this).getLanguage();
        int selectedIndex = 0;
        for (int i = 0; i < langCodes.length; i++) {
            if (langCodes[i].equalsIgnoreCase(currentLang)) {
                selectedIndex = i;
                break;
            }
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.select_language)
                .setSingleChoiceItems(languages, selectedIndex, (dialog, which) -> {
                    String chosen = langCodes[which];
                    if (!chosen.equalsIgnoreCase(currentLang)) {
                        LocaleHelper.setLocale(MainActivity.this, chosen);
                        android.widget.Toast.makeText(MainActivity.this, R.string.language_updated, android.widget.Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        recreate();
                    } else {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}