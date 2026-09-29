package com.example.farmers.ui.garden;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.farmers.databinding.FragmentGardenBinding;
import com.example.farmers.ui.adapter.GardenTaskAdapter;

import java.util.ArrayList;
import java.util.List;

public class GardenFragment extends Fragment {

    private FragmentGardenBinding binding;
    private GardenTaskAdapter taskAdapter;
    private final List<GardenTaskAdapter.GardenTask> tasks = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGardenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        taskAdapter = new GardenTaskAdapter();
        binding.rvGardenTasks.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvGardenTasks.setAdapter(taskAdapter);

        loadGardenTasks();

        binding.btnAddGardenTask.setOnClickListener(v -> showAddTaskDialog());
    }

    private void loadGardenTasks() {
        tasks.clear();
        tasks.add(new GardenTaskAdapter.GardenTask(
                "Water potted seedlings & balcony planters",
                "Early morning watering prevents leaf sunburn",
                "Watering",
                false
        ));
        tasks.add(new GardenTaskAdapter.GardenTask(
                "Inspect tomato & pepper foliage for aphids",
                "Check undersides of leaves and soft new shoots",
                "Pest Check",
                false
        ));
        tasks.add(new GardenTaskAdapter.GardenTask(
                "Apply vermicompost or seaweed fertilizer",
                "Boost vegetative growth during warm daylight window",
                "Nutrition",
                false
        ));
        tasks.add(new GardenTaskAdapter.GardenTask(
                "Prune yellowing leaves & deadhead spent blooms",
                "Stimulates fresh side branching in herbs and roses",
                "Pruning",
                true
        ));
        tasks.add(new GardenTaskAdapter.GardenTask(
                "Mulch garden soil with dry leaves or straw",
                "Conserves root moisture & suppresses weed growth",
                "Soil Care",
                false
        ));

        taskAdapter.setTasks(tasks);
    }

    private void showAddTaskDialog() {
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 10);

        final EditText etTitle = new EditText(requireContext());
        etTitle.setHint("Task Name (e.g. Water Orchids)");
        layout.addView(etTitle);

        final EditText etSub = new EditText(requireContext());
        etSub.setHint("Details/Notes (optional)");
        layout.addView(etSub);

        new AlertDialog.Builder(requireContext())
                .setTitle("Add Garden Care Task")
                .setView(layout)
                .setPositiveButton("Add", (dialog, which) -> {
                    String title = etTitle.getText().toString().trim();
                    String sub = etSub.getText().toString().trim();
                    if (!title.isEmpty()) {
                        GardenTaskAdapter.GardenTask newTask = new GardenTaskAdapter.GardenTask(
                                title,
                                sub.isEmpty() ? "Custom garden care item" : sub,
                                "Custom",
                                false
                        );
                        tasks.add(0, newTask);
                        taskAdapter.setTasks(tasks);
                        Toast.makeText(requireContext(), "Task added!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
