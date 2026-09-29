package com.example.event_planner;

import java.util.ArrayList;
import java.util.List;

/**
 * WeatherData holds the current weather metrics, 7-day extended forecasts,
 * and hourly planning timelines.
 *
 * It isolates meteorological data from the UI layer to allow smooth replacement
 * with a live weather REST API (such as IMD or Open-Meteo).
 */
public class WeatherData {

    private int temperature;
    private int rainProbability;
    private int humidity;
    private int windSpeed;
    private String condition;
    private String date;
    private String time;
    private int uvIndex;
    private String sunrise;
    private String sunset;
    private String goldenHour;

    public WeatherData() {
        // Default mock values representing realistic outdoor event conditions
        this.temperature = 28;
        this.rainProbability = 20;
        this.humidity = 62;
        this.windSpeed = 12;
        this.condition = "Partly Cloudy";
        this.date = "Today";
        this.time = "18:00";
        this.uvIndex = 5;
        this.sunrise = "06:23 AM";
        this.sunset = "06:30 PM";
        this.goldenHour = "5:25 PM – 6:30 PM";
    }

    public WeatherData(int temperature, int rainProbability, int humidity, int windSpeed,
                       String condition, String date, String time) {
        this.temperature = temperature;
        this.rainProbability = rainProbability;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.condition = condition;
        this.date = date;
        this.time = time;
        this.uvIndex = 5;
        this.sunrise = "06:23 AM";
        this.sunset = "06:30 PM";
        this.goldenHour = "5:25 PM – 6:30 PM";
    }

    // =========================================================================
    // EXTENDED 7-DAY FORECAST DATA
    // =========================================================================

    /**
     * Generates an extended 7-day weather forecast tailored for event planning.
     *
     * // TODO: Replace mock weather data with API response.
     * // For live API integration:
     * // Make an HTTP GET request to a weather service (e.g. Open-Meteo or IMD)
     * // Parse the daily arrays (daily.temperature_2m_max, daily.precipitation_probability_max, etc.)
     * // and populate this list dynamically.
     */
    public static List<ForecastDay> getExtendedForecast() {
        List<ForecastDay> forecastList = new ArrayList<>();

        forecastList.add(new ForecastDay(
                "TODAY", "23 Sep", "Partly Cloudy", 28, 22, 20, 12, 88,
                "GOOD", "Optimal Event Day • Great conditions for wedding, party, or reception."
        ));

        forecastList.add(new ForecastDay(
                "TOMORROW", "24 Sep", "Sunny & Clear", 30, 23, 10, 10, 92,
                "EXCELLENT", "Prime Gathering Day • Clear sky and calm breeze throughout evening."
        ));

        forecastList.add(new ForecastDay(
                "FRIDAY", "25 Sep", "Cloudy", 27, 21, 45, 15, 72,
                "GOOD", "Favorable Conditions • Keep backup rain shade on standby for late evening."
        ));

        forecastList.add(new ForecastDay(
                "SATURDAY", "26 Sep", "Light Rain", 25, 20, 65, 18, 55,
                "MODERATE", "Caution Advised • High rain chance; arrange water-resistant tents."
        ));

        forecastList.add(new ForecastDay(
                "SUNDAY", "27 Sep", "Mostly Cloudy", 26, 21, 35, 13, 68,
                "GOOD", "Pleasant Weather • Suitable for daytime picnics and outdoor celebrations."
        ));

        forecastList.add(new ForecastDay(
                "MONDAY", "28 Sep", "Clear & Mild", 29, 22, 15, 11, 89,
                "EXCELLENT", "Ideal Weather • Low humidity, mild breeze, great for photography."
        ));

        forecastList.add(new ForecastDay(
                "TUESDAY", "29 Sep", "Scattered Clouds", 28, 21, 25, 14, 82,
                "GOOD", "Very Suitable • Moderate warmth, good outdoor seating comfort."
        ));

        return forecastList;
    }

    // =========================================================================
    // HOURLY TIMELINE SLOTS
    // =========================================================================

    /**
     * Generates hourly weather timeline slots to identify the best time window.
     *
     * // TODO: Replace mock weather data with API response.
     * // Connect to hourly weather endpoint (e.g. hourly.temperature_2m, hourly.precipitation_probability)
     */
    public static List<HourlySlot> getHourlySlots() {
        List<HourlySlot> slots = new ArrayList<>();

        slots.add(new HourlySlot("10:00 AM", 27, 15, "Pleasant Morning", 78, "GOOD", "Moderate sunlight; comfortable for guest arrivals.", false));
        slots.add(new HourlySlot("12:00 PM", 29, 20, "Warm Afternoon", 75, "GOOD", "Direct solar radiation; provide canopy or tent shade.", false));
        slots.add(new HourlySlot("03:00 PM", 31, 30, "Peak Heat", 58, "MODERATE", "Higher temperature; ensure guest hydration and cooling.", false));
        slots.add(new HourlySlot("06:00 PM", 27, 18, "Golden Hour", 94, "EXCELLENT", "Prime gathering window! Optimal temperature and gentle breeze.", true));
        slots.add(new HourlySlot("08:00 PM", 25, 15, "Mild Evening", 90, "EXCELLENT", "Very comfortable evening atmosphere for dinner and music.", false));

        return slots;
    }

    // Getters and Setters

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getRainProbability() {
        return rainProbability;
    }

    public void setRainProbability(int rainProbability) {
        this.rainProbability = rainProbability;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(int windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getUvIndex() {
        return uvIndex;
    }

    public String getSunrise() {
        return sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public String getGoldenHour() {
        return goldenHour;
    }
}
