package com.supergym.sep490_supergymmanagement.fragments;

import static android.content.Context.NOTIFICATION_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.supergym.sep490_supergymmanagement.FirebaseImageLoader.FirebaseHelper;
import com.supergym.sep490_supergymmanagement.R;
import com.supergym.sep490_supergymmanagement.ViewMainContent;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



public class SettingFragment extends Fragment {
    private static final String PREFS_NAME = "WaterReminderPrefs";
    private static final String WATER_REMINDER_ENABLED = "water_reminder_enabled";
    private static final String CHANNEL_Water = "WaterReminderChannel";
    private static final String WORK_TAG = "daily_water_reminder";
    private static final String REMINDER_TIME_KEY = "reminder_time";
    private static final int DEFAULT_REMINDER_HOUR = 19; // 7 PM
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;


    private static final String SendNotification = "notification_enabled";
    private static final String CHANNEL_ID1 = "default_channel_id";
    private static final String CHANNEL_NAME = "General Notifications";
    private SharedPreferences sharedPreferences;
    private Switch switchWaterConsumption, switchTodaySchedule, switchNotifications;
    private CardView returnBtn;
    FirebaseHelper firebaseHelper = new FirebaseHelper();
    // Get today's date in YYYY-MM-DD format
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    String todayDate = sdf.format(new Date());
    public static String userIdFinal;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_fragment, container, false);

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userIdFinal = user.getUid();
        }
        // Find Water Consumption Switch
        switchWaterConsumption = view.findViewById(R.id.switchWaterConsumption);
        returnBtn = view.findViewById(R.id.returnBtn);
        returnBtn.setOnClickListener(v -> getActivity().onBackPressed());
        // Set up switch listener
        switchWaterConsumption.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save state to SharedPreferences
            saveSetting(WATER_REMINDER_ENABLED, isChecked);

            if (isChecked) {
                // Check and request notification permission first
                if (checkNotificationPermission()) {
                    // Check and disable battery optimizations if necessary
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        PowerManager powerManager = (PowerManager) requireContext().getSystemService(Context.POWER_SERVICE);
                        if (powerManager != null && !powerManager.isIgnoringBatteryOptimizations(requireContext().getPackageName())) {
                            disableBatteryOptimizations();
                        }
                    }

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

        // Find the Switch in the layout
        switchNotifications = view.findViewById(R.id.switchNotifications);

        // Set the listener for state changes
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveSetting(SendNotification, isChecked);
            if (isChecked) {
                // Switch is ON (Notifications enabled)
                enableNotifications();
            } else {
                // Switch is OFF (Notifications disabled)
                disableNotifications();
            }
        });

        switchTodaySchedule = view.findViewById(R.id.switchTodaySchedule);
        switchTodaySchedule.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save state to SharedPreferences
            saveSetting("today_schedule_enabled", isChecked);

            if (isChecked) {
                // Check and disable battery optimizations if necessary
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PowerManager powerManager = (PowerManager) requireContext().getSystemService(Context.POWER_SERVICE);
                    if (powerManager != null && !powerManager.isIgnoringBatteryOptimizations(requireContext().getPackageName())) {
                        disableBatteryOptimizations();
                    }
                }

                // Enable the daily check-in reminder
                scheduleCheckInReminder();
                Toast.makeText(requireContext(), "Today's schedule reminder enabled", Toast.LENGTH_SHORT).show();
            } else {
                // Disable the daily check-in reminder
                WorkManager.getInstance(requireContext()).cancelUniqueWork("check_in_reminder");
                Toast.makeText(requireContext(), "Today's schedule reminder disabled", Toast.LENGTH_SHORT).show();
            }
        });

        scheduleCheckInReminder();
        return view;
    }
    private void disableBatteryOptimizations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(android.provider.Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            requireContext().startActivity(intent);
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Load and set UI state from SharedPreferences
        loadSettings();
    }

    // Method to enable notifications
    // Method to enable notifications
    private void enableNotifications() {
        // Get the NotificationManager using ContextCompat.getSystemService
        NotificationManager notificationManager = ContextCompat.getSystemService(requireContext(), NotificationManager.class);

        // Check if the device is running Android Oreo or later
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create a notification channel
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID1,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Notify the user that notifications are enabled
        Toast.makeText(requireContext(), "Notifications Enabled", Toast.LENGTH_SHORT).show();
    }

    // Method to disable notifications
    private void disableNotifications() {
        // Get the NotificationManager using ContextCompat.getSystemService
        NotificationManager notificationManager = ContextCompat.getSystemService(requireContext(), NotificationManager.class);

        // Check if the device is running Android Oreo or later
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Disable the notification channel (users won't receive notifications from this channel)
            notificationManager.deleteNotificationChannel(CHANNEL_ID1);
        }

        // Notify the user that notifications are disabled
        Toast.makeText(requireContext(), "Notifications Disabled", Toast.LENGTH_SHORT).show();
    }


    private void scheduleCheckInReminder() {
        // Create a PeriodicWorkRequest for a daily check at 5 p.m.
        PeriodicWorkRequest checkInReminderWork = new PeriodicWorkRequest.Builder(
                CheckInReminderWorker.class,
                1, TimeUnit.DAYS
        )
                .setInitialDelay(calculateInitialDelayForCheckIn(), TimeUnit.MILLISECONDS)
                .addTag("check_in_reminder")
                .build();

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
                "check_in_reminder",
                ExistingPeriodicWorkPolicy.REPLACE,
                checkInReminderWork
        );
    }

    private long calculateInitialDelayForCheckIn() {
        Calendar calendar = Calendar.getInstance();
        Calendar targetTime = Calendar.getInstance();

        // Set to 5 PM
        targetTime.set(Calendar.HOUR_OF_DAY, 17);
        targetTime.set(Calendar.MINUTE, 0);
        targetTime.set(Calendar.SECOND, 0);

        // If target time has already passed today, schedule for next day
        if (calendar.after(targetTime)) {
            targetTime.add(Calendar.DAY_OF_MONTH, 1);
        }

        return targetTime.getTimeInMillis() - calendar.getTimeInMillis();
    }

    // Worker class to handle check-in reminders
    public static class CheckInReminderWorker extends Worker {

        private FirebaseHelper firebaseHelper = new FirebaseHelper();

        public CheckInReminderWorker(
                @NonNull Context context,
                @NonNull WorkerParameters params
        ) {
            super(context, params);
        }

        @NonNull
        @Override
        public Result doWork() {
            // Get today's date in YYYY-MM-DD format
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String todayDate = sdf.format(new Date());

            // Get user ID from SharedPreferences or a secure source
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String userId = userIdFinal;

            if (userId != null) {
                // Check if the user has checked in today
                firebaseHelper.isUserIdExistsForToday(userId, todayDate, exists -> {
                    if (!exists) {
                        // If the user has not checked in, send a notification
                        sendCheckInReminderNotification(getApplicationContext());
                    }
                });
            }

            return Result.success();
        }

        private void sendCheckInReminderNotification(Context context) {
            Intent intent = new Intent(context, ViewMainContent.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_Water)
                    .setSmallIcon(R.drawable.ic_location_noti)  // Replace with your check-in icon
                    .setContentTitle("Check-In Reminder")
                    .setContentText("Don't forget to check in today!")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(context);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notificationManager.notify(2, builder.build());
            }
        }
    }


    private void loadSettings() {
        // Load water reminder enabled state
        boolean isReminderEnabled = sharedPreferences.getBoolean(WATER_REMINDER_ENABLED, false);
        switchWaterConsumption.setChecked(isReminderEnabled);


        boolean isNotificationEnabled = sharedPreferences.getBoolean(SendNotification, false);
        switchNotifications.setChecked(isNotificationEnabled);

        // Load today's schedule reminder enabled state
        boolean isTodayScheduleEnabled = sharedPreferences.getBoolean("today_schedule_enabled", false);
        switchTodaySchedule.setChecked(isTodayScheduleEnabled);

        // Enable scheduleCheckInReminder if the switch is enabled
        if (isTodayScheduleEnabled) {
            scheduleCheckInReminder();
        }
    }


    private void saveSetting(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
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

        // Configure constraints for WorkManager
        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true) // Ensure the work runs only when battery is not low
                .build();

        // Schedule daily work with the constraints
        PeriodicWorkRequest waterReminderWork = new PeriodicWorkRequest.Builder(
                WaterReminderWorker.class,
                1, TimeUnit.DAYS
        )
                .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
                .setConstraints(constraints) // Apply constraints
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

            NotificationChannel channel = new NotificationChannel(CHANNEL_Water, name, importance);
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
            try {
                sendWaterReminderNotification(getApplicationContext());
                return Result.success();
            } catch (Exception e) {
                return Result.failure();
            }
        }

        private void sendWaterReminderNotification(Context context) {
            Intent intent = new Intent(context, ViewMainContent.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_Water)
                    .setSmallIcon(R.drawable.ic_noti_water)
                    .setContentTitle("Hydration Reminder")
                    .setContentText("Have you had enough water today? Stay hydrated!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notificationManager.notify(1, builder.build());
            }
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