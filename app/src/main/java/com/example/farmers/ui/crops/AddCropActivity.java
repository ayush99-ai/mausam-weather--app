package com.example.farmers.ui.crops;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.farmers.data.db.AppDatabase;
import com.example.farmers.data.model.Crop;
import com.example.farmers.databinding.ActivityAddCropBinding;
import com.example.farmers.util.LocaleHelper;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.concurrent.Executors;

public class AddCropActivity extends AppCompatActivity {

    private ActivityAddCropBinding binding;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddCropBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbarAddCrop.setNavigationOnClickListener(v -> finish());

        setupDatePickers();

        binding.btnSaveCrop.setOnClickListener(v -> saveCrop());
    }

    private void setupDatePickers() {
        binding.etSowDate.setOnClickListener(v -> showDatePicker((year, month, day) -> {
            String date = String.format("%04d-%02d-%02d", year, month + 1, day);
            binding.etSowDate.setText(date);
        }));

        binding.etHarvestDate.setOnClickListener(v -> showDatePicker((year, month, day) -> {
            String date = String.format("%04d-%02d-%02d", year, month + 1, day);
            binding.etHarvestDate.setText(date);
        }));
    }

    private interface DateSelectCallback {
        void onDateSelected(int year, int month, int day);
    }

    private void showDatePicker(DateSelectCallback callback) {
        Calendar c = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> callback.onDateSelected(year, month, dayOfMonth),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void saveCrop() {
        String name = binding.etCropName.getText() != null ? binding.etCropName.getText().toString().trim() : "";
        String variety = binding.etCropVariety.getText() != null ? binding.etCropVariety.getText().toString().trim() : "";
        String sowDate = binding.etSowDate.getText() != null ? binding.etSowDate.getText().toString().trim() : "";
        String harvestDate = binding.etHarvestDate.getText() != null ? binding.etHarvestDate.getText().toString().trim() : "";
        String areaStr = binding.etCropArea.getText() != null ? binding.etCropArea.getText().toString().trim() : "1.0";
        String notes = binding.etCropNotes.getText() != null ? binding.etCropNotes.getText().toString().trim() : "";

        if (name.isEmpty()) {
            binding.etCropName.setError("Crop name is required");
            return;
        }

        float area = 1.0f;
        try {
            area = Float.parseFloat(areaStr);
        } catch (Exception ignored) {}

        Crop crop = new Crop();
        crop.name = name;
        crop.variety = variety;
        crop.sowDate = sowDate.isEmpty() ? LocalDate.now().toString() : sowDate;
        crop.harvestDate = harvestDate;
        crop.areAcres = area;
        crop.notes = notes;
        crop.status = "GROWING";

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase.getInstance(this).cropDao().insert(crop);
            runOnUiThread(() -> {
                Toast.makeText(this, "Crop saved successfully!", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}
