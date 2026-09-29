package com.example.farmers.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "crops")
public class Crop {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String variety;
    public String sowDate;       // ISO date yyyy-MM-dd
    public String harvestDate;   // ISO date yyyy-MM-dd
    public float areAcres;
    public String notes;
    public String status;        // GROWING, HARVESTED, FAILED
    public long createdAt;

    public Crop() {
        this.createdAt = System.currentTimeMillis();
        this.status = "GROWING";
    }

    public int getDaysToHarvest() {
        if (harvestDate == null || harvestDate.isEmpty()) return -1;
        try {
            java.time.LocalDate harvest = java.time.LocalDate.parse(harvestDate);
            java.time.LocalDate today = java.time.LocalDate.now();
            long days = java.time.temporal.ChronoUnit.DAYS.between(today, harvest);
            return (int) days;
        } catch (Exception e) {
            return -1;
        }
    }

    public String getDisplayName() {
        if (variety != null && !variety.isEmpty()) {
            return name + " (" + variety + ")";
        }
        return name;
    }
}
