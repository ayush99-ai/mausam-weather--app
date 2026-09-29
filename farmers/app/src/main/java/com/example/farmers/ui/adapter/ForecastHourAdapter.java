package com.example.farmers.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmers.R;

import java.util.ArrayList;
import java.util.List;

public class ForecastHourAdapter extends RecyclerView.Adapter<ForecastHourAdapter.HourViewHolder> {

    public static class HourItem {
        public final String time;
        public final double temp;
        public final int rainProb;
        public final int weatherCode;

        public HourItem(String time, double temp, int rainProb, int weatherCode) {
            this.time = time;
            this.temp = temp;
            this.rainProb = rainProb;
            this.weatherCode = weatherCode;
        }
    }

    private final List<HourItem> items = new ArrayList<>();

    public void setItems(List<HourItem> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_forecast_hour, parent, false);
        return new HourViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HourViewHolder holder, int position) {
        HourItem item = items.get(position);
        
        // Format ISO time 2026-09-22T14:00 -> 14:00
        String displayTime = item.time;
        if (item.time != null && item.time.contains("T")) {
            displayTime = item.time.substring(item.time.indexOf("T") + 1);
        }
        holder.tvHourTime.setText(displayTime);
        holder.tvHourTemp.setText(String.format("%.0f°", item.temp));
        holder.tvHourRain.setText(item.rainProb + "%");

        if (item.rainProb > 40) {
            holder.ivHourIcon.setImageResource(R.drawable.ic_rain);
        } else {
            holder.ivHourIcon.setImageResource(R.drawable.ic_weather);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HourViewHolder extends RecyclerView.ViewHolder {
        TextView tvHourTime, tvHourTemp, tvHourRain;
        ImageView ivHourIcon;

        public HourViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHourTime = itemView.findViewById(R.id.tvHourTime);
            tvHourTemp = itemView.findViewById(R.id.tvHourTemp);
            tvHourRain = itemView.findViewById(R.id.tvHourRain);
            ivHourIcon = itemView.findViewById(R.id.ivHourIcon);
        }
    }
}
