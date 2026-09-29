package com.example.farmers;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.farmers.worker.CropReminderWorker;
import com.example.farmers.worker.WeatherAlertWorker;

import java.util.concurrent.TimeUnit;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Schedule periodic background workers
        scheduleWorkers();

        // Always navigate from Splash -> Login Page
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, 1500);
    }

    private void scheduleWorkers() {
        try {
            WorkManager workManager = WorkManager.getInstance(this);

            // Weather alert worker runs every 3 hours
            PeriodicWorkRequest weatherWork = new PeriodicWorkRequest.Builder(
                    WeatherAlertWorker.class, 3, TimeUnit.HOURS)
                    .build();
            workManager.enqueueUniquePeriodicWork(
                    "WeatherAlertWorker",
                    ExistingPeriodicWorkPolicy.KEEP,
                    weatherWork
            );

            // Daily crop reminder worker runs every 24 hours
            PeriodicWorkRequest cropWork = new PeriodicWorkRequest.Builder(
                    CropReminderWorker.class, 24, TimeUnit.HOURS)
                    .build();
            workManager.enqueueUniquePeriodicWork(
                    "CropReminderWorker",
                    ExistingPeriodicWorkPolicy.KEEP,
                    cropWork
            );
        } catch (Exception ignored) {}
    }
}
