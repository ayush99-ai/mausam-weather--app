package com.example.farmers.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmers.R;
import com.example.farmers.data.model.Crop;

import java.util.ArrayList;
import java.util.List;

public class CropAdapter extends RecyclerView.Adapter<CropAdapter.CropViewHolder> {

    public interface OnCropActionListener {
        void onDeleteCrop(Crop crop);
        void onToggleCropStatus(Crop crop);
    }

    private final List<Crop> crops = new ArrayList<>();
    private final OnCropActionListener listener;

    public CropAdapter(OnCropActionListener listener) {
        this.listener = listener;
    }

    public void setCrops(List<Crop> newCrops) {
        crops.clear();
        if (newCrops != null) crops.addAll(newCrops);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CropViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_crop, parent, false);
        return new CropViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CropViewHolder holder, int position) {
        Crop crop = crops.get(position);
        holder.tvCropName.setText(crop.getDisplayName());
        holder.tvCropArea.setText(String.format("Area: %.1f Acres", crop.areAcres));
        holder.tvSowDate.setText(crop.sowDate != null ? crop.sowDate : "--");

        boolean isGrowing = "GROWING".equalsIgnoreCase(crop.status);
        holder.tvCropStatus.setText(isGrowing ? "🌱 GROWING" : "🌾 HARVESTED");
        holder.tvCropStatus.setTextColor(isGrowing ? Color.parseColor("#1B5E20") : Color.parseColor("#E65100"));

        holder.tvCropStatus.setOnClickListener(v -> {
            if (listener != null) listener.onToggleCropStatus(crop);
        });

        int days = crop.getDaysToHarvest();
        if (days >= 0 && isGrowing) {
            holder.tvHarvestCountdown.setText(days + " days");
        } else if (!isGrowing) {
            holder.tvHarvestCountdown.setText("Done");
        } else {
            holder.tvHarvestCountdown.setText(crop.harvestDate != null ? crop.harvestDate : "--");
        }

        if (crop.notes != null && !crop.notes.trim().isEmpty()) {
            holder.tvCropNotes.setVisibility(View.VISIBLE);
            holder.tvCropNotes.setText(crop.notes);
        } else {
            holder.tvCropNotes.setVisibility(View.GONE);
        }

        holder.btnDeleteCrop.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteCrop(crop);
        });
    }

    @Override
    public int getItemCount() {
        return crops.size();
    }

    static class CropViewHolder extends RecyclerView.ViewHolder {
        TextView tvCropName, tvCropArea, tvCropStatus, tvSowDate, tvHarvestCountdown, tvCropNotes;
        ImageButton btnDeleteCrop;

        public CropViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCropName = itemView.findViewById(R.id.tvCropName);
            tvCropArea = itemView.findViewById(R.id.tvCropArea);
            tvCropStatus = itemView.findViewById(R.id.tvCropStatus);
            tvSowDate = itemView.findViewById(R.id.tvSowDate);
            tvHarvestCountdown = itemView.findViewById(R.id.tvHarvestCountdown);
            tvCropNotes = itemView.findViewById(R.id.tvCropNotes);
            btnDeleteCrop = itemView.findViewById(R.id.btnDeleteCrop);
        }
    }
}
