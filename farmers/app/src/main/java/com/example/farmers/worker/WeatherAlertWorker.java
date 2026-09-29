package com.example.farmers.worker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.farmers.R;
import com.example.farmers.data.api.RetrofitClient;
import com.example.farmers.data.model.WeatherResponse;
import com.example.farmers.util.PrefsManager;

import retrofit2.Response;

public class WeatherAlertWorker extends Worker {

    public static final String CHANNEL_ID = "farmweather_alerts";
    public static final String CHANNEL_NAME = "Farm Weather Alerts";

    public WeatherAlertWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        PrefsManager prefs = PrefsManager.getInstance(context);

        if (!prefs.alertsEnabled()) {
            return Result.success();
        }

        double lat = prefs.getLatitude();
        double lon = prefs.getLongitude();

        try {
            Response<WeatherResponse> response = RetrofitClient.getWeatherService().getWeather(
                    lat,
                    lon,
                    "temperature_2m,precipitation,wind_speed_10m,wind_gusts_10m,weather_code",
                    null,
                    "precipitation_sum,wind_speed_10m_max,wind_gusts_10m_max",
                    "auto",
                    1
            ).execute();

            if (response.isSuccessful() && response.body() != null) {
                WeatherResponse weather = response.body();
                checkAndSendAlerts(context, prefs, weather);
                return Result.success();
            }
        } catch (Exception e) {
            return Result.retry();
        }

        return Result.success();
    }

    private void checkAndSendAlerts(Context context, PrefsManager prefs, WeatherResponse weather) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Severe weather and agricultural alerts");
            notificationManager.createNotificationChannel(channel);
        }

        // Check Rain
        float rainThreshold = prefs.getRainThreshold();
        double dailyRain = 0;
        if (weather.daily != null && weather.daily.precipitationSum != null && !weather.daily.precipitationSum.isEmpty()) {
            Double val = weather.daily.precipitationSum.get(0);
            if (val != null) dailyRain = val;
        } else if (weather.current != null) {
            dailyRain = weather.current.precipitation;
        }

        if (dailyRain >= rainThreshold && dailyRain > 0) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("🌧️ Heavy Rain Warning!")
                    .setContentText(String.format("Expected rainfall: %.1f mm (Threshold: %.1f mm). Protect unharvested crops & clear drainage.", dailyRain, rainThreshold))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true);
            notificationManager.notify(101, builder.build());
        }

        // Check Wind
        float windThreshold = prefs.getWindThreshold();
        double currentWind = weather.current != null ? weather.current.windSpeed10m : 0;
        double currentGusts = weather.current != null ? weather.current.windGusts10m : 0;
        double maxWind = Math.max(currentWind, currentGusts);

        if (maxWind >= windThreshold) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("💨 High Wind Warning!")
                    .setContentText(String.format("Wind gusts up to %.1f km/h detected (Threshold: %.1f km/h). Delay sprayings and reinforce shelters.", maxWind, windThreshold))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true);
            notificationManager.notify(102, builder.build());
        }
    }
}
