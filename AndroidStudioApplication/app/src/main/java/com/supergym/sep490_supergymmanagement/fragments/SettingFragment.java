package com.supergym.sep490_supergymmanagement.fragments;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.supergym.sep490_supergymmanagement.R;
import com.supergym.sep490_supergymmanagement.ViewMainContent;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class SettingFragment extends Fragment {
    private static final String PREFS_NAME = "WaterReminderPrefs";
    private static final String WATER_REMINDER_ENABLED = "water_reminder_enabled";
    private static final String CHANNEL_ID = "WaterReminderChannel";
    private static final String WORK_TAG = "daily_water_reminder";
    private static final String REMINDER_TIME_KEY = "reminder_time";
    private static final int DEFAULT_REMINDER_HOUR = 19; // 7 PM
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;
    private SharedPreferences sharedPreferences;
    private Switch switchWaterConsumption;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_fragment, container, false);

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Find Water Consumption Switch
        switchWaterConsumption = view.findViewById(R.id.switchWaterConsumption);

        // Set initial switch state from SharedPreferences
        boolean isReminderEnabled = sharedPreferences.getBoolean(WATER_REMINDER_ENABLED, false);
        switchWaterConsumption.setChecked(isReminderEnabled);

        // Set up switch listener
        switchWaterConsumption.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Check and request notification permission first
            if (isChecked) {
                if (checkNotificationPermission()) {
                    enableWaterReminder();
                } else {
                    requestNotificationPermission();
                    // Revert the switch if permission is not granted
                    buttonView.setChecked(false);
                }
            } else {
                disableWaterReminder();
            }
        });

        return view;
    }


    private boolean checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        }
        return true; // For Android versions below 13, permission is not required
    }

    // Request notification permission
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    NOTIFICATION_PERMISSION_REQUEST_CODE
            );
        }
    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, enable water reminder
                switchWaterConsumption.setChecked(true);
                enableWaterReminder();
                Toast.makeText(requireContext(), "Notification permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied
                switchWaterConsumption.setChecked(false);
                Toast.makeText(requireContext(), "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void enableWaterReminder() {
        // Create notification channel
        createNotificationChannel();

        // Schedule daily work
        PeriodicWorkRequest waterReminderWork = new PeriodicWorkRequest.Builder(
                WaterReminderWorker.class,
                1,
                TimeUnit.DAYS
        )
                .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
                .setInputData(createInputData())
                .addTag(WORK_TAG)
                .build();

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
                WORK_TAG,
                ExistingPeriodicWorkPolicy.REPLACE,
                waterReminderWork
        );

        Toast.makeText(requireContext(), "Water reminder enabled", Toast.LENGTH_SHORT).show();
    }

    private void disableWaterReminder() {
        WorkManager.getInstance(requireContext()).cancelUniqueWork(WORK_TAG);
        Toast.makeText(requireContext(), "Water reminder disabled", Toast.LENGTH_SHORT).show();
    }

    private long calculateInitialDelay() {
        Calendar calendar = Calendar.getInstance();
        Calendar targetTime = Calendar.getInstance();

        // Set to 7 PM or custom time
        int reminderHour = sharedPreferences.getInt(REMINDER_TIME_KEY, DEFAULT_REMINDER_HOUR);
        targetTime.set(Calendar.HOUR_OF_DAY, reminderHour);
        targetTime.set(Calendar.MINUTE, 0);
        targetTime.set(Calendar.SECOND, 0);

        // If target time has already passed today, schedule for next day
        if (calendar.after(targetTime)) {
            targetTime.add(Calendar.DAY_OF_MONTH, 1);
        }

        return targetTime.getTimeInMillis() - calendar.getTimeInMillis();
    }

    private Data createInputData() {
        return new Data.Builder()
                .putInt(REMINDER_TIME_KEY,
                        sharedPreferences.getInt(REMINDER_TIME_KEY, DEFAULT_REMINDER_HOUR))
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Water Intake Reminder";
            String description = "Reminds user to drink water";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager =
                    requireActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Worker class to handle water reminder
    public static class WaterReminderWorker extends Worker {
        public WaterReminderWorker(
                @NonNull Context context,
                @NonNull WorkerParameters params
        ) {
            super(context, params);
        }

        @NonNull
        @Override
        public Result doWork() {
            // Here you would check water intake logic
            // For this example, we'll just send a generic reminder
            sendWaterReminderNotification(getApplicationContext());
            return Result.success();
        }

        private void sendWaterReminderNotification(Context context) {
            Intent intent = new Intent(context, ViewMainContent.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_noti_water)  // Replace with your water glass icon
                    .setContentTitle("Hydration Reminder")
                    .setContentText("Have you had enough water today? Stay hydrated!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(context);
            if (ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            notificationManager.notify(1, builder.build());
        }
    }

    // Optional: Method to set custom reminder time
    public void setReminderTime(int hour) {
        sharedPreferences.edit().putInt(REMINDER_TIME_KEY, hour).apply();

        // If reminder is currently enabled, reschedule
        if (sharedPreferences.getBoolean(WATER_REMINDER_ENABLED, false)) {
            enableWaterReminder();
        }
    }
}