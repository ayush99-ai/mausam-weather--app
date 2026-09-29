package com.example.farmers.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmers.R;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ForecastDayAdapter extends RecyclerView.Adapter<ForecastDayAdapter.DayViewHolder> {

    public static class DayItem {
        public final String date;
        public final double maxTemp;
        public final double minTemp;
        public final int rainProb;
        public final int weatherCode;

        public DayItem(String date, double maxTemp, double minTemp, int rainProb, int weatherCode) {
            this.date = date;
            this.maxTemp = maxTemp;
            this.minTemp = minTemp;
            this.rainProb = rainProb;
            this.weatherCode = weatherCode;
        }
    }

    private final List<DayItem> items = new ArrayList<>();

    public void setItems(List<DayItem> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_forecast_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        DayItem item = items.get(position);

        String dayLabel = item.date;
        try {
            LocalDate localDate = LocalDate.parse(item.date);
            dayLabel = localDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()) + " " + localDate.getDayOfMonth();
        } catch (Exception ignored) {}

        holder.tvDayName.setText(dayLabel);
        holder.tvDayTempMax.setText(String.format("%.0f°", item.maxTemp));
        holder.tvDayTempMin.setText(String.format("%.0f°", item.minTemp));
        holder.tvDayRainProb.setText("💧 " + item.rainProb + "%");

        if (item.rainProb > 40) {
            holder.ivDayIcon.setImageResource(R.drawable.ic_rain);
        } else {
            holder.ivDayIcon.setImageResource(R.drawable.ic_weather);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayName, tvDayRainProb, tvDayTempMin, tvDayTempMax;
        ImageView ivDayIcon;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayName = itemView.findViewById(R.id.tvDayName);
            tvDayRainProb = itemView.findViewById(R.id.tvDayRainProb);
            tvDayTempMin = itemView.findViewById(R.id.tvDayTempMin);
            tvDayTempMax = itemView.findViewById(R.id.tvDayTempMax);
            ivDayIcon = itemView.findViewById(R.id.ivDayIcon);
        }
    }
}
