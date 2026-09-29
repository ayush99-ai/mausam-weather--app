package com.example.farmers;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.farmers.ui.agent.AgentFragment;
import com.example.farmers.ui.crops.AddCropActivity;
import com.example.farmers.ui.crops.CropsFragment;
import com.example.farmers.ui.farm.FarmFragment;
import com.example.farmers.ui.garden.GardenFragment;
import com.example.farmers.ui.soil.SoilFragment;
import com.example.farmers.util.PrefsManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class FarmerActivity extends AppCompatActivity {

    private final String[] tabTitles = new String[]{
            "Crops & Fields",
            "Soil & Irrigation",
            "Analytics & GDD",
            "Garden Mode",
            "Agri AI Advisor"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer);

        MaterialToolbar toolbar = findViewById(R.id.toolbarFarmer);
        TabLayout tabLayout = findViewById(R.id.tabLayoutFarmer);
        ViewPager2 viewPager = findViewById(R.id.viewPagerFarmer);
        ExtendedFloatingActionButton fabAddCrop = findViewById(R.id.fabAddCrop);

        toolbar.setNavigationOnClickListener(v -> finish());

        String location = PrefsManager.getInstance(this).getLocationName();
        toolbar.setSubtitle("📍 Location: " + location);

        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0:
                        return new CropsFragment();
                    case 1:
                        return new SoilFragment();
                    case 2:
                        return new FarmFragment();
                    case 3:
                        return new GardenFragment();
                    case 4:
                        return new AgentFragment();
                    default:
                        return new CropsFragment();
                }
            }

            @Override
            public int getItemCount() {
                return tabTitles.length;
            }
        });

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabTitles[position])).attach();

        fabAddCrop.setOnClickListener(v -> {
            Intent intent = new Intent(FarmerActivity.this, AddCropActivity.class);
            startActivity(intent);
        });
    }
}
