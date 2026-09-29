package com.example.farmers.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WeatherResponse {

    @SerializedName("latitude")
    public double latitude;

    @SerializedName("longitude")
    public double longitude;

    @SerializedName("timezone")
    public String timezone;

    @SerializedName("current")
    public Current current;

    @SerializedName("hourly")
    public Hourly hourly;

    @SerializedName("daily")
    public Daily daily;

    public static class Current {
        @SerializedName("time")
        public String time;
        @SerializedName("temperature_2m")
        public double temperature2m;
        @SerializedName("apparent_temperature")
        public double apparentTemperature;
        @SerializedName("relative_humidity_2m")
        public int relativeHumidity2m;
        @SerializedName("precipitation")
        public double precipitation;
        @SerializedName("weather_code")
        public int weatherCode;
        @SerializedName("surface_pressure")
        public double surfacePressure;
        @SerializedName("wind_speed_10m")
        public double windSpeed10m;
        @SerializedName("wind_direction_10m")
        public int windDirection10m;
        @SerializedName("wind_gusts_10m")
        public double windGusts10m;
        @SerializedName("uv_index")
        public double uvIndex;
        @SerializedName("is_day")
        public int isDay;
    }

    public static class Hourly {
        @SerializedName("time")
        public List<String> time;
        @SerializedName("temperature_2m")
        public List<Double> temperature2m;
        @SerializedName("precipitation_probability")
        public List<Integer> precipitationProbability;
        @SerializedName("precipitation")
        public List<Double> precipitation;
        @SerializedName("weather_code")
        public List<Integer> weatherCode;
        @SerializedName("wind_speed_10m")
        public List<Double> windSpeed10m;
        @SerializedName("wind_gusts_10m")
        public List<Double> windGusts10m;
        @SerializedName("uv_index")
        public List<Double> uvIndex;
        @SerializedName("soil_moisture_0_to_7cm")
        public List<Double> soilMoisture0to7;
        @SerializedName("soil_moisture_7_to_28cm")
        public List<Double> soilMoisture7to28;
        @SerializedName("soil_moisture_28_to_100cm")
        public List<Double> soilMoisture28to100;
        @SerializedName("sunshine_duration")
        public List<Double> sunshineDuration;
    }

    public static class Daily {
        @SerializedName("time")
        public List<String> time;
        @SerializedName("weather_code")
        public List<Integer> weatherCode;
        @SerializedName("temperature_2m_max")
        public List<Double> temperatureMax;
        @SerializedName("temperature_2m_min")
        public List<Double> temperatureMin;
        @SerializedName("precipitation_sum")
        public List<Double> precipitationSum;
        @SerializedName("precipitation_probability_max")
        public List<Integer> precipitationProbabilityMax;
        @SerializedName("wind_speed_10m_max")
        public List<Double> windSpeedMax;
        @SerializedName("wind_gusts_10m_max")
        public List<Double> windGustsMax;
        @SerializedName("uv_index_max")
        public List<Double> uvIndexMax;
        @SerializedName("sunrise")
        public List<String> sunrise;
        @SerializedName("sunset")
        public List<String> sunset;
        @SerializedName("et0_fao_evapotranspiration")
        public List<Double> evapotranspiration;
    }
}
