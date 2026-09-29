package com.example.event_planner;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.farmers.R;
import com.example.farmers.util.LocaleHelper;
import com.example.farmers.util.PrefsManager;

/**
 * ProfileActivity manages user preferences for the MAUSAM Event Planner,
 * including default location, preferred event type, metric units, language, and alert notifications.
 */
public class ProfileActivity extends AppCompatActivity {

    private ImageButton btnProfileBack;
    private EditText etProfileName;
    private EditText etProfileLocation;
    private Spinner spProfileEventType;
    private RadioGroup rgTempUnit;
    private RadioGroup rgWindUnit;
    private RadioGroup rgProfileLanguage;
    private RadioButton rbProfileEn;
    private RadioButton rbProfileHi;
    private RadioButton rbProfileMr;
    private RadioButton rbCelsius;
    private RadioButton rbFahrenheit;
    private RadioButton rbKmh;
    private RadioButton rbMps;
    private SwitchCompat swWeatherAlerts;
    private SwitchCompat swRainAlerts;
    private SwitchCompat swSevereAlerts;
    private Button btnSaveProfile;

    private PrefsManager prefs;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        prefs = PrefsManager.getInstance(this);

        // Bind Views
        btnProfileBack = findViewById(R.id.btnProfileBack);
        etProfileName = findViewById(R.id.etProfileName);
        etProfileLocation = findViewById(R.id.etProfileLocation);
        spProfileEventType = findViewById(R.id.spProfileEventType);
        rgTempUnit = findViewById(R.id.rgTempUnit);
        rgWindUnit = findViewById(R.id.rgWindUnit);
        rgProfileLanguage = findViewById(R.id.rgProfileLanguage);
        rbProfileEn = findViewById(R.id.rbProfileEn);
        rbProfileHi = findViewById(R.id.rbProfileHi);
        rbProfileMr = findViewById(R.id.rbProfileMr);
        rbCelsius = findViewById(R.id.rbCelsius);
        rbFahrenheit = findViewById(R.id.rbFahrenheit);
        rbKmh = findViewById(R.id.rbKmh);
        rbMps = findViewById(R.id.rbMps);
        swWeatherAlerts = findViewById(R.id.swWeatherAlerts);
        swRainAlerts = findViewById(R.id.swRainAlerts);
        swSevereAlerts = findViewById(R.id.swSevereAlerts);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        // Setup Event Type Spinner
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
        spProfileEventType.setAdapter(adapter);

        // Pre-fill existing preferences
        etProfileName.setText(prefs.getUserName());
        etProfileLocation.setText(prefs.getLocationName());

        String savedEventType = prefs.getEventType();
        for (int i = 0; i < eventTypes.length; i++) {
            if (eventTypes[i].equalsIgnoreCase(savedEventType)) {
                spProfileEventType.setSelection(i);
                break;
            }
        }

        if (prefs.useCelsius()) {
            rbCelsius.setChecked(true);
        } else {
            rbFahrenheit.setChecked(true);
        }

        if (prefs.useKmh()) {
            rbKmh.setChecked(true);
        } else {
            rbMps.setChecked(true);
        }

        String lang = prefs.getLanguage();
        if (LocaleHelper.LANG_HINDI.equalsIgnoreCase(lang)) {
            rbProfileHi.setChecked(true);
        } else if (LocaleHelper.LANG_MARATHI.equalsIgnoreCase(lang)) {
            rbProfileMr.setChecked(true);
        } else {
            rbProfileEn.setChecked(true);
        }

        swWeatherAlerts.setChecked(prefs.alertsEnabled());
        swRainAlerts.setChecked(prefs.alertsEnabled());
        swSevereAlerts.setChecked(prefs.alertsEnabled());

        btnProfileBack.setOnClickListener(v -> finish());
        btnSaveProfile.setOnClickListener(v -> savePreferences());
    }

    private void savePreferences() {
        String name = etProfileName.getText().toString().trim();
        String location = etProfileLocation.getText().toString().trim();
        String eventType = spProfileEventType.getSelectedItem().toString();

        if (name.isEmpty()) {
            etProfileName.setError(getString(R.string.name_empty_error));
            etProfileName.requestFocus();
            return;
        }

        if (location.isEmpty()) {
            etProfileLocation.setError(getString(R.string.location_empty_error));
            etProfileLocation.requestFocus();
            return;
        }

        prefs.setUserName(name);
        prefs.setEventType(eventType);
        prefs.setLocation(prefs.getLatitude(), prefs.getLongitude(), location);
        prefs.setUseCelsius(rbCelsius.isChecked());
        prefs.setUseKmh(rbKmh.isChecked());
        prefs.setAlertsEnabled(swWeatherAlerts.isChecked());

        // Language check
        String chosenLang = LocaleHelper.LANG_ENGLISH;
        if (rbProfileHi.isChecked()) {
            chosenLang = LocaleHelper.LANG_HINDI;
        } else if (rbProfileMr.isChecked()) {
            chosenLang = LocaleHelper.LANG_MARATHI;
        }

        boolean languageChanged = !chosenLang.equalsIgnoreCase(prefs.getLanguage());
        if (languageChanged) {
            LocaleHelper.setLocale(this, chosenLang);
        }

        String message = getString(R.string.save_settings) + ": " + name + " (" + location + ")";
        Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
        finish();
    }
}
