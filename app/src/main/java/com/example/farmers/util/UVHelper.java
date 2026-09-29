package com.example.farmers.util;

import android.graphics.Color;

public class UVHelper {

    public static class UVCategory {
        public final String level;
        public final int color;
        public final String advice;

        public UVCategory(String level, int color, String advice) {
            this.level = level;
            this.color = color;
            this.advice = advice;
        }
    }

    public static UVCategory getCategory(double uvIndex) {
        if (uvIndex < 3.0) {
            return new UVCategory("Low", Color.parseColor("#4CAF50"),
                    "Safe for outdoor field work. Wear light protection during noon.");
        } else if (uvIndex < 6.0) {
            return new UVCategory("Moderate", Color.parseColor("#FFC107"),
                    "Seek shade during midday. Wear a broad-brimmed hat, sunglasses & sunscreen.");
        } else if (uvIndex < 8.0) {
            return new UVCategory("High", Color.parseColor("#FF9800"),
                    "Protection needed. Reduce sun exposure between 10 AM and 4 PM. Irrigate plants early morning or evening.");
        } else if (uvIndex < 11.0) {
            return new UVCategory("Very High", Color.parseColor("#F44336"),
                    "Extra protection required. Avoid prolonged field exposure. Delicate seedlings may need shade cloth.");
        } else {
            return new UVCategory("Extreme", Color.parseColor("#9C27B0"),
                    "Dangerous UV levels. Stay indoors or in shade during peak hours. Shield sensitive crops and greenhouse plants.");
        }
    }
}
