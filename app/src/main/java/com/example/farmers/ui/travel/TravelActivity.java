package com.example.farmers.ui.travel;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmers.R;
import com.example.farmers.data.api.RetrofitClient;
import com.example.farmers.data.model.GeocodingResponse;
import com.example.farmers.data.model.RoadsideStop;
import com.example.farmers.data.model.WeatherResponse;
import com.example.farmers.ui.adapter.RoadsideStopAdapter;
import com.example.farmers.util.LocaleHelper;
import com.example.farmers.util.PrefsManager;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TravelActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    private ImageButton btnBack;
    private ImageButton btnRefresh;
    private TextView tvLocationSubtitle;

    // Destination Search & Trip Planner
    private EditText etDestinationSearch;
    private Button btnSearchDestination;
    private TextView tvSelectedDestination;
    private Button btnTripDate;
    private Chip chipModeCar, chipModeBike, chipModeBus, chipModeWalk;
    private Button btnSubmitTrip;
    private Button btnNavigateGoogleMaps;

    // Recommendations Container
    private View layoutRecommendations;
    private TextView tvBestTimeTitle;
    private TextView tvBestTimeWindow;
    private TextView tvBestTimeAdvice;
    private TextView tvTripFuelEstimate;
    private Button btnEmergencySos;
    private Button btnShareTripPlan;

    // Safety Tab Views
    private TextView tvRoadStatusBadge;
    private TextView tvRoadSafetyTitle;
    private TextView tvRoadSafetyDesc;
    private TextView tvVisibility;
    private TextView tvBrakingDistance;
    private TextView tvCrosswinds;
    private TextView tvFogHazard;

    // Commute Tab Views
    private TextView tvCommuteScore;
    private TextView tvCommuteScoreBadge;
    private TextView tvCommuteDesc;
    private TextView tvMorningCommuteDetails;
    private TextView tvEveningCommuteDetails;
    private TextView tvBikeSafetyBadge;
    private TextView tvBikeAdvice;

    // Packing Tab
    private CheckBox cbRainGear;

    // Tab Layout Containers
    private View layoutTabSafety;
    private View layoutTabGps;
    private View layoutTabCommute;
    private View layoutTabAlerts;
    private View layoutTabPacking;

    // Navigation Chips
    private Chip tabSafety, tabGps, tabCommute, tabAlerts, tabPacking;

    // GPS & Stops Tab
    private RecyclerView rvRoadsideStops;
    private Chip filterAll, filterFuelEv, filterTyre, filterCafe, filterPharmacy, filterPolice, filterAtm;
    private List<RoadsideStop> allStops;

    private PrefsManager prefs;

    // State Variables
    private double destLat;
    private double destLon;
    private String destName = "Current Highway Corridor";
    private String selectedDateDisplay = "Today";
    private String selectedTransportMode = "car"; // car, bike, bus, walk

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel);

        prefs = PrefsManager.getInstance(this);
        destLat = prefs.getLatitude();
        destLon = prefs.getLongitude();
        if (!prefs.getLocationName().isEmpty()) {
            destName = prefs.getLocationName();
        }

        initViews();
        setupTabs();
        setupTripPlanner();
        setupRoadsideStops();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnRefresh = findViewById(R.id.btnRefresh);
        tvLocationSubtitle = findViewById(R.id.tvLocationSubtitle);

        // Destination Search & Trip Planner
        etDestinationSearch = findViewById(R.id.etDestinationSearch);
        btnSearchDestination = findViewById(R.id.btnSearchDestination);
        tvSelectedDestination = findViewById(R.id.tvSelectedDestination);
        btnTripDate = findViewById(R.id.btnTripDate);
        chipModeCar = findViewById(R.id.chipModeCar);
        chipModeBike = findViewById(R.id.chipModeBike);
        chipModeBus = findViewById(R.id.chipModeBus);
        chipModeWalk = findViewById(R.id.chipModeWalk);
        btnSubmitTrip = findViewById(R.id.btnSubmitTrip);
        btnNavigateGoogleMaps = findViewById(R.id.btnNavigateGoogleMaps);

        // Recommendations Container
        layoutRecommendations = findViewById(R.id.layoutRecommendations);
        tvBestTimeTitle = findViewById(R.id.tvBestTimeTitle);
        tvBestTimeWindow = findViewById(R.id.tvBestTimeWindow);
        tvBestTimeAdvice = findViewById(R.id.tvBestTimeAdvice);
        tvTripFuelEstimate = findViewById(R.id.tvTripFuelEstimate);
        btnEmergencySos = findViewById(R.id.btnEmergencySos);
        btnShareTripPlan = findViewById(R.id.btnShareTripPlan);

        // Safety
        tvRoadStatusBadge = findViewById(R.id.tvRoadStatusBadge);
        tvRoadSafetyTitle = findViewById(R.id.tvRoadSafetyTitle);
        tvRoadSafetyDesc = findViewById(R.id.tvRoadSafetyDesc);
        tvVisibility = findViewById(R.id.tvVisibility);
        tvBrakingDistance = findViewById(R.id.tvBrakingDistance);
        tvCrosswinds = findViewById(R.id.tvCrosswinds);
        tvFogHazard = findViewById(R.id.tvFogHazard);

        // Commute
        tvCommuteScore = findViewById(R.id.tvCommuteScore);
        tvCommuteScoreBadge = findViewById(R.id.tvCommuteScoreBadge);
        tvCommuteDesc = findViewById(R.id.tvCommuteDesc);
        tvMorningCommuteDetails = findViewById(R.id.tvMorningCommuteDetails);
        tvEveningCommuteDetails = findViewById(R.id.tvEveningCommuteDetails);
        tvBikeSafetyBadge = findViewById(R.id.tvBikeSafetyBadge);
        tvBikeAdvice = findViewById(R.id.tvBikeAdvice);

        // Packing
        cbRainGear = findViewById(R.id.cbRainGear);

        // Tab Containers
        layoutTabSafety = findViewById(R.id.layoutTabSafety);
        layoutTabGps = findViewById(R.id.layoutTabGps);
        layoutTabCommute = findViewById(R.id.layoutTabCommute);
        layoutTabAlerts = findViewById(R.id.layoutTabAlerts);
        layoutTabPacking = findViewById(R.id.layoutTabPacking);

        // Nav Chips
        tabSafety = findViewById(R.id.tabSafety);
        tabGps = findViewById(R.id.tabGps);
        tabCommute = findViewById(R.id.tabCommute);
        tabAlerts = findViewById(R.id.tabAlerts);
        tabPacking = findViewById(R.id.tabPacking);

        // GPS Stops
        rvRoadsideStops = findViewById(R.id.rvRoadsideStops);
        filterAll = findViewById(R.id.filterAll);
        filterFuelEv = findViewById(R.id.filterFuelEv);
        filterTyre = findViewById(R.id.filterTyre);
        filterCafe = findViewById(R.id.filterCafe);
        filterPharmacy = findViewById(R.id.filterPharmacy);
        filterPolice = findViewById(R.id.filterPolice);
        filterAtm = findViewById(R.id.filterAtm);

        updateDestinationLabel();
    }

    private void updateDestinationLabel() {
        tvSelectedDestination.setText(String.format(Locale.getDefault(),
                "🎯 Target Destination: %s (Lat: %.2f, Lon: %.2f)", destName, destLat, destLon));
        tvLocationSubtitle.setText("📍 " + destName + " • Route GPS & Safety");
    }

    private void setupTripPlanner() {
        btnSearchDestination.setOnClickListener(v -> searchDestination());

        btnTripDate.setOnClickListener(v -> showDatePicker());

        chipModeCar.setOnClickListener(v -> selectedTransportMode = "car");
        chipModeBike.setOnClickListener(v -> selectedTransportMode = "bike");
        chipModeBus.setOnClickListener(v -> selectedTransportMode = "bus");
        chipModeWalk.setOnClickListener(v -> selectedTransportMode = "walk");

        btnSubmitTrip.setOnClickListener(v -> submitAndGenerateRecommendations());

        btnNavigateGoogleMaps.setOnClickListener(v -> openGoogleMapsNavigation());

        btnEmergencySos.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Emergency Dial: 112", Toast.LENGTH_SHORT).show();
            }
        });

        btnShareTripPlan.setOnClickListener(v -> shareTripItinerary());
    }

    private void submitAndGenerateRecommendations() {
        String query = etDestinationSearch.getText() != null ? etDestinationSearch.getText().toString().trim() : "";
        if (!query.isEmpty() && !query.equalsIgnoreCase(destName)) {
            btnSubmitTrip.setEnabled(false);
            RetrofitClient.getGeocodingService().searchCity(query, 1, "en", "json")
                    .enqueue(new Callback<GeocodingResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<GeocodingResponse> call, @NonNull Response<GeocodingResponse> response) {
                            btnSubmitTrip.setEnabled(true);
                            if (response.isSuccessful() && response.body() != null &&
                                    response.body().results != null && !response.body().results.isEmpty()) {
                                GeocodingResponse.GeocodingResult res = response.body().results.get(0);
                                destLat = res.latitude;
                                destLon = res.longitude;
                                destName = res.name + (res.country != null ? ", " + res.country : "");
                                updateDestinationLabel();
                            } else {
                                destName = query;
                                updateDestinationLabel();
                            }
                            executeSubmitFlow();
                        }

                        @Override
                        public void onFailure(@NonNull Call<GeocodingResponse> call, @NonNull Throwable t) {
                            btnSubmitTrip.setEnabled(true);
                            destName = query;
                            updateDestinationLabel();
                            executeSubmitFlow();
                        }
                    });
        } else {
            executeSubmitFlow();
        }
    }

    private void executeSubmitFlow() {
        Toast.makeText(this, "Calculating best departure window for " + destName + "...", Toast.LENGTH_SHORT).show();

        loadWeatherData();

        if (layoutRecommendations != null) {
            layoutRecommendations.setVisibility(View.VISIBLE);
        }
    }

    private void shareTripItinerary() {
        String modeEmoji = "🚗 Car";
        if ("bike".equals(selectedTransportMode)) modeEmoji = "🏍️ Bike";
        else if ("bus".equals(selectedTransportMode)) modeEmoji = "🚌 Bus";
        else if ("walk".equals(selectedTransportMode)) modeEmoji = "🚶 Walk";

        String text = String.format(Locale.getDefault(),
                "🧳 *Travel Itinerary & Weather Plan*\n\n" +
                        "📍 *Destination*: %s\n" +
                        "📅 *Date*: %s\n" +
                        "🚗 *Transport Mode*: %s\n" +
                        "⭐ *Recommended Departure*: %s\n\n" +
                        "Stay safe on the route!",
                destName, selectedDateDisplay, modeEmoji,
                tvBestTimeWindow != null ? tvBestTimeWindow.getText().toString() : "07:30 AM - 09:30 AM");

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, "Share Trip Itinerary");
        startActivity(shareIntent);
    }

    private void searchDestination() {
        String query = etDestinationSearch.getText() != null ? etDestinationSearch.getText().toString().trim() : "";
        if (query.isEmpty()) {
            etDestinationSearch.setError("Enter destination city or place");
            return;
        }

        btnSearchDestination.setEnabled(false);
        Toast.makeText(this, "Searching destination: " + query + "...", Toast.LENGTH_SHORT).show();

        RetrofitClient.getGeocodingService().searchCity(query, 1, "en", "json")
                .enqueue(new Callback<GeocodingResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<GeocodingResponse> call, @NonNull Response<GeocodingResponse> response) {
                        btnSearchDestination.setEnabled(true);
                        if (response.isSuccessful() && response.body() != null &&
                                response.body().results != null && !response.body().results.isEmpty()) {
                            GeocodingResponse.GeocodingResult res = response.body().results.get(0);
                            destLat = res.latitude;
                            destLon = res.longitude;
                            destName = res.name + (res.country != null ? ", " + res.country : "");

                            updateDestinationLabel();
                            Toast.makeText(TravelActivity.this, "Destination set: " + destName, Toast.LENGTH_SHORT).show();
                        } else {
                            destName = query;
                            updateDestinationLabel();
                            Toast.makeText(TravelActivity.this, "Destination set: " + query, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<GeocodingResponse> call, @NonNull Throwable t) {
                        btnSearchDestination.setEnabled(true);
                        destName = query;
                        updateDestinationLabel();
                        Toast.makeText(TravelActivity.this, "Destination set: " + query, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            selectedDateDisplay = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month1 + 1, year1);
            btnTripDate.setText("📅 Travel Date: " + selectedDateDisplay + " (Tap to change date)");
            Toast.makeText(this, "Date set to: " + selectedDateDisplay, Toast.LENGTH_SHORT).show();
        }, year, month, day);

        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.show();
    }

    private void openGoogleMapsNavigation() {
        try {
            String query = etDestinationSearch != null && etDestinationSearch.getText() != null ?
                    etDestinationSearch.getText().toString().trim() : "";

            String targetDest = !query.isEmpty() ? query : destName;
            if (targetDest.isEmpty() || "Current Highway Corridor".equalsIgnoreCase(targetDest)) {
                targetDest = "Pune";
            }

            String navModeCode = "d"; // driving
            String webTravelMode = "driving";
            switch (selectedTransportMode) {
                case "bike":
                    navModeCode = "b";
                    webTravelMode = "bicycling";
                    break;
                case "bus":
                    navModeCode = "r";
                    webTravelMode = "transit";
                    break;
                case "walk":
                    navModeCode = "w";
                    webTravelMode = "walking";
                    break;
                case "car":
                default:
                    navModeCode = "d";
                    webTravelMode = "driving";
                    break;
            }

            // Primary Google Maps intent targeting entered destination location
            String uriString;
            if (destLat != 0.0 && destLon != 0.0 && query.isEmpty()) {
                uriString = String.format(Locale.US, "google.navigation:q=%.6f,%.6f&mode=%s", destLat, destLon, navModeCode);
            } else {
                uriString = String.format(Locale.US, "google.navigation:q=%s&mode=%s", Uri.encode(targetDest), navModeCode);
            }

            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
            mapIntent.setPackage("com.google.android.apps.maps");

            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                // Universal Google Maps Web & App Fallback
                String webUrl = String.format(Locale.US,
                        "https://www.google.com/maps/dir/?api=1&destination=%s&travelmode=%s",
                        Uri.encode(targetDest), webTravelMode);
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl));
                startActivity(webIntent);
            }
            Toast.makeText(this, "Opening Google Maps for " + targetDest, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Opening map for " + destName, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateBestTimeRecommendation(WeatherResponse.Current c) {
        String modeUpper = selectedTransportMode.toUpperCase();
        tvBestTimeTitle.setText(String.format("RECOMMENDED BEST TIME OF DAY (%s - %s)", modeUpper, selectedDateDisplay));

        // Fuel Estimate
        switch (selectedTransportMode) {
            case "bike":
                tvTripFuelEstimate.setText("⛽ Est. Fuel Requirement: ~3.5 Liters (Petrol) • High two-wheeler mileage");
                break;
            case "bus":
                tvTripFuelEstimate.setText("🚌 Est. Public Transit Fare: Standard bus pass / Ticket • Low carbon footprint");
                break;
            case "walk":
                tvTripFuelEstimate.setText("👟 Est. Footwear Comfort: Wear breathable walking shoes • Hydration recommended");
                break;
            case "car":
            default:
                tvTripFuelEstimate.setText("⛽ Est. Fuel Requirement: ~10.5 Liters (or ~24 kWh EV Charging Buffer)");
                break;
        }

        if (c == null) {
            tvBestTimeWindow.setText("07:30 AM - 09:30 AM");
            tvBestTimeAdvice.setText("⭐ Optimal Morning Window: Dry asphalt, mild breeze, clear visibility, and minimal rain probability.");
            return;
        }

        boolean isRain = c.precipitation > 0 || c.weatherCode >= 51;
        boolean isFog = c.weatherCode == 45 || c.weatherCode == 48;

        switch (selectedTransportMode) {
            case "bike":
                if (isRain) {
                    tvBestTimeWindow.setText("11:30 AM - 01:30 PM (Post-Rain)");
                    tvBestTimeAdvice.setText("🏍️ Bike Recommendation: Rain active earlier. Best departure is mid-day when roads dry out. Wear waterproof rain suit & anti-fog visor.");
                } else if (isFog) {
                    tvBestTimeWindow.setText("09:30 AM - 11:30 AM (Post-Fog)");
                    tvBestTimeAdvice.setText("🏍️ Bike Recommendation: Heavy fog early morning. Depart after 09:30 AM as ground fog lifts. Use high-visibility reflective jacket.");
                } else {
                    tvBestTimeWindow.setText("07:00 AM - 09:30 AM • Prime Window");
                    tvBestTimeAdvice.setText("🏍️ Bike Recommendation: Excellent dry roads! Cool morning temperature (22°C-25°C) and zero visor fogging risk.");
                }
                break;

            case "bus":
                if (isRain) {
                    tvBestTimeWindow.setText("08:00 AM - 10:00 AM (Express Bus)");
                    tvBestTimeAdvice.setText("🚌 Bus/Transit Recommendation: Active rain slowdowns. Board express buses with covered shelters. Keep umbrella ready for bus stops.");
                } else {
                    tvBestTimeWindow.setText("08:15 AM - 09:45 AM • Fast Transit");
                    tvBestTimeAdvice.setText("🚌 Bus/Transit Recommendation: Smooth traffic flow and dry bus bays. On-time transit arrivals expected across arterial routes.");
                }
                break;

            case "walk":
                if (c.temperature2m > 32) {
                    tvBestTimeWindow.setText("06:30 AM - 08:30 AM (Cool Window)");
                    tvBestTimeAdvice.setText("🚶 Walk Recommendation: High afternoon temperatures. Best walking time is early morning before heat peaks. Stay hydrated!");
                } else if (isRain) {
                    tvBestTimeWindow.setText("10:00 AM - 11:30 AM (Drizzle Ease)");
                    tvBestTimeAdvice.setText("🚶 Walk Recommendation: Active rain. Carry a sturdy umbrella & non-slip footwear. Walk on paved footpaths away from roadside puddles.");
                } else {
                    tvBestTimeWindow.setText("07:00 AM - 09:00 AM • Great Walk");
                    tvBestTimeAdvice.setText("🚶 Walk Recommendation: Pleasant 24°C fresh morning air. Clear pedestrian sightlines and dry footpaths.");
                }
                break;

            case "car":
            default:
                if (isRain) {
                    tvBestTimeWindow.setText("10:00 AM - 12:00 PM (Lighter Rain)");
                    tvBestTimeAdvice.setText("🚗 Car Recommendation: Heavy rain early rush hour. Depart after 10:00 AM when cloud intensity drops. Maintain gentle braking & 1.4x distance.");
                } else if (isFog) {
                    tvBestTimeWindow.setText("09:00 AM - 11:00 AM (Clear Sightlines)");
                    tvBestTimeAdvice.setText("🚗 Car Recommendation: Low morning visibility. Best departure after 09:00 AM. Keep fog lamps on and drive under 50 km/h.");
                } else {
                    tvBestTimeWindow.setText("07:30 AM - 09:30 AM • Optimal Driving");
                    tvBestTimeAdvice.setText("⭐ Best Window for Car: Dry roads, clear 10 km visibility, 24°C mild weather, and minimal rain or traffic delay risk.");
                }
                break;
        }
    }

    private void setupTabs() {
        tabSafety.setOnClickListener(v -> selectTab("safety"));
        tabGps.setOnClickListener(v -> selectTab("gps"));
        tabCommute.setOnClickListener(v -> selectTab("commute"));
        tabAlerts.setOnClickListener(v -> selectTab("alerts"));
        tabPacking.setOnClickListener(v -> selectTab("packing"));
    }

    private void selectTab(String tabKey) {
        layoutTabSafety.setVisibility(View.GONE);
        layoutTabGps.setVisibility(View.GONE);
        layoutTabCommute.setVisibility(View.GONE);
        layoutTabAlerts.setVisibility(View.GONE);
        layoutTabPacking.setVisibility(View.GONE);

        resetChipStyle(tabSafety);
        resetChipStyle(tabGps);
        resetChipStyle(tabCommute);
        resetChipStyle(tabAlerts);
        resetChipStyle(tabPacking);

        switch (tabKey) {
            case "gps":
                layoutTabGps.setVisibility(View.VISIBLE);
                highlightChip(tabGps);
                break;
            case "commute":
                layoutTabCommute.setVisibility(View.VISIBLE);
                highlightChip(tabCommute);
                break;
            case "alerts":
                layoutTabAlerts.setVisibility(View.VISIBLE);
                highlightChip(tabAlerts);
                break;
            case "packing":
                layoutTabPacking.setVisibility(View.VISIBLE);
                highlightChip(tabPacking);
                break;
            case "safety":
            default:
                layoutTabSafety.setVisibility(View.VISIBLE);
                highlightChip(tabSafety);
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

    private void setupRoadsideStops() {
        allStops = new ArrayList<>();
        allStops.add(new RoadsideStop(
                "p1", "FUEL", "Fuel & EV Superhub",
                "Shell Select & 150kW Ultra EV Superhub",
                "850 m", "3 min",
                "🟢 Open • 4 Fast EV Chargers Free",
                "+91-124-4289901", "Expressway Corridor Exit 7B",
                "150kW Fast EV Plug • Nitrogen Tyre Gauge • Restrooms • Costa Coffee"
        ));
        allStops.add(new RoadsideStop(
                "p2", "TYRE", "Tyre & Puncture Care",
                "Expressway 24/7 Tyre & Mobile Puncture Care",
                "1.4 km", "4 min",
                "🟢 Open • Mobile Rescue Van Ready",
                "+91-98112-33445", "Service Road KM 4, Near Flyover Pillar 88",
                "24/7 Wheel Alignment • Mobile Puncture Van • Nitrogen Refill"
        ));
        allStops.add(new RoadsideStop(
                "p3", "CAFE", "Commuter Haven & Cafe",
                "Highway Commuter Haven & Chai Cafe",
                "1.1 km", "3 min",
                "🟢 Open • Dry Covered Parking",
                "+91-124-4112233", "Sector 43 Boulevard Plaza",
                "Covered Parking • Hot Ginger Chai • Free High-Speed Wi-Fi"
        ));
        allStops.add(new RoadsideStop(
                "p4", "PHARMACY", "24x7 Pharmacy",
                "Apollo 24/7 Drive-Thru Chemist",
                "600 m", "2 min",
                "🟢 Open 24 Hours • Pharmacist On-Site",
                "+91-124-4900024", "Galleria Market Boulevard, Block A",
                "First-Aid Kits • ORS & Cold Packs • Drive-Thru Window"
        ));
        allStops.add(new RoadsideStop(
                "p5", "POLICE", "Highway Police & Tow",
                "Cyber Expressway Traffic Police Post",
                "1.2 km", "4 min",
                "🚔 Patrol Active • Tow Unit On Standby",
                "112", "Cyber City Entry Roundabout",
                "Emergency Tow Truck • Accident First Aid • Live Route Advisory"
        ));
        allStops.add(new RoadsideStop(
                "p6", "ATM", "ATM & Cash Point",
                "SBI & HDFC 24/7 Concourse ATM",
                "450 m", "2 min",
                "🟢 Cash Available • Fast Queue",
                "1800-425-3800", "Sector 54 Metro Station Lower Deck",
                "Cash Dispenser • Dry AC Shelter • 24/7 Security Guard"
        ));
        allStops.add(new RoadsideStop(
                "p7", "CAFE", "Kiosk & Umbrella Stall",
                "Metro Corner Umbrella & Rain Gear Kiosk",
                "280 m", "1 min walk",
                "🟢 Open Now • Umbrellas & Raincoats in Stock",
                "+91-98112-99011", "Gate 2, Rapid Metro Concourse",
                "Windproof Umbrellas • Emergency Ponchos • Waterproof Mobile Pouches"
        ));

        rvRoadsideStops.setLayoutManager(new LinearLayoutManager(this));
        filterStops("ALL");

        if (filterAll != null) filterAll.setOnClickListener(v -> filterStops("ALL"));
        if (filterFuelEv != null) filterFuelEv.setOnClickListener(v -> filterStops("FUEL"));
        if (filterTyre != null) filterTyre.setOnClickListener(v -> filterStops("TYRE"));
        if (filterCafe != null) filterCafe.setOnClickListener(v -> filterStops("CAFE"));
        if (filterPharmacy != null) filterPharmacy.setOnClickListener(v -> filterStops("PHARMACY"));
        if (filterPolice != null) filterPolice.setOnClickListener(v -> filterStops("POLICE"));
        if (filterAtm != null) filterAtm.setOnClickListener(v -> filterStops("ATM"));
    }

    private void filterStops(String category) {
        if ("ALL".equalsIgnoreCase(category)) {
            rvRoadsideStops.setAdapter(new RoadsideStopAdapter(allStops));
        } else {
            List<RoadsideStop> filtered = new ArrayList<>();
            for (RoadsideStop s : allStops) {
                if (s.getCategory().equalsIgnoreCase(category)) {
                    filtered.add(s);
                }
            }
            rvRoadsideStops.setAdapter(new RoadsideStopAdapter(filtered));
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnRefresh.setOnClickListener(v -> loadWeatherData());
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
                    populateTravelData(response.body().current);
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

    private void populateTravelData(WeatherResponse.Current c) {
        tvCrosswinds.setText(String.format(Locale.getDefault(), "%.1f km/h", c.windSpeed10m));

        boolean isRain = c.precipitation > 0 || c.weatherCode >= 51;
        boolean isFog = c.weatherCode == 45 || c.weatherCode == 48;

        if (cbRainGear != null) cbRainGear.setChecked(isRain);

        updateBestTimeRecommendation(c);

        if (isFog) {
            tvRoadStatusBadge.setText("⚠️ FOGGY CONDITIONS");
            tvRoadStatusBadge.setBackgroundResource(R.drawable.pill_badge_yellow);
            tvRoadSafetyTitle.setText("Reduced Highway Visibility");
            tvRoadSafetyDesc.setText("Dense fog patches on morning transit routes. Keep fog lamps on and double vehicle following distance.");
            tvVisibility.setText("1.5 km");
            tvBrakingDistance.setText("1.4x (Caution)");
            tvFogHazard.setText("Moderate Fog");
            tvFogHazard.setTextColor(ContextCompat.getColor(this, R.color.status_warning));

            tvCommuteScore.setText("74 / 100");
            tvCommuteScoreBadge.setText("FOG DELAYS");
            tvCommuteScoreBadge.setBackgroundResource(R.drawable.pill_badge_yellow);
            tvCommuteDesc.setText("Dense fog patches on highway corridors. Maintain low speeds and use fog lights.");

            tvMorningCommuteDetails.setText("🌫️ Low Visibility: Fog lights recommended until 09:00 AM.");
            tvEveningCommuteDetails.setText("Clearer evening transit as ground fog lifts.");

            tvBikeSafetyBadge.setText("Caution (Fog)");
            tvBikeAdvice.setText("Wipe helmet visor frequently. Use high-visibility reflective jacket in early hours.");
        } else if (isRain) {
            tvRoadStatusBadge.setText("🌧️ WET ROADS");
            tvRoadStatusBadge.setBackgroundResource(R.drawable.pill_badge_yellow);
            tvRoadSafetyTitle.setText("Wet Road Hazard & Hydroplaning Risk");
            tvRoadSafetyDesc.setText("Active rain on highways. Reduce driving speed by 20 km/h and maintain gentle braking.");
            tvVisibility.setText("5.0 km");
            tvBrakingDistance.setText("1.6x (Slippery)");
            tvBrakingDistance.setTextColor(ContextCompat.getColor(this, R.color.status_danger));
            tvFogHazard.setText("Spray Mist");

            tvCommuteScore.setText("62 / 100");
            tvCommuteScoreBadge.setText("RAIN DELAYS");
            tvCommuteScoreBadge.setBackgroundResource(R.drawable.pill_badge_yellow);
            tvCommuteDesc.setText("Wet asphalt and reduced speeds across flyovers. Allow 15-20 min transit buffer.");

            tvMorningCommuteDetails.setText("🌧️ Active Rain: Carry an umbrella or rain poncho. Extra traffic on arterial roads.");
            tvEveningCommuteDetails.setText("🌧️ Wet Corridors: Drive with dipped headlights. Slow traffic near underpasses.");

            tvBikeSafetyBadge.setText("Caution (Wet)");
            tvBikeAdvice.setText("Wear waterproof rain suit and anti-fog visor. Watch out for slick road markings and metal drain covers.");
        } else {
            tvRoadStatusBadge.setText("CLEAR & DRY");
            tvRoadStatusBadge.setBackgroundResource(R.drawable.pill_badge_green);
            tvRoadSafetyTitle.setText("Optimal Driving Conditions");
            tvRoadSafetyDesc.setText("Dry pavement, clear highway sightlines (>10 km), and normal stopping distance.");
            tvVisibility.setText("10.0 km");
            tvBrakingDistance.setText("1.0x (Normal)");
            tvBrakingDistance.setTextColor(ContextCompat.getColor(this, R.color.status_success));
            tvFogHazard.setText("None");
            tvFogHazard.setTextColor(ContextCompat.getColor(this, R.color.status_success));

            tvCommuteScore.setText("96 / 100");
            tvCommuteScoreBadge.setText("SMOOTH COMMUTE");
            tvCommuteScoreBadge.setBackgroundResource(R.drawable.pill_badge_green);
            tvCommuteDesc.setText("Dry roads, clear traffic visibility, and zero precipitation disruption expected.");

            tvMorningCommuteDetails.setText("Dry corridors. No rain buffer needed; standard travel duration.");
            tvEveningCommuteDetails.setText("Pleasant breeze. No weather slowdown on arterial flyovers.");

            tvBikeSafetyBadge.setText("Dry Roads");
            tvBikeAdvice.setText("Zero road waterlogging. Visor fogging unlikely. Standard helmet and jacket sufficient.");
        }
    }

    private void populateFallbackData() {
        tvRoadStatusBadge.setText("CLEAR & DRY");
        tvRoadStatusBadge.setBackgroundResource(R.drawable.pill_badge_green);
        tvRoadSafetyTitle.setText("Optimal Driving Conditions");
        tvRoadSafetyDesc.setText("Dry asphalt, clear sightlines, and safe driving speeds across all transit corridors.");
        tvVisibility.setText("10.0 km");
        tvBrakingDistance.setText("1.0x (Normal)");
        tvCrosswinds.setText("12.0 km/h");
        tvFogHazard.setText("None");

        tvCommuteScore.setText("95 / 100");
        tvCommuteScoreBadge.setText("SMOOTH COMMUTE");
        tvCommuteScoreBadge.setBackgroundResource(R.drawable.pill_badge_green);
        tvCommuteDesc.setText("Dry roads and clear weather across city transit network.");
        tvMorningCommuteDetails.setText("Normal morning traffic conditions.");
        tvEveningCommuteDetails.setText("Smooth evening travel expected.");
        tvBikeSafetyBadge.setText("Dry Roads");
        tvBikeAdvice.setText("Ideal conditions for two-wheeler commute.");

        updateBestTimeRecommendation(null);
    }
}
