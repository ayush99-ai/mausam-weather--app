package com.example.farmers.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsManager {

    private static final String PREF_NAME = "farmweather_prefs";
    private static final String KEY_LAT = "latitude";
    private static final String KEY_LON = "longitude";
    private static final String KEY_LOCATION_NAME = "location_name";
    private static final String KEY_USE_CELSIUS = "use_celsius";
    private static final String KEY_USE_KMH = "use_kmh";
    private static final String KEY_USE_MM = "use_mm";
    private static final String KEY_GEMINI_API_KEY = "gemini_api_key";
    private static final String KEY_FARM_NAME = "farm_name";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_RAIN_THRESHOLD = "rain_threshold";
    private static final String KEY_WIND_THRESHOLD = "wind_threshold";
    private static final String KEY_ALERTS_ENABLED = "alerts_enabled";
    private static final String KEY_GARDEN_MODE = "garden_mode";
    private static final String KEY_ONBOARDED = "onboarded";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_EMAIL = "user_email";
    private static final String KEY_PHONE = "user_phone";
    private static final String KEY_LOGIN_METHOD = "login_method";
    private static final String KEY_LANGUAGE = "app_language";
    private static final String KEY_EVENT_TYPE = "event_type";

    private static PrefsManager instance;
    private final SharedPreferences prefs;

    private PrefsManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static PrefsManager getInstance(Context context) {
        if (instance == null) instance = new PrefsManager(context);
        return instance;
    }

    // Location
    public void setLocation(double lat, double lon, String name) {
        prefs.edit().putFloat(KEY_LAT, (float) lat)
                .putFloat(KEY_LON, (float) lon)
                .putString(KEY_LOCATION_NAME, name)
                .apply();
    }
    public double getLatitude() { return prefs.getFloat(KEY_LAT, 20.0385f); }
    public double getLongitude() { return prefs.getFloat(KEY_LON, 73.8245f); }
    public String getLocationName() { return prefs.getString(KEY_LOCATION_NAME, "Aadgaon, Nashik"); }
    public boolean hasLocation() { return prefs.contains(KEY_LAT); }

    // Units
    public boolean useCelsius() { return prefs.getBoolean(KEY_USE_CELSIUS, true); }
    public void setUseCelsius(boolean v) { prefs.edit().putBoolean(KEY_USE_CELSIUS, v).apply(); }
    public boolean useKmh() { return prefs.getBoolean(KEY_USE_KMH, true); }
    public void setUseKmh(boolean v) { prefs.edit().putBoolean(KEY_USE_KMH, v).apply(); }
    public boolean useMm() { return prefs.getBoolean(KEY_USE_MM, true); }
    public void setUseMm(boolean v) { prefs.edit().putBoolean(KEY_USE_MM, v).apply(); }

    // AI
    public String getGeminiApiKey() { return prefs.getString(KEY_GEMINI_API_KEY, ""); }
    public void setGeminiApiKey(String key) { prefs.edit().putString(KEY_GEMINI_API_KEY, key).apply(); }

    // Profile
    public String getFarmName() { return prefs.getString(KEY_FARM_NAME, "My Farm"); }
    public void setFarmName(String v) { prefs.edit().putString(KEY_FARM_NAME, v).apply(); }
    public String getUserName() { return prefs.getString(KEY_USER_NAME, "Farmer"); }
    public void setUserName(String v) { prefs.edit().putString(KEY_USER_NAME, v).apply(); }

    // Alert thresholds
    public float getRainThreshold() { return prefs.getFloat(KEY_RAIN_THRESHOLD, 10f); }
    public void setRainThreshold(float v) { prefs.edit().putFloat(KEY_RAIN_THRESHOLD, v).apply(); }
    public float getWindThreshold() { return prefs.getFloat(KEY_WIND_THRESHOLD, 40f); }
    public void setWindThreshold(float v) { prefs.edit().putFloat(KEY_WIND_THRESHOLD, v).apply(); }
    public boolean alertsEnabled() { return prefs.getBoolean(KEY_ALERTS_ENABLED, true); }
    public void setAlertsEnabled(boolean v) { prefs.edit().putBoolean(KEY_ALERTS_ENABLED, v).apply(); }

    // Mode
    public boolean isGardenMode() { return prefs.getBoolean(KEY_GARDEN_MODE, false); }
    public void setGardenMode(boolean v) { prefs.edit().putBoolean(KEY_GARDEN_MODE, v).apply(); }

    // Onboarding
    public boolean isOnboarded() { return prefs.getBoolean(KEY_ONBOARDED, false); }
    public void setOnboarded(boolean v) { prefs.edit().putBoolean(KEY_ONBOARDED, v).apply(); }

    // Login
    public boolean isLoggedIn() {
        boolean loggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false);
        boolean hasEmailOrPhone = (getEmail() != null && !getEmail().isEmpty())
                || (getPhone() != null && !getPhone().isEmpty());
        return loggedIn && hasEmailOrPhone;
    }
    public void setLoggedIn(boolean v) { prefs.edit().putBoolean(KEY_IS_LOGGED_IN, v).apply(); }

    public void logout() {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, false).apply();
    }

    public String getEmail() { return prefs.getString(KEY_EMAIL, ""); }
    public void setEmail(String v) { prefs.edit().putString(KEY_EMAIL, v).apply(); }

    public String getPhone() { return prefs.getString(KEY_PHONE, ""); }
    public void setPhone(String v) { prefs.edit().putString(KEY_PHONE, v).apply(); }

    public String getLoginMethod() { return prefs.getString(KEY_LOGIN_METHOD, "email"); }
    public void setLoginMethod(String v) { prefs.edit().putString(KEY_LOGIN_METHOD, v).apply(); }

    // Language
    public String getLanguage() { return prefs.getString(KEY_LANGUAGE, "en"); }
    public void setLanguage(String lang) { prefs.edit().putString(KEY_LANGUAGE, lang).apply(); }

    // Event Type
    public String getEventType() { return prefs.getString(KEY_EVENT_TYPE, "Wedding"); }
    public void setEventType(String v) { prefs.edit().putString(KEY_EVENT_TYPE, v).apply(); }
}
