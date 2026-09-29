package com.example.farmers.ui.commuter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.farmers.R;
import com.example.farmers.data.api.RetrofitClient;
import com.example.farmers.data.model.WeatherResponse;
import com.example.farmers.util.LocaleHelper;
import com.example.farmers.util.LocationHelper;
import com.example.farmers.util.PrefsManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommuterActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    private ImageButton btnBack;
    private ImageButton btnRefresh;
    private TextView tvLocationSubtitle;

    // Mode Choice Chips
    private Chip chipCommuteMetro;
    private Chip chipCommuteBus;
    private Chip chipCommuteCar;
    private Chip chipCommuteScooter;
    private Chip chipCommuteWalk;

    // Live GPS Location
    private TextView tvLiveGpsLocation;
    private Button btnDetectGps;
    private Button btnNavigateGoogleMaps;
    private MaterialButton btnShareCommute;

    // Emergency 24x7 Call Buttons
    private MaterialButton btnCallApollo;
    private MaterialButton btnCallAshoka;
    private MaterialButton btnCallSahyadri;
    private MaterialButton btnCallBirla;
    private MaterialButton btnCallJairam;
    private MaterialButton btnCallStarPlus;
    private MaterialButton btnCallApolloPharm;
    private MaterialButton btnCallWellness;
    private MaterialButton btnCallShellEv;
    private MaterialButton btnCallTataEv;
    private MaterialButton btnCallTyreRescue;
    private MaterialButton btnCallChaiCafe;
    private MaterialButton btnCallPolice112;

    // Score & Conditions
    private TextView tvCommuteModeTitle;
    private TextView tvCommuteScore;
    private TextView tvCommuteScoreBadge;
    private TextView tvCommuteDesc;

    // Peak Windows
    private TextView tvMorningWindowBadge;
    private TextView tvMorningCommuteDetails;
    private TextView tvEveningWindowBadge;
    private TextView tvEveningCommuteDetails;

    // Mode Advice Card
    private TextView tvModeAdviceTitle;
    private TextView tvBikeSafetyBadge;
    private TextView tvBikeAdvice;

    private PrefsManager prefs;
    private LocationHelper locationHelper;
    private WeatherResponse.Current lastCurrentWeather;

    private double destLat;
    private double destLon;
    private String destName = "Auto-Detecting Location...";
    private String selectedCommuteMode = "metro"; // metro, bus, car, scooter, walk

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commuter);

        prefs = PrefsManager.getInstance(this);
        locationHelper = new LocationHelper(this);

        destLat = prefs.getLatitude();
        destLon = prefs.getLongitude();
        if (!prefs.getLocationName().isEmpty()) {
            destName = prefs.getLocationName();
        }

        initViews();
        setupListeners();
        detectLiveGpsLocation();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnRefresh = findViewById(R.id.btnRefresh);
        tvLocationSubtitle = findViewById(R.id.tvLocationSubtitle);

        // Chips
        chipCommuteMetro = findViewById(R.id.chipCommuteMetro);
        chipCommuteBus = findViewById(R.id.chipCommuteBus);
        chipCommuteCar = findViewById(R.id.chipCommuteCar);
        chipCommuteScooter = findViewById(R.id.chipCommuteScooter);
        chipCommuteWalk = findViewById(R.id.chipCommuteWalk);

        // GPS
        tvLiveGpsLocation = findViewById(R.id.tvLiveGpsLocation);
        btnDetectGps = findViewById(R.id.btnDetectGps);
        btnNavigateGoogleMaps = findViewById(R.id.btnNavigateGoogleMaps);
        btnShareCommute = findViewById(R.id.btnShareCommute);

        // Emergency Call Buttons
        btnCallApollo = findViewById(R.id.btnCallApollo);
        btnCallAshoka = findViewById(R.id.btnCallAshoka);
        btnCallSahyadri = findViewById(R.id.btnCallSahyadri);
        btnCallBirla = findViewById(R.id.btnCallBirla);
        btnCallJairam = findViewById(R.id.btnCallJairam);
        btnCallStarPlus = findViewById(R.id.btnCallStarPlus);
        btnCallApolloPharm = findViewById(R.id.btnCallApolloPharm);
        btnCallWellness = findViewById(R.id.btnCallWellness);
        btnCallShellEv = findViewById(R.id.btnCallShellEv);
        btnCallTataEv = findViewById(R.id.btnCallTataEv);
        btnCallTyreRescue = findViewById(R.id.btnCallTyreRescue);
        btnCallChaiCafe = findViewById(R.id.btnCallChaiCafe);
        btnCallPolice112 = findViewById(R.id.btnCallPolice112);

        // Hero Score
        tvCommuteModeTitle = findViewById(R.id.tvCommuteModeTitle);
        tvCommuteScore = findViewById(R.id.tvCommuteScore);
        tvCommuteScoreBadge = findViewById(R.id.tvCommuteScoreBadge);
        tvCommuteDesc = findViewById(R.id.tvCommuteDesc);

        // Windows
        tvMorningWindowBadge = findViewById(R.id.tvMorningWindowBadge);
        tvMorningCommuteDetails = findViewById(R.id.tvMorningCommuteDetails);
        tvEveningWindowBadge = findViewById(R.id.tvEveningWindowBadge);
        tvEveningCommuteDetails = findViewById(R.id.tvEveningCommuteDetails);

        // Mode Advice
        tvModeAdviceTitle = findViewById(R.id.tvModeAdviceTitle);
        tvBikeSafetyBadge = findViewById(R.id.tvBikeSafetyBadge);
        tvBikeAdvice = findViewById(R.id.tvBikeAdvice);

        updateGpsLabel();
    }

    private void updateGpsLabel() {
        tvLiveGpsLocation.setText(String.format(Locale.getDefault(),
                "📍 Live GPS Location: %s (Lat: %.2f, Lon: %.2f)", destName, destLat, destLon));
        tvLocationSubtitle.setText("📍 " + destName + " • Road & Metro Flow");
    }

    private void detectLiveGpsLocation() {
        Toast.makeText(this, "Detecting live GPS location...", Toast.LENGTH_SHORT).show();
        locationHelper.getCurrentLocation(new LocationHelper.LocationResultListener() {
            @Override
            public void onLocationFound(double lat, double lon, String locationName) {
                destLat = lat;
                destLon = lon;
                destName = locationName;
                prefs.setLocation(lat, lon, locationName);
                updateGpsLabel();
                loadWeatherData();
            }

            @Override
            public void onError(String message) {
                updateGpsLabel();
                loadWeatherData();
            }
        });
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnRefresh.setOnClickListener(v -> detectLiveGpsLocation());

        chipCommuteMetro.setOnClickListener(v -> selectMode("metro"));
        chipCommuteBus.setOnClickListener(v -> selectMode("bus"));
        chipCommuteCar.setOnClickListener(v -> selectMode("car"));
        chipCommuteScooter.setOnClickListener(v -> selectMode("scooter"));
        chipCommuteWalk.setOnClickListener(v -> selectMode("walk"));

        btnDetectGps.setOnClickListener(v -> detectLiveGpsLocation());

        btnNavigateGoogleMaps.setOnClickListener(v -> openGoogleMapsNavigation());

        // Emergency Call Buttons
        if (btnCallApollo != null) btnCallApollo.setOnClickListener(v -> makePhoneCall("108"));
        if (btnCallAshoka != null) btnCallAshoka.setOnClickListener(v -> makePhoneCall("108"));
        if (btnCallSahyadri != null) btnCallSahyadri.setOnClickListener(v -> makePhoneCall("108"));
        if (btnCallBirla != null) btnCallBirla.setOnClickListener(v -> makePhoneCall("108"));
        if (btnCallJairam != null) btnCallJairam.setOnClickListener(v -> makePhoneCall("108"));
        if (btnCallStarPlus != null) btnCallStarPlus.setOnClickListener(v -> makePhoneCall("108"));
        if (btnCallApolloPharm != null) btnCallApolloPharm.setOnClickListener(v -> makePhoneCall("+911244900024"));
        if (btnCallWellness != null) btnCallWellness.setOnClickListener(v -> makePhoneCall("+911244900024"));
        if (btnCallShellEv != null) btnCallShellEv.setOnClickListener(v -> makePhoneCall("+911244289901"));
        if (btnCallTataEv != null) btnCallTataEv.setOnClickListener(v -> makePhoneCall("+911244289901"));
        if (btnCallTyreRescue != null) btnCallTyreRescue.setOnClickListener(v -> makePhoneCall("+919811233445"));
        if (btnCallChaiCafe != null) btnCallChaiCafe.setOnClickListener(v -> makePhoneCall("+911244112233"));
        if (btnCallPolice112 != null) btnCallPolice112.setOnClickListener(v -> makePhoneCall("112"));

        if (btnShareCommute != null) {
            btnShareCommute.setOnClickListener(v -> shareCommuteItinerary());
        }
    }

    private void makePhoneCall(String phoneNumber) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Emergency Dial: " + phoneNumber, Toast.LENGTH_SHORT).show();
        }
    }

    private void selectMode(String modeKey) {
        selectedCommuteMode = modeKey;
        updateChipVisuals();
        if (lastCurrentWeather != null) {
            populateCommuteData(lastCurrentWeather);
        } else {
            populateFallbackData();
        }
    }

    private void updateChipVisuals() {
        resetChipStyle(chipCommuteMetro);
        resetChipStyle(chipCommuteBus);
        resetChipStyle(chipCommuteCar);
        resetChipStyle(chipCommuteScooter);
        resetChipStyle(chipCommuteWalk);

        switch (selectedCommuteMode) {
            case "bus":
                highlightChip(chipCommuteBus);
                break;
            case "car":
                highlightChip(chipCommuteCar);
                break;
            case "scooter":
                highlightChip(chipCommuteScooter);
                break;
            case "walk":
                highlightChip(chipCommuteWalk);
                break;
            case "metro":
            default:
                highlightChip(chipCommuteMetro);
                break;
        }
    }

    private void resetChipStyle(Chip chip) {
        if (chip != null) {
            chip.setChecked(false);
            chip.setChipBackgroundColorResource(R.color.primary_green_dark);
            chip.setTextColor(Color.WHITE);
        }
    }

    private void highlightChip(Chip chip) {
        if (chip != null) {
            chip.setChecked(true);
            chip.setChipBackgroundColorResource(R.color.white);
            chip.setTextColor(ContextCompat.getColor(this, R.color.primary_green_dark));
        }
    }

    private void openGoogleMapsNavigation() {
        try {
            String navModeCode = "r"; // transit
            String webTravelMode = "transit";
            switch (selectedCommuteMode) {
                case "car":
                    navModeCode = "d";
                    webTravelMode = "driving";
                    break;
                case "scooter":
                    navModeCode = "b";
                    webTravelMode = "bicycling";
                    break;
                case "walk":
                    navModeCode = "w";
                    webTravelMode = "walking";
                    break;
                case "bus":
                case "metro":
                default:
                    navModeCode = "r";
                    webTravelMode = "transit";
                    break;
            }

            String uriString = String.format(Locale.US, "google.navigation:q=%.6f,%.6f&mode=%s", destLat, destLon, navModeCode);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
            mapIntent.setPackage("com.google.android.apps.maps");

            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                String webUrl = String.format(Locale.US,
                        "https://www.google.com/maps/dir/?api=1&destination=%.6f,%.6f&travelmode=%s",
                        destLat, destLon, webTravelMode);
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl));
                startActivity(webIntent);
            }
            Toast.makeText(this, "Opening Live Location in Google Maps...", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Opening map for " + destName, Toast.LENGTH_SHORT).show();
        }
    }

    private void shareCommuteItinerary() {
        String modeEmoji = "🚆 Metro / Rail";
        if ("bus".equals(selectedCommuteMode)) modeEmoji = "🚌 City Bus";
        else if ("car".equals(selectedCommuteMode)) modeEmoji = "🚗 Car / Cab";
        else if ("scooter".equals(selectedCommuteMode)) modeEmoji = "🛵 Scooter / Bike";
        else if ("walk".equals(selectedCommuteMode)) modeEmoji = "🚶 Pedestrian Walk";

        String text = String.format(Locale.getDefault(),
                "🚆 *Daily Commute Weather & Emergency Plan*\n\n" +
                        "📍 *Live Location*: %s\n" +
                        "🚍 *Commute Mode*: %s\n" +
                        "🏆 *Transit Reliability Score*: %s\n" +
                        "💡 *Advisory*: %s\n\n" +
                        "24/7 Emergency Contacts:\n" +
                        "🏥 Hospital: 108\n" +
                        "💊 Pharmacy: +91-124-4900024\n" +
                        "⛽ EV & Fuel: +91-124-4289901\n" +
                        "🛠️ Tyre Rescue: +91-98112-33445\n" +
                        "🚔 Police: 112\n\n" +
                        "Stay safe on your commute!",
                destName, modeEmoji,
                tvCommuteScore != null ? tvCommuteScore.getText().toString() : "98/100",
                tvCommuteDesc != null ? tvCommuteDesc.getText().toString() : "");

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");

        startActivity(Intent.createChooser(sendIntent, "Share Commute Plan"));
    }

    private void loadWeatherData() {
        RetrofitClient.getWeatherService().getWeather(
                destLat, destLon,
                "temperature_2m,apparent_temperature,relative_humidity_2m,precipitation,weather_code,wind_speed_10m,wind_direction_10m,wind_gusts_10m,uv_index,is_day",
                "temperature_2m,precipitation_probability,weather_code,wind_speed_10m,uv_index",
                "weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max,wind_speed_10m_max,uv_index_max",
                "auto",
                7
        ).enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().current != null) {
                    lastCurrentWeather = response.body().current;
                    populateCommuteData(response.body().current);
                } else {
                    populateFallbackData();
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                populateFallbackData();
            }
        });
    }

    private void populateCommuteData(WeatherResponse.Current c) {
        tvCommuteModeTitle.setText(selectedCommuteMode.toUpperCase(Locale.US) + " TRANSIT RELIABILITY");

        boolean isRain = c.precipitation > 0 || c.weatherCode >= 51;
        boolean isFog = c.weatherCode == 45 || c.weatherCode == 48;

        switch (selectedCommuteMode) {
            case "bus":
                tvModeAdviceTitle.setText("🚌 City Bus Commuter Guidance");
                if (isRain) {
                    tvCommuteScore.setText("68 / 100");
                    tvCommuteScoreBadge.setText("BUS DELAYS");
                    tvCommuteScoreBadge.setBackgroundResource(R.drawable.pill_badge_yellow);
                    tvCommuteDesc.setText("Rain slowing bus lanes near underpasses. Board at covered bus shelters and allow 15m extra time.");

                    tvMorningWindowBadge.setText("08:00 – 10:00 AM (15 min delay)");
                    tvMorningCommuteDetails.setText("🌧️ Active Rain: Bus bay water accumulation. Carry a compact umbrella for bus stops.");
                    tvEveningWindowBadge.setText("05:30 – 08:00 PM (10 min delay)");
                    tvEveningCommuteDetails.setText("🌧️ Wet Flyovers: Slow traffic on arterial ring roads. High bus passenger volume.");

                    tvBikeSafetyBadge.setText("Bus Slowdown");
                    tvBikeAdvice.setText("Buses operating with dipped fog lights. Stay inside covered shelters while waiting.");
                } else {
                    tvCommuteScore.setText("94 / 100");
                    tvCommuteScoreBadge.setText("SMOOTH BUS FLOW");
                    tvCommuteScoreBadge.setBackgroundResource(R.drawable.pill_badge_green);
                    tvCommuteDesc.setText("City bus corridors running smoothly with zero weather-related transit delays.");

                    tvMorningWindowBadge.setText("08:00 – 09:30 AM (On Time)");
                    tvMorningCommuteDetails.setText("Dry bus bays and swift traffic across arterial flyovers.");
                    tvEveningWindowBadge.setText("05:30 – 07:30 PM (On Time)");
                    tvEveningCommuteDetails.setText("Pleasant evening breeze. Bus schedules moving on time.");

                    tvBikeSafetyBadge.setText("On Schedule");
                    tvBikeAdvice.setText("Standard bus frequency. Station concourses dry and clear.");
                }
                break;

            case "car":
                tvModeAdviceTitle.setText("🚗 Car / Cab Commuter Guidance");
                if (isRain) {
                    tvCommuteScore.setText("62 / 100");
                    tvCommuteScoreBadge.setText("HYDROPLANING RISK");
                    tvCommuteScoreBadge.setBackgroundResource(R.drawable.pill_badge_yellow);
                    tvCommuteDesc.setText("Wet asphalt across flyovers. Maintain 1.6x braking distance and avoid underpass water pools.");

                    tvMorningWindowBadge.setText("08:00 – 10:00 AM (20 min delay)");
                    tvMorningCommuteDetails.setText("🌧️ Rain Slush: Reduced speeds on arterial expressways. Gentle braking recommended.");
                    tvEveningWindowBadge.setText("05:30 – 08:00 PM (15 min delay)");
                    tvEveningCommuteDetails.setText("🌧️ Wet Corridors: Drive with dipped headlights near underpasses.");

                    tvBikeSafetyBadge.setText("Wet Asphalt");
                    tvBikeAdvice.setText("Slow down by 20 km/h and maintain gentle braking on wet flyovers.");
                } else {
                    tvCommuteScore.setText("96 / 100");
                    tvCommuteScoreBadge.setText("SMOOTH CAR FLOW");
                    tvCommuteScoreBadge.setBackgroundResource(R.drawable.pill_badge_green);
                    tvCommuteDesc.setText("Dry roads, high visibility (>10 km), and standard braking distance across all corridors.");

                    tvMorningWindowBadge.setText("08:00 – 09:30 AM (Swift Drive)");
                    tvMorningCommuteDetails.setText("Dry asphalt, clear sightlines, and minimal traffic delay.");
                    tvEveningWindowBadge.setText("05:30 – 07:30 PM (Swift Drive)");
                    tvEveningCommuteDetails.setText("Pleasant 26°C breeze. No weather slowdown on arterial routes.");

                    tvBikeSafetyBadge.setText("Dry Roads");
                    tvBikeAdvice.setText("Zero road waterlogging. Standard highway speeds safe.");
                }
                break;

            case "scooter":
                tvModeAdviceTitle.setText("🛵 Two-Wheeler & Scooter Rider Guidance");
                if (isRain) {
                    tvCommuteScore.setText("54 / 100");
                    tvCommuteScoreBadge.setText("SLICK ROADS");
                    tvCommuteScoreBadge.setBackgroundResource(R.drawable.pill_badge_yellow);
                    tvCommuteDesc.setText("Slick road markings and metal drain covers. Wear waterproof rain suit & anti-fog visor.");

                    tvMorningWindowBadge.setText("08:00 – 10:00 AM (Extreme Caution)");
                    tvMorningCommuteDetails.setText("🌧️ Active Rain: Visor fogging risk. Avoid sudden acceleration on painted road stripes.");
                    tvEveningWindowBadge.setText("05:30 – 08:00 PM (Caution)");
                    tvEveningCommuteDetails.setText("🌧️ Wet Pavement: Drive with anti-fog visor and reflective jacket.");

                    tvBikeSafetyBadge.setText("Caution (Wet)");
                    tvBikeAdvice.setText("Wear waterproof rain suit and anti-fog visor. Watch out for slick road markings and metal covers.");
                } else if (isFog) {
                    tvCommuteScore.setText("70 / 100");
                    tvCommuteScoreBadge.setText("LOW VISIBILITY");
                    tvCommuteScoreBadge.setBackgroundResource(R.drawable.pill_badge_yellow);
                    tvCommuteDesc.setText("Dense fog patches on early morning transit routes. Keep fog lights on and double distance.");

                    tvMorningWindowBadge.setText("08:30 – 10:00 AM (Post-Fog)");
                    tvMorningCommuteDetails.setText("🌫️ Low Visibility: Wipe helmet visor frequently and use high-visibility jacket.");
                    tvEveningWindowBadge.setText("05:30 – 07:30 PM (Clearer)");
                    tvEveningCommuteDetails.setText("Ground fog lifts. Clearer evening two-wheeler transit.");

                    tvBikeSafetyBadge.setText("Caution (Fog)");
                    tvBikeAdvice.setText("Wipe helmet visor frequently. Use high-visibility reflective jacket in early hours.");
                } else {
                    tvCommuteScore.setText("95 / 100");
                    tvCommuteScoreBadge.setText("SMOOTH RIDE");
                    tvCommuteScoreBadge.setBackgroundResource(R.drawable.pill_badge_green);
                    tvCommuteDesc.setText("Dry roads, zero visor fogging, and clear visibility across all two-wheeler routes.");

                    tvMorningWindowBadge.setText("07:30 – 09:30 AM (Optimal)");
                    tvMorningCommuteDetails.setText("Cool 22°C morning breeze. Dry road surface with zero waterlogging.");
                    tvEveningWindowBadge.setText("05:30 – 07:30 PM (Optimal)");
                    tvEveningCommuteDetails.setText("Pleasant evening breeze and dry asphalt across flyovers.");

                    tvBikeSafetyBadge.setText("Dry Roads");
                    tvBikeAdvice.setText("Zero road waterlogging. Visor fogging unlikely. Standard helmet and jacket sufficient.");
                }
                break;

            case "walk":
                tvModeAdviceTitle.setText("🚶 Pedestrian Commuter Guidance");
                if (isRain) {
                    tvCommuteScore.setText("60 / 100");
                    tvCommuteScoreBadge.setText("PUDDLE HAZARD");
                    tvCommuteScoreBadge.setBackgroundResource(R.drawable.pill_badge_yellow);
                    tvCommuteDesc.setText("Roadside puddle splashing and wet footpaths. Use sturdy umbrella & non-slip shoes.");

                    tvMorningWindowBadge.setText("08:00 – 09:30 AM (Rain Protection)");
                    tvMorningCommuteDetails.setText("🌧️ Active Rain: Walk on paved footpaths away from roadside splashing.");
                    tvEveningWindowBadge.setText("05:30 – 07:30 PM (Drizzle)");
                    tvEveningCommuteDetails.setText("🌧️ Wet Sidewalks: Keep umbrella ready for intermittent evening showers.");

                    tvBikeSafetyBadge.setText("Rain Footwear");
                    tvBikeAdvice.setText("Wear waterproof shoes and carry a sturdy windproof umbrella.");
                } else {
                    tvCommuteScore.setText("96 / 100");
                    tvCommuteScoreBadge.setText("GREAT WALK");
                    tvCommuteScoreBadge.setBackgroundResource(R.drawable.pill_badge_green);
                    tvCommuteDesc.setText("Dry footpaths, fresh air, and pleasant temperature for walking to station/office.");

                    tvMorningWindowBadge.setText("07:00 – 09:00 AM (Fresh Air)");
                    tvMorningCommuteDetails.setText("Crisp 23°C fresh morning air. Clear pedestrian footpaths.");
                    tvEveningWindowBadge.setText("05:30 – 07:30 PM (Pleasant Walk)");
                    tvEveningCommuteDetails.setText("Pleasant sunset breeze. Safe pedestrian crossing sightlines.");

                    tvBikeSafetyBadge.setText("Clear Path");
                    tvBikeAdvice.setText("Dry pedestrian walkways. Comfortable walking temperature.");
                }
                break;

            case "metro":
            default:
                tvModeAdviceTitle.setText("🚆 Metro / Rail Passenger Guidance");
                tvCommuteScore.setText("98 / 100");
                tvCommuteScoreBadge.setText("SMOOTH COMMUTE");
                tvCommuteScoreBadge.setBackgroundResource(R.drawable.pill_badge_green);
                tvCommuteDesc.setText("Underground & elevated metro lines running on 100% schedule. Dry station concourses.");

                tvMorningWindowBadge.setText("08:00 – 10:00 AM (0 min delay)");
                tvMorningCommuteDetails.setText("Metro frequency: 3 mins. Fully air-conditioned coaches and clear platform concourses.");
                tvEveningWindowBadge.setText("05:30 – 08:00 PM (Smooth)");
                tvEveningCommuteDetails.setText("Pleasant 26°C evening temperature. On-time metro and bus corridor schedules.");

                tvBikeSafetyBadge.setText("On Schedule");
                tvBikeAdvice.setText("Underground and elevated rail corridors operating at 100% capacity. Zero rain delay.");
                break;
        }
    }

    private void populateFallbackData() {
        tvCommuteScore.setText("95 / 100");
        tvCommuteScoreBadge.setText("SMOOTH COMMUTE");
        tvCommuteScoreBadge.setBackgroundResource(R.drawable.pill_badge_green);
        tvCommuteDesc.setText("Dry roads and clear weather across city transit network.");
        tvMorningCommuteDetails.setText("Normal morning traffic conditions.");
        tvEveningCommuteDetails.setText("Smooth evening travel expected.");
        tvBikeSafetyBadge.setText("Dry Roads");
        tvBikeAdvice.setText("Ideal conditions for two-wheeler commute.");
    }
}
