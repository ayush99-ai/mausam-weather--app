package com.example.farmers.data.api;

import com.example.farmers.data.model.AirQualityResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AirQualityApiService {

    @GET("v1/air-quality")
    Call<AirQualityResponse> getAirQuality(
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("current") String currentFields,
            @Query("hourly") String hourlyFields,
            @Query("timezone") String timezone
    );
}
