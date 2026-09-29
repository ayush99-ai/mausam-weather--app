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

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup top level destinations so back arrow appears on sub-screens
        Set<Integer> topLevelDestinations = new HashSet<>();
        topLevelDestinations.add(R.id.navigation_home);
        topLevelDestinations.add(R.id.navigation_agent);

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
}