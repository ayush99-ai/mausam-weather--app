package com.example.farmers.util;

public class WindHelper {

    public static String getDirectionText(int degrees) {
        String[] directions = {"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE",
                "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"};
        int index = (int) Math.round(((degrees % 360) / 22.5));
        return directions[index % 16];
    }

    public static class WindCondition {
        public final String beaufort;
        public final String farmingImpact;
        public final boolean isAlert;

        public WindCondition(String beaufort, String farmingImpact, boolean isAlert) {
            this.beaufort = beaufort;
            this.farmingImpact = farmingImpact;
            this.isAlert = isAlert;
        }
    }

    public static WindCondition evaluateWind(double kmh) {
        if (kmh < 5) {
            return new WindCondition("Calm", "Ideal for pesticide/herbicide spraying. Zero drift risk.", false);
        } else if (kmh < 19) {
            return new WindCondition("Light Breeze", "Good conditions for general farming and drone operations.", false);
        } else if (kmh < 30) {
            return new WindCondition("Moderate Breeze", "Avoid delicate pesticide spraying due to drift hazard.", false);
        } else if (kmh < 40) {
            return new WindCondition("Fresh Breeze", "Do not spray chemicals. Secure loose greenhouse poly sheets.", true);
        } else if (kmh < 55) {
            return new WindCondition("Strong Wind Alert", "Risk of crop lodging (corn, paddy, banana). Halt aerial spraying.", true);
        } else {
            return new WindCondition("Gale / Storm Warning", "Severe threat of tree limb breakage, lodging, and structural damage.", true);
        }
    }
}
