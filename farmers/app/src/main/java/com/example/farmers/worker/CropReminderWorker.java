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
import com.example.farmers.data.db.AppDatabase;
import com.example.farmers.data.model.Crop;

import java.util.List;

public class CropReminderWorker extends Worker {

    public static final String CHANNEL_ID = "crop_reminders";
    public static final String CHANNEL_NAME = "Crop & Garden Reminders";

    public CropReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        try {
            AppDatabase db = AppDatabase.getInstance(context);
            // Non-livedata query or sync check
            int growingCount = db.cropDao().getGrowingCount();
            if (growingCount > 0) {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (notificationManager == null) return Result.success();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(
                            CHANNEL_ID,
                            CHANNEL_NAME,
                            NotificationManager.IMPORTANCE_DEFAULT
                    );
                    channel.setDescription("Daily farm and garden crop tracking reminders");
                    notificationManager.createNotificationChannel(channel);
                }

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("🌾 Daily Crop & Farm Check")
                        .setContentText("You have " + growingCount + " active crop(s) growing. Inspect soil moisture and check for pest risks today.")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true);

                notificationManager.notify(201, builder.build());
            }
        } catch (Exception ignored) {}

        return Result.success();
    }
}
