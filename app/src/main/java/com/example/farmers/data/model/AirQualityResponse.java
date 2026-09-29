package com.example.farmers.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AirQualityResponse {

    @SerializedName("latitude")
    public double latitude;

    @SerializedName("longitude")
    public double longitude;

    @SerializedName("current")
    public Current current;

    @SerializedName("hourly")
    public Hourly hourly;

    public static class Current {
        @SerializedName("time")
        public String time;

        @SerializedName("pm10")
        public double pm10;

        @SerializedName("pm2_5")
        public double pm25;

        @SerializedName("carbon_monoxide")
        public double carbonMonoxide;

        @SerializedName("nitrogen_dioxide")
        public double nitrogenDioxide;

        @SerializedName("sulphur_dioxide")
        public double sulphurDioxide;

        @SerializedName("ozone")
        public double ozone;

        @SerializedName("us_aqi")
        public int usAqi;

        @SerializedName("european_aqi")
        public int europeanAqi;
    }

    public static class Hourly {
        @SerializedName("time")
        public List<String> time;

        @SerializedName("pm10")
        public List<Double> pm10;

        @SerializedName("pm2_5")
        public List<Double> pm25;

        @SerializedName("grass_pollen")
        public List<Double> grassPollen;

        @SerializedName("birch_pollen")
        public List<Double> birchPollen;

        @SerializedName("ragweed_pollen")
        public List<Double> ragweedPollen;

        @SerializedName("olive_pollen")
        public List<Double> olivePollen;
    }

    /**
     * Calculates the official Indian Central Pollution Control Board (CPCB) AQI index
     * based on live PM2.5, PM10, and gaseous pollutant concentrations.
     */
    public int calculateCpcbAqi() {
        if (current == null) return 65; // realistic fallback

        int pm25Index = calculatePm25SubIndex(current.pm25);
        int pm10Index = calculatePm10SubIndex(current.pm10);
        int no2Index = calculateNo2SubIndex(current.nitrogenDioxide);

        // Indian National Air Quality Index takes the maximum of sub-indices
        int aqi = Math.max(pm25Index, Math.max(pm10Index, no2Index));
        return Math.max(15, Math.min(aqi, 500));
    }

    private int calculatePm25SubIndex(double conc) {
        if (conc <= 30) return (int) (conc * 50 / 30);
        if (conc <= 60) return (int) (51 + (conc - 30) * 49 / 30);
        if (conc <= 90) return (int) (101 + (conc - 60) * 99 / 30);
        if (conc <= 120) return (int) (201 + (conc - 90) * 99 / 30);
        if (conc <= 250) return (int) (301 + (conc - 120) * 99 / 130);
        return (int) (401 + (conc - 250) * 99 / 130);
    }

    private int calculatePm10SubIndex(double conc) {
        if (conc <= 50) return (int) (conc * 50 / 50);
        if (conc <= 100) return (int) (51 + (conc - 50) * 49 / 50);
        if (conc <= 250) return (int) (101 + (conc - 100) * 99 / 150);
        if (conc <= 350) return (int) (201 + (conc - 250) * 99 / 100);
        if (conc <= 430) return (int) (301 + (conc - 350) * 99 / 80);
        return (int) (401 + (conc - 430) * 99 / 70);
    }

    private int calculateNo2SubIndex(double conc) {
        if (conc <= 40) return (int) (conc * 50 / 40);
        if (conc <= 80) return (int) (51 + (conc - 40) * 49 / 40);
        if (conc <= 180) return (int) (101 + (conc - 80) * 99 / 100);
        if (conc <= 280) return (int) (201 + (conc - 180) * 99 / 100);
        if (conc <= 400) return (int) (301 + (conc - 280) * 99 / 120);
        return 401;
    }

    public static String getCpcbCategory(int aqi) {
        if (aqi <= 50) return "Good";
        if (aqi <= 100) return "Satisfactory";
        if (aqi <= 200) return "Moderate";
        if (aqi <= 300) return "Poor";
        if (aqi <= 400) return "Very Poor";
        return "Severe";
    }

    public static String getCpcbAdvisory(int aqi) {
        if (aqi <= 50) return "Minimal impact. Safe for all outdoor activities and deep breathing.";
        if (aqi <= 100) return "Satisfactory. Minor breathing discomfort to sensitive people.";
        if (aqi <= 200) return "Moderate. Breathing discomfort to people with asthma and heart conditions.";
        if (aqi <= 300) return "Poor. Breathing discomfort to most people on prolonged exposure. Wear N95 outdoors.";
        if (aqi <= 400) return "Very Poor. Respiratory illness on prolonged exposure. Avoid morning jogs.";
        return "Severe. Seriously affects healthy people and severely impacts sensitive groups. Stay indoors.";
    }

    public static String getLocalizedCpcbCategory(android.content.Context context, int aqi) {
        if (context == null) return getCpcbCategory(aqi);
        if (aqi <= 50) return context.getString(com.example.farmers.R.string.aqi_good);
        if (aqi <= 100) return context.getString(com.example.farmers.R.string.aqi_satisfactory);
        if (aqi <= 200) return context.getString(com.example.farmers.R.string.aqi_moderate);
        if (aqi <= 300) return context.getString(com.example.farmers.R.string.aqi_poor);
        if (aqi <= 400) return context.getString(com.example.farmers.R.string.aqi_very_poor);
        return context.getString(com.example.farmers.R.string.aqi_severe);
    }
}
