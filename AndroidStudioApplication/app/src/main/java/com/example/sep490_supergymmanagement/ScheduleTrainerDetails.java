package com.example.sep490_supergymmanagement;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sep490_supergymmanagement.adapters.DateGenerator;

import java.util.Map;

public class ScheduleTrainerDetails extends AppCompatActivity {

    private LinearLayout scheduleLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.schedule_date_details); // Inflate the layout for this activity

        // Initialize UI elements
        scheduleLayout = findViewById(R.id.scheduleLinearLayout); // The ScrollView's LinearLayout
        LinearLayout daysLayout = findViewById(R.id.daysLayout);

        // Generate the month dates dynamically
        DateGenerator.generateMonthDates(this, daysLayout);
        // Generate the event schedule
        generateEvents(scheduleLayout);
    }

    // Function to generate events for each time slot from 5 AM to 10 PM
    private void generateEvents(LinearLayout scheduleLayout) {
        // Create a loop from 5 AM to 10 PM (17 hours)
        int startHour = 5;  // Start from 5 AM
        int endHour = 22;   // End at 10 PM (22 in 24-hour format)

        for (int hour = startHour; hour <= endHour; hour++) {
            String timeText = getTimeText(hour);

            // Create a new LinearLayout for each time row
            LinearLayout timeRow = new LinearLayout(this);
            timeRow.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            timeRow.setOrientation(LinearLayout.HORIZONTAL);
            timeRow.setPadding(0, 26, 0, 26); // Padding for each time row

            // Create the TextView for time
            TextView timeTextView = new TextView(this);
            timeTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 1)); // Weight 1 for time
            timeTextView.setText(timeText);
            timeTextView.setGravity(Gravity.START);

            // Create the TextView for the event
            TextView eventTextView = new TextView(this);
            eventTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 3)); // Weight 3 for event
            eventTextView.setText("Available Slot");  // Default text for an empty slot
            eventTextView.setBackground(getResources().getDrawable(R.drawable.event_background, null));  // Custom background
            eventTextView.setPadding(16, 18, 16, 18);  // Padding inside the event block
            eventTextView.setTextColor(Color.WHITE); // Text color for event
            eventTextView.setGravity(Gravity.CENTER);

            // Add the time and event TextViews to the row
            timeRow.addView(timeTextView);
            timeRow.addView(eventTextView);

            // Add the time row to the schedule layout
            scheduleLayout.addView(timeRow);
        }
    }




    // Helper function to format the time as a string (12-hour format)
    private String getTimeText(int hour) {
        boolean isAM = hour < 12;
        int displayHour = hour % 12;
        if (displayHour == 0) displayHour = 12; // Handle "12:00 AM" and "12:00 PM"

        String timeSuffix = isAM ? " AM" : " PM";
        return displayHour + ":00" + timeSuffix;
    }
}
