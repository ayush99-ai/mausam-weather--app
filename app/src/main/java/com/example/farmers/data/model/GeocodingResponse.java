package com.example.farmers.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GeocodingResponse {
    @SerializedName("results")
    public List<GeocodingResult> results;

    public static class GeocodingResult {
        @SerializedName("id")
        public int id;
        @SerializedName("name")
        public String name;
        @SerializedName("latitude")
        public double latitude;
        @SerializedName("longitude")
        public double longitude;
        @SerializedName("country")
        public String country;
        @SerializedName("admin1")
        public String admin1;
    }
}
