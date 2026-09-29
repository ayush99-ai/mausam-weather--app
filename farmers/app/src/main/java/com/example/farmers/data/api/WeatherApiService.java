package com.example.farmers.data.api;

import com.example.farmers.data.model.GeocodingResponse;
import com.example.farmers.data.model.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApiService {

    // Current + hourly + daily weather
    @GET("v1/forecast")
    Call<WeatherResponse> getWeather(
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("current") String current,
            @Query("hourly") String hourly,
            @Query("daily") String daily,
            @Query("timezone") String timezone,
            @Query("forecast_days") int forecastDays
    );

    // Soil moisture data
    @GET("v1/forecast")
    Call<WeatherResponse> getSoilMoisture(
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("hourly") String hourly,
            @Query("timezone") String timezone,
            @Query("forecast_days") int forecastDays
    );

    // Historical data for farm analysis
    @GET("v1/archive")
    Call<WeatherResponse> getHistoricalWeather(
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("start_date") String startDate,
            @Query("end_date") String endDate,
            @Query("daily") String daily,
            @Query("timezone") String timezone
    );

    // Geocoding search (on geocoding-api.open-meteo.com)
    @GET("v1/search")
    Call<GeocodingResponse> searchCity(
            @Query("name") String name,
            @Query("count") int count,
            @Query("language") String language,
            @Query("format") String format
    );
}
