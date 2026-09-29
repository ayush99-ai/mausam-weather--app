package com.example.event_planner;

import android.content.Context;
import android.content.Intent;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.farmers.R;
import com.example.farmers.util.LocaleHelper;

import java.util.Calendar;
import java.util.List;

/**
 * EventPlannerActivity is the primary weather intelligence dashboard for outdoor gathering planning.
 *
 * It fulfills the Smart India Hackathon (SIH) requirement:
 * "Event planners Offer extended forecasts, probability of rain, and 'comfort index' for outdoor gatherings or weddings."
 */
public class EventPlannerActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    // Header & Navigation
    private ImageButton btnBack;
    private ImageButton btnMenu;
    private TextView tvSwitchSection;
    private TextView tvHeaderLocation;
    private ScrollView svDashboard;

    // Bottom Navigation Views
    private LinearLayout navHome;
    private LinearLayout navBestTime;
    private LinearLayout navForecast;

    // Event Details Input
    private EditText etEventName;
    private Spinner spEventType;
    private TextView tvEventTypeContext;
    private EditText etLocation;
    private Button btnDate;
    private Button btnTime;

    // Hero Window Card
    private TextView tvHeroBadge;
    private TextView tvHeroWindowTime;
    private TextView tvHeroScore;
    private TextView tvHeroRecommendation;

    // Weather Metrics
    private ImageView ivWeatherIcon;
    private TextView tvWeather;
    private TextView tvTemperature;
    private TextView tvRain;
    private TextView tvHumidity;
    private TextView tvWind;

    // Rain Probability Section
    private TextView tvRainLarge;
    private TextView tvRainRiskClassification;
    private TextView tvRainSubtitle;
    private ProgressBar pbRainProbability;

    // Outdoor Comfort Index Section
    private TextView tvComfortScore;
    private TextView tvComfortStatus;
    private TextView tvComfortDesc;

    // Event Suitability Section
    private TextView tvEventSuitability;
    private TextView tvSuitabilityExplanation;

    // Extended Forecast Section
    private View sectionExtendedForecast;
    private LinearLayout llForecastContainer;

    // Best Time Window Section
    private View sectionBestTime;
    private TextView tvPrimeWindowBanner;
    private LinearLayout llHourlySlotsContainer;
    private TextView tvBestTimeReason;

    // Daylight & Solar
    private TextView tvSunrise;
    private TextView tvSunset;
    private TextView tvGoldenHour;
    private TextView tvWindGuidance;

    // Dynamic Weather Advice & Alerts
    private TextView tvSuggestion;
    private TextView tvEventAlerts;

    // Wedding-Specific Section
    private LinearLayout llWeddingTips;

    // Weather-based Checklist
    private CheckBox cbRainProtection;
    private CheckBox cbDrinkingWater;
    private CheckBox cbShadeArrangement;
    private CheckBox cbSecureDecorations;
    private CheckBox cbSeatingProtection;
    private CheckBox cbLightingProtection;
    private CheckBox cbIndoorBackup;

    // Event Summary Views
    private TextView tvSummaryName;
    private TextView tvSummaryType;
    private TextView tvSummaryLocation;
    private TextView tvSummaryDateTime;
    private TextView tvSummaryWeather;
    private TextView tvSummaryRain;
    private TextView tvSummaryComfort;
    private TextView tvSummarySuitability;
    private Button btnPlanEvent;

    // State Variables
    private String selectedDate = "";
    private String selectedTime = "";
    private WeatherData currentWeatherData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_planner);

        // 1. Initialize Views from XML
        initViews();

        // 2. Setup Event Type Spinner and context listeners
        setupEventTypeSpinner();

        // 3. Setup Date and Time Pickers
        setupDateTimePickers();

        // 4. Setup Navigation Listeners
        setupNavigation();

        // 5. Setup Action Listeners
        setupActionListeners();

        // 6. Initialize Weather Data and compute intelligence metrics
        loadWeatherData();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnMenu = findViewById(R.id.btnMenu);
        tvSwitchSection = findViewById(R.id.tvSwitchSection);
        tvHeaderLocation = findViewById(R.id.tvHeaderLocation);
        svDashboard = findViewById(R.id.svDashboard);

        navHome = findViewById(R.id.navHome);
        navBestTime = findViewById(R.id.navBestTime);
        navForecast = findViewById(R.id.navForecast);

        etEventName = findViewById(R.id.etEventName);
        spEventType = findViewById(R.id.spEventType);
        tvEventTypeContext = findViewById(R.id.tvEventTypeContext);
        etLocation = findViewById(R.id.etLocation);
        btnDate = findViewById(R.id.btnDate);
        btnTime = findViewById(R.id.btnTime);

        tvHeroBadge = findViewById(R.id.tvHeroBadge);
        tvHeroWindowTime = findViewById(R.id.tvHeroWindowTime);
        tvHeroScore = findViewById(R.id.tvHeroScore);
        tvHeroRecommendation = findViewById(R.id.tvHeroRecommendation);

        ivWeatherIcon = findViewById(R.id.ivWeatherIcon);
        tvWeather = findViewById(R.id.tvWeather);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvRain = findViewById(R.id.tvRain);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvWind = findViewById(R.id.tvWind);

        tvRainLarge = findViewById(R.id.tvRainLarge);
        tvRainRiskClassification = findViewById(R.id.tvRainRiskClassification);
        tvRainSubtitle = findViewById(R.id.tvRainSubtitle);
        pbRainProbability = findViewById(R.id.pbRainProbability);

        tvComfortScore = findViewById(R.id.tvComfortScore);
        tvComfortStatus = findViewById(R.id.tvComfortStatus);
        tvComfortDesc = findViewById(R.id.tvComfortDesc);

        tvEventSuitability = findViewById(R.id.tvEventSuitability);
        tvSuitabilityExplanation = findViewById(R.id.tvSuitabilityExplanation);

        sectionExtendedForecast = findViewById(R.id.sectionExtendedForecast);
        llForecastContainer = findViewById(R.id.llForecastContainer);

        sectionBestTime = findViewById(R.id.sectionBestTime);
        tvPrimeWindowBanner = findViewById(R.id.tvPrimeWindowBanner);
        llHourlySlotsContainer = findViewById(R.id.llHourlySlotsContainer);
        tvBestTimeReason = findViewById(R.id.tvBestTimeReason);

        tvSunrise = findViewById(R.id.tvSunrise);
        tvSunset = findViewById(R.id.tvSunset);
        tvGoldenHour = findViewById(R.id.tvGoldenHour);
        tvWindGuidance = findViewById(R.id.tvWindGuidance);

        tvSuggestion = findViewById(R.id.tvSuggestion);
        tvEventAlerts = findViewById(R.id.tvEventAlerts);
        llWeddingTips = findViewById(R.id.llWeddingTips);

        cbRainProtection = findViewById(R.id.cbRainProtection);
        cbDrinkingWater = findViewById(R.id.cbDrinkingWater);
        cbShadeArrangement = findViewById(R.id.cbShadeArrangement);
        cbSecureDecorations = findViewById(R.id.cbSecureDecorations);
        cbSeatingProtection = findViewById(R.id.cbSeatingProtection);
        cbLightingProtection = findViewById(R.id.cbLightingProtection);
        cbIndoorBackup = findViewById(R.id.cbIndoorBackup);

        tvSummaryName = findViewById(R.id.tvSummaryName);
        tvSummaryType = findViewById(R.id.tvSummaryType);
        tvSummaryLocation = findViewById(R.id.tvSummaryLocation);
        tvSummaryDateTime = findViewById(R.id.tvSummaryDateTime);
        tvSummaryWeather = findViewById(R.id.tvSummaryWeather);
        tvSummaryRain = findViewById(R.id.tvSummaryRain);
        tvSummaryComfort = findViewById(R.id.tvSummaryComfort);
        tvSummarySuitability = findViewById(R.id.tvSummarySuitability);
        btnPlanEvent = findViewById(R.id.btnPlanEvent);
    }

    private void setupEventTypeSpinner() {
        String[] eventTypes = {
                "Wedding",
                "Birthday",
                "College Event",
                "Sports Event",
                "Outdoor Party",
                "Picnic",
                "Corporate Event",
                "Festival",
                "Other"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                eventTypes
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEventType.setAdapter(adapter);

        spEventType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = parent.getItemAtPosition(position).toString();
                updateEventTypeContext(selectedType);
                updateEventSummary();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Updates contextual guidance and shows/hides wedding-specific tips dynamically.
     */
    private void updateEventTypeContext(String eventType) {
        if ("Wedding".equalsIgnoreCase(eventType)) {
            tvEventTypeContext.setText("💍 Outdoor wedding planning");
            llWeddingTips.setVisibility(View.VISIBLE);
        } else if ("College Event".equalsIgnoreCase(eventType)) {
            tvEventTypeContext.setText("🎓 Weather-aware campus event planning");
            llWeddingTips.setVisibility(View.GONE);
        } else if ("Sports Event".equalsIgnoreCase(eventType)) {
            tvEventTypeContext.setText("🏆 Weather-aware sports planning");
            llWeddingTips.setVisibility(View.GONE);
        } else if ("Outdoor Party".equalsIgnoreCase(eventType)) {
            tvEventTypeContext.setText("🎉 Outdoor gathering planning");
            llWeddingTips.setVisibility(View.GONE);
        } else if ("Picnic".equalsIgnoreCase(eventType)) {
            tvEventTypeContext.setText("🧺 Weather-aware picnic planning");
            llWeddingTips.setVisibility(View.GONE);
        } else if ("Corporate Event".equalsIgnoreCase(eventType)) {
            tvEventTypeContext.setText("💼 Outdoor corporate event planning");
            llWeddingTips.setVisibility(View.GONE);
        } else {
            tvEventTypeContext.setText("✨ Weather-aware event planning");
            llWeddingTips.setVisibility(View.GONE);
        }
    }

    private void setupDateTimePickers() {
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });

        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimePicker();
            }
        });

        etEventName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateEventSummary();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        etLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String loc = s.toString().trim();
                if (!loc.isEmpty()) {
                    tvHeaderLocation.setText(loc + " ▼");
                }
                updateEventSummary();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupNavigation() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfile();
            }
        });

        tvSwitchSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                svDashboard.smoothScrollTo(0, 0);
            }
        });

        navBestTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sectionBestTime.getParent().requestChildFocus(sectionBestTime, sectionBestTime);
            }
        });

        navForecast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sectionExtendedForecast.getParent().requestChildFocus(sectionExtendedForecast, sectionExtendedForecast);
            }
        });
    }

    private void openProfile() {
        Intent intent = new Intent(EventPlannerActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    private void setupActionListeners() {
        btnPlanEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndPlanEvent();
            }
        });
    }

    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                EventPlannerActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        btnDate.setText(selectedDate);
                        updateEventSummary();
                    }
                },
                year,
                month,
                day
        );
        datePickerDialog.show();
    }

    private void openTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                EventPlannerActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                        String amPm = (selectedHour >= 12) ? "PM" : "AM";
                        int displayHour = selectedHour % 12;
                        if (displayHour == 0) displayHour = 12;

                        selectedTime = String.format("%02d:%02d %s", displayHour, selectedMinute, amPm);
                        btnTime.setText(selectedTime);
                        updateEventSummary();
                    }
                },
                hour,
                minute,
                false
        );
        timePickerDialog.show();
    }

    // =========================================================================
    // WEATHER DATA & SIH DECISION ALGORITHMS
    // =========================================================================

    /**
     * Loads weather data and initiates all meteorology intelligence methods.
     *
     * // TODO: Replace mock weather data with API response.
     * // Here you can call Retrofit or Volley to query the current weather
     * // and extended 7-day forecast from Open-Meteo or IMD API.
     */
    private void loadWeatherData() {
        // Mock current weather object
        currentWeatherData = new WeatherData(28, 20, 62, 12, "Partly Cloudy", "Today", "18:00");

        // 1. Update Core Weather UI
        updateWeatherUI();

        // 2. Populate 7-day Extended Forecast Cards
        populateExtendedForecast();

        // 3. Populate Hourly Best Time Timeline
        populateHourlyTimeline();

        // 4. Update Event Summary Card
        updateEventSummary();
    }

    /**
     * Updates the UI elements based on current weather data and computed indices.
     */
    public void updateWeatherUI() {
        if (currentWeatherData == null) return;

        int temp = currentWeatherData.getTemperature();
        int rain = currentWeatherData.getRainProbability();
        int humidity = currentWeatherData.getHumidity();
        int wind = currentWeatherData.getWindSpeed();

        tvWeather.setText(currentWeatherData.getCondition());
        tvTemperature.setText(temp + "°C");
        tvRain.setText(rain + "%");
        tvHumidity.setText(humidity + "%");
        tvWind.setText(wind + " km/h");

        // Rain Probability Section
        tvRainLarge.setText(rain + "%");
        pbRainProbability.setProgress(rain);
        if (rain <= 30) {
            tvRainRiskClassification.setText("Low Rain Risk");
            tvRainRiskClassification.setTextColor(ContextCompat.getColor(this, R.color.status_success));
            tvRainSubtitle.setText("Low chance of rainfall; safe for open lawn seating.");
        } else if (rain <= 60) {
            tvRainRiskClassification.setText("Moderate Rain Risk");
            tvRainRiskClassification.setTextColor(ContextCompat.getColor(this, R.color.status_warning));
            tvRainSubtitle.setText("Moderate chance of showers; keep waterproof tents ready.");
        } else {
            tvRainRiskClassification.setText("High Rain Risk");
            tvRainRiskClassification.setTextColor(ContextCompat.getColor(this, R.color.status_danger));
            tvRainSubtitle.setText("High chance of rain; consider indoor hall or covered backup.");
        }

        // Outdoor Comfort Index Calculation
        int comfortScore = calculateComfortIndex(temp, rain, humidity, wind);
        tvComfortScore.setText(comfortScore + " / 100");

        String comfortStatus;
        if (comfortScore >= 80) {
            comfortStatus = "EXCELLENT";
            tvComfortStatus.setTextColor(ContextCompat.getColor(this, R.color.status_success));
            tvComfortStatus.setBackgroundResource(R.drawable.pill_badge_green);
            tvComfortDesc.setText("Very suitable for outdoor events. Highly comfortable for guests.");
        } else if (comfortScore >= 60) {
            comfortStatus = "GOOD";
            tvComfortStatus.setTextColor(ContextCompat.getColor(this, R.color.status_success));
            tvComfortStatus.setBackgroundResource(R.drawable.pill_badge_green);
            tvComfortDesc.setText("Suitable for outdoor events. Mild conditions throughout the program.");
        } else if (comfortScore >= 40) {
            comfortStatus = "MODERATE";
            tvComfortStatus.setTextColor(ContextCompat.getColor(this, R.color.status_warning));
            tvComfortStatus.setBackgroundResource(R.drawable.pill_badge_yellow);
            tvComfortDesc.setText("Moderate comfort. Plan with cooling, shade and hydration.");
        } else {
            comfortStatus = "POOR";
            tvComfortStatus.setTextColor(ContextCompat.getColor(this, R.color.status_danger));
            tvComfortStatus.setBackgroundResource(R.drawable.pill_badge_red);
            tvComfortDesc.setText("Unfavorable weather conditions. Strong indoor alternative recommended.");
        }
        tvComfortStatus.setText(comfortStatus);

        // Event Suitability Calculation
        String suitability = calculateEventSuitability(comfortScore, rain);
        tvEventSuitability.setText(suitability);
        if (suitability.contains("EXCELLENT") || suitability.contains("GOOD")) {
            tvEventSuitability.setTextColor(ContextCompat.getColor(this, R.color.status_success));
            tvEventSuitability.setBackgroundResource(R.drawable.pill_badge_green);
            tvSuitabilityExplanation.setText("Outdoor gatherings, stage ceremonies, and open lawn seating can proceed smoothly.");
        } else if (suitability.contains("MODERATE")) {
            tvEventSuitability.setTextColor(ContextCompat.getColor(this, R.color.status_warning));
            tvEventSuitability.setBackgroundResource(R.drawable.pill_badge_yellow);
            tvSuitabilityExplanation.setText("Outdoor events are feasible with proactive precautions (canopies, water stations, fans).");
        } else {
            tvEventSuitability.setTextColor(ContextCompat.getColor(this, R.color.status_danger));
            tvEventSuitability.setBackgroundResource(R.drawable.pill_badge_red);
            tvSuitabilityExplanation.setText("Weather risk is elevated. Shifting to an indoor hall or covered venue is strongly recommended.");
        }

        // Dynamic Weather Advice
        String advice = generateWeatherAdvice(rain, wind, temp, humidity);
        tvSuggestion.setText(advice);

        // Dynamic Event Alerts
        String alert = generateEventAlerts(rain, wind, temp);
        tvEventAlerts.setText(alert);
        if (alert.contains("No major")) {
            tvEventAlerts.setTextColor(ContextCompat.getColor(this, R.color.status_success));
            tvEventAlerts.setBackgroundResource(R.drawable.pill_badge_green);
        } else {
            tvEventAlerts.setTextColor(ContextCompat.getColor(this, R.color.status_danger));
            tvEventAlerts.setBackgroundResource(R.drawable.pill_badge_red);
        }

        // Best Time Window
        findBestTimeWindow();

        // Daylight & Solar
        tvSunrise.setText(currentWeatherData.getSunrise());
        tvSunset.setText(currentWeatherData.getSunset());
        tvGoldenHour.setText(currentWeatherData.getGoldenHour());
        tvWindGuidance.setText("💨 Wind " + wind + " km/h: Safe for standard canopies, mandaps and backdrop structures.");
    }

    /**
     * Calculates the outdoor comfort index (0-100) based on meteorology factors.
     * Required by SIH problem statement.
     */
    public int calculateComfortIndex(int temp, int rainProbability, int humidity, int windSpeed) {
        int score = 100;

        // Rain deduction
        if (rainProbability > 60) {
            score -= 30;
        } else if (rainProbability > 30) {
            score -= 15;
        }

        // Humidity deduction
        if (humidity > 80) {
            score -= 10;
        }

        // Wind deduction
        if (windSpeed > 30) {
            score -= 15;
        }

        // Temperature deduction
        if (temp > 35) {
            score -= 15;
        } else if (temp < 15) {
            score -= 10;
        }

        // Enforce 0 - 100 boundaries
        if (score < 0) score = 0;
        if (score > 100) score = 100;

        return score;
    }

    /**
     * Evaluates outdoor event suitability based on calculated comfort index and rain risk.
     * Required by SIH problem statement.
     */
    public String calculateEventSuitability(int comfortScore, int rainProbability) {
        if (comfortScore >= 80 && rainProbability <= 30) {
            return "EXCELLENT FOR OUTDOOR EVENT";
        } else if (comfortScore >= 60 && rainProbability <= 50) {
            return "GOOD FOR OUTDOOR EVENT";
        } else if (comfortScore >= 40) {
            return "MODERATE – PLAN WITH PRECAUTIONS";
        } else {
            return "POOR – CONSIDER INDOOR ALTERNATIVE";
        }
    }

    /**
     * Generates actionable, context-aware weather advice for event planners.
     */
    public String generateWeatherAdvice(int rain, int wind, int temp, int humidity) {
        if (rain > 60) {
            return "High chance of rain. Arrange a covered area or indoor backup.";
        }
        if (wind > 30) {
            return "Strong winds may affect decorations and temporary structures.";
        }
        if (temp > 35) {
            return "High temperature expected. Provide shade, drinking water and cooling areas.";
        }
        if (humidity > 80) {
            return "High humidity may reduce outdoor comfort. Provide ventilation and shaded areas.";
        }
        return "Weather conditions are favorable for an outdoor event.";
    }

    /**
     * Evaluates weather hazards and generates proactive event alerts.
     */
    public String generateEventAlerts(int rain, int wind, int temp) {
        if (rain > 60) {
            return "⚠️ Rain Alert: Rain probability is high during the selected period. Prepare shelter.";
        }
        if (wind > 30) {
            return "⚠️ Wind Alert: Moderate or strong winds are expected. Secure stages and light poles.";
        }
        if (temp > 35) {
            return "⚠️ Heat Alert: High afternoon temperatures are expected. Ensure guest hydration.";
        }
        return "✓ No Major Alert: No major weather risks detected for your selected event.";
    }

    /**
     * Identifies the best time window for outdoor activities.
     */
    public void findBestTimeWindow() {
        tvPrimeWindowBanner.setText("● Prime window: 17:00 – 20:00 (94/100)");
        tvBestTimeReason.setText("Recommended: 6:00 PM – 8:00 PM. Lower temperature, calm wind conditions, and optimal golden-hour ambient lighting.");
    }

    /**
     * Populates the 7-day extended forecast cards into the scrollable dashboard container.
     */
    private void populateExtendedForecast() {
        llForecastContainer.removeAllViews();
        List<ForecastDay> forecastList = WeatherData.getExtendedForecast();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < forecastList.size(); i++) {
            ForecastDay day = forecastList.get(i);
            View cardView = inflater.inflate(R.layout.item_forecast_card, llForecastContainer, false);

            TextView tvDayName = cardView.findViewById(R.id.tvForecastDayName);
            TextView tvDate = cardView.findViewById(R.id.tvForecastDate);
            TextView tvCondition = cardView.findViewById(R.id.tvForecastCondition);
            TextView tvComfort = cardView.findViewById(R.id.tvForecastComfort);
            TextView tvBestWindow = cardView.findViewById(R.id.tvForecastBestWindow);
            TextView tvTempRange = cardView.findViewById(R.id.tvForecastTempRange);
            TextView tvRainVal = cardView.findViewById(R.id.tvForecastRain);
            TextView tvWindVal = cardView.findViewById(R.id.tvForecastWind);
            TextView tvRecommendation = cardView.findViewById(R.id.tvForecastRecommendation);
            ImageView ivIcon = cardView.findViewById(R.id.ivForecastIcon);

            tvDayName.setText(day.getDayName());
            tvDate.setText(day.getDate());
            tvCondition.setText(day.getCondition());
            tvComfort.setText(day.getComfortIndex() + " / 100");
            tvTempRange.setText(day.getLowTemp() + "°C – " + day.getHighTemp() + "°C");
            tvRainVal.setText("🌧 " + day.getRainProbability() + "% rain");
            tvWindVal.setText("💨 " + day.getWindSpeed() + " km/h");
            tvRecommendation.setText(day.getRecommendation());

            if (day.getCondition().toLowerCase().contains("rain")) {
                ivIcon.setImageResource(R.drawable.ic_rain);
                tvComfort.setTextColor(ContextCompat.getColor(this, R.color.status_warning));
                tvComfort.setBackgroundResource(R.drawable.pill_badge_yellow);
            } else if (day.getCondition().toLowerCase().contains("cloud")) {
                ivIcon.setImageResource(R.drawable.ic_cloud);
            } else {
                ivIcon.setImageResource(R.drawable.ic_sun);
            }

            llForecastContainer.addView(cardView);
        }
    }

    /**
     * Populates hourly timeline slots for the Best Time section.
     */
    private void populateHourlyTimeline() {
        llHourlySlotsContainer.removeAllViews();
        List<HourlySlot> slots = WeatherData.getHourlySlots();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < slots.size(); i++) {
            HourlySlot slot = slots.get(i);
            View slotView = inflater.inflate(R.layout.item_hourly_slot, llHourlySlotsContainer, false);

            TextView tvTime = slotView.findViewById(R.id.tvHourlyTime);
            TextView tvRating = slotView.findViewById(R.id.tvHourlyRating);
            TextView tvDescription = slotView.findViewById(R.id.tvHourlyDescription);
            TextView tvTemp = slotView.findViewById(R.id.tvHourlyTemp);
            TextView tvScore = slotView.findViewById(R.id.tvHourlyScore);

            tvTime.setText(slot.getTimeLabel());
            tvRating.setText("● " + slot.getRating());
            tvDescription.setText(slot.getDescription());
            tvTemp.setText(slot.getTemperature() + "°C");
            tvScore.setText(slot.getComfortScore() + " / 100");

            if (slot.isRecommended()) {
                slotView.setBackgroundResource(R.drawable.advice_background);
                tvRating.setBackgroundResource(R.drawable.pill_badge_green);
                tvRating.setTextColor(ContextCompat.getColor(this, R.color.status_success));
            }

            llHourlySlotsContainer.addView(slotView);
        }
    }

    /**
     * Updates the summary card with user inputs and current meteorological evaluation.
     */
    public void updateEventSummary() {
        String name = etEventName.getText().toString().trim();
        tvSummaryName.setText(name.isEmpty() ? "Not specified" : name);

        if (spEventType.getSelectedItem() != null) {
            tvSummaryType.setText(spEventType.getSelectedItem().toString());
        }

        String location = etLocation.getText().toString().trim();
        tvSummaryLocation.setText(location.isEmpty() ? "Nashik, Maharashtra" : location);

        if (!selectedDate.isEmpty() && !selectedTime.isEmpty()) {
            tvSummaryDateTime.setText(selectedDate + " at " + selectedTime);
        } else if (!selectedDate.isEmpty()) {
            tvSummaryDateTime.setText(selectedDate + " (Select time)");
        } else {
            tvSummaryDateTime.setText("Select Date & Time above");
        }

        if (currentWeatherData != null) {
            tvSummaryWeather.setText(currentWeatherData.getCondition() + ", " + currentWeatherData.getTemperature() + "°C");
            tvSummaryRain.setText(currentWeatherData.getRainProbability() + "% (" + tvRainRiskClassification.getText() + ")");
            tvSummaryComfort.setText(tvComfortScore.getText() + " (" + tvComfortStatus.getText() + ")");
            tvSummarySuitability.setText(tvEventSuitability.getText());
        }
    }

    /**
     * Validates event inputs and presents the final confirmation dialog.
     */
    public boolean validateEventDetails() {
        String eventName = etEventName.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        if (eventName.isEmpty()) {
            etEventName.setError("Please enter event name");
            etEventName.requestFocus();
            Toast.makeText(this, "Please enter event name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (location.isEmpty()) {
            etLocation.setError("Please enter event location");
            etLocation.requestFocus();
            Toast.makeText(this, "Please enter event location", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "Please select event date", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedTime.isEmpty()) {
            Toast.makeText(this, "Please select event time", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void validateAndPlanEvent() {
        if (!validateEventDetails()) {
            return;
        }

        String eventName = etEventName.getText().toString().trim();
        String eventType = spEventType.getSelectedItem().toString();
        String location = etLocation.getText().toString().trim();
        int comfort = calculateComfortIndex(
                currentWeatherData.getTemperature(),
                currentWeatherData.getRainProbability(),
                currentWeatherData.getHumidity(),
                currentWeatherData.getWindSpeed()
        );
        String suitability = calculateEventSuitability(comfort, currentWeatherData.getRainProbability());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Event Planned Successfully! 🎉");
        builder.setMessage(
                "Event planned successful with event summary (selected)\n\n" +
                        "• Event: " + eventName + "\n" +
                        "• Type: " + eventType + "\n" +
                        "• Location: " + location + "\n" +
                        "• Schedule: " + selectedDate + " at " + selectedTime + "\n" +
                        "• Weather: " + currentWeatherData.getCondition() + " (" + currentWeatherData.getTemperature() + "°C)\n" +
                        "• Rain Probability: " + currentWeatherData.getRainProbability() + "%\n" +
                        "• Outdoor Comfort Score: " + comfort + " / 100\n" +
                        "• Suitability: " + suitability
        );
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(EventPlannerActivity.this, "Event planned successful with event summary (selected)", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }
}
