package com.example.farmers.ui.crops;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.farmers.data.db.AppDatabase;
import com.example.farmers.data.model.Crop;
import com.example.farmers.databinding.FragmentCropsBinding;
import com.example.farmers.ui.adapter.CropAdapter;

import java.util.List;
import java.util.concurrent.Executors;

public class CropsFragment extends Fragment implements CropAdapter.OnCropActionListener {

    private FragmentCropsBinding binding;
    private CropAdapter adapter;
    private AppDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCropsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = AppDatabase.getInstance(requireContext());

        adapter = new CropAdapter(this);
        binding.rvCrops.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvCrops.setAdapter(adapter);

        binding.fabAddCrop.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddCropActivity.class);
            startActivity(intent);
        });

        observeCrops();
    }

    private void observeCrops() {
        db.cropDao().getAllCrops().observe(getViewLifecycleOwner(), this::updateUI);
    }

    private void updateUI(List<Crop> crops) {
        if (crops == null || crops.isEmpty()) {
            binding.layoutEmptyCrops.setVisibility(View.VISIBLE);
            binding.rvCrops.setVisibility(View.GONE);
            binding.tvActiveCropsCount.setText("0");
            binding.tvTotalAcres.setText("0.0");
            adapter.setCrops(crops);
        } else {
            binding.layoutEmptyCrops.setVisibility(View.GONE);
            binding.rvCrops.setVisibility(View.VISIBLE);

            int activeCount = 0;
            float totalAcres = 0f;
            for (Crop c : crops) {
                if ("GROWING".equalsIgnoreCase(c.status)) {
                    activeCount++;
                    totalAcres += c.areAcres;
                }
            }

            binding.tvActiveCropsCount.setText(String.valueOf(activeCount));
            binding.tvTotalAcres.setText(String.format("%.1f", totalAcres));
            adapter.setCrops(crops);
        }
    }

    @Override
    public void onDeleteCrop(Crop crop) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Crop")
                .setMessage("Are you sure you want to remove " + crop.getDisplayName() + " from your farm records?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        db.cropDao().delete(crop);
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() ->
                                    Toast.makeText(requireContext(), "Crop removed", Toast.LENGTH_SHORT).show()
                            );
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onToggleCropStatus(Crop crop) {
        String newStatus = "GROWING".equalsIgnoreCase(crop.status) ? "HARVESTED" : "GROWING";
        crop.status = newStatus;
        Executors.newSingleThreadExecutor().execute(() -> {
            db.cropDao().update(crop);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), crop.getDisplayName() + " status updated to " + newStatus, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
