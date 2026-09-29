package com.example.farmers.data.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class RetrofitClient {

    private static final String WEATHER_BASE_URL = "https://api.open-meteo.com/";
    private static final String GEOCODING_BASE_URL = "https://geocoding-api.open-meteo.com/";
    private static final String NOMINATIM_BASE_URL = "https://nominatim.openstreetmap.org/";
    private static final String GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/";

    private static Retrofit weatherRetrofit;
    private static Retrofit geocodingRetrofit;
    private static Retrofit nominatimRetrofit;
    private static Retrofit geminiRetrofit;

    private static OkHttpClient buildClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public static WeatherApiService getWeatherService() {
        if (weatherRetrofit == null) {
            weatherRetrofit = new Retrofit.Builder()
                    .baseUrl(WEATHER_BASE_URL)
                    .client(buildClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return weatherRetrofit.create(WeatherApiService.class);
    }

    public static WeatherApiService getGeocodingService() {
        if (geocodingRetrofit == null) {
            geocodingRetrofit = new Retrofit.Builder()
                    .baseUrl(GEOCODING_BASE_URL)
                    .client(buildClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return geocodingRetrofit.create(WeatherApiService.class);
    }

    public static NominatimApiService getNominatimService() {
        if (nominatimRetrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> chain.proceed(
                            chain.request().newBuilder()
                                    .header("User-Agent", "FarmWeatherPro/1.0 (android)")
                                    .build()))
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
            nominatimRetrofit = new Retrofit.Builder()
                    .baseUrl(NOMINATIM_BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return nominatimRetrofit.create(NominatimApiService.class);
    }

    public static GeminiApiService getGeminiService() {
        if (geminiRetrofit == null) {
            geminiRetrofit = new Retrofit.Builder()
                    .baseUrl(GEMINI_BASE_URL)
                    .client(buildClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return geminiRetrofit.create(GeminiApiService.class);
    }
}
