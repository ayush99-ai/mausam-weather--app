package com.example.farmers.data.model;

import com.google.gson.annotations.SerializedName;

public class NominatimResult {
    @SerializedName("display_name")
    public String displayName;
    @SerializedName("lat")
    public String lat;
    @SerializedName("lon")
    public String lon;
}
