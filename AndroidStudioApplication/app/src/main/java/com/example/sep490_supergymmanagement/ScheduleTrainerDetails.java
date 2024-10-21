package com.example.sep490_supergymmanagement;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.sep490_supergymmanagement.adapters.DateGenerator;

import java.util.Calendar;
import java.util.Map;

public class ScheduleTrainerDetails extends AppCompatActivity {

    private LinearLayout scheduleLayout;
    private TextView dateText;
    private LinearLayout daysLayout;
    private int dayNumber;


    private int calendar_real;

    private CardView backToMainScheduleBtn;
    private HorizontalScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.schedule_date_details); // Inflate the layout for this activity
        // Retrieve the day number passed via Intent
         dayNumber = getIntent().getIntExtra("day_number", -1); // -1 is the default value if "day_number" is not found
        calendar_real = getIntent().getIntExtra("calendar_real", -1);
        // Initialize UI elements
        scheduleLayout = findViewById(R.id.scheduleLinearLayout); // The ScrollView's LinearLayout

        backToMainScheduleBtn = findViewById(R.id.backToMainScheduleBtn);
        // Set the click listener for the back button
        backToMainScheduleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the current activity and go back to the previous one
                onBackPressed();
            }
        });
         daysLayout = findViewById(R.id.daysLayout);

        dateText = findViewById(R.id.dateText);
         // Generate the month dates dynamically
        dateText.setText( String.valueOf(dayNumber));

        scrollView = findViewById(R.id.scrollView); // The parent ScrollView for daysLayout


        DateGenerator.generateMonthDates(this, daysLayout, dayNumber);



        // Generate the event schedule
        generateEvents(scheduleLayout);
        moveScrollBar();
    }

    private void moveScrollBar(){
        // Scroll to the selected day after layout is rendered
        daysLayout.post(() -> {
            // Get the view of the selected day
            View selectedDayView = daysLayout.getChildAt(dayNumber - 1); // dayNumber is 1-based, so subtract 1
            if (selectedDayView != null) {
                // Get the X coordinate of the selected day
                int xCoordinate = selectedDayView.getLeft();
                // Scroll to the X coordinate of the selected day view
                scrollView.smoothScrollTo(xCoordinate, 0); // Scroll horizontally to the selected day
            }
        });
    }

    // Function to generate events for each time slot from 5 AM to 10 PM
    private void generateEvents(LinearLayout scheduleLayout) {
        // Create a loop from 5 AM to 10 PM (17 hours)
        int startHour = 5;  // Start from 5 AM
        int endHour = 22;   // End at 10 PM (22 in 24-hour format)

        // Get current time
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);


        boolean lineInserted = false; // To check if the line has been inserted

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
            eventTextView.setText("Schedule Class:\n 1vs1 Training with Duong");  // Default text for an empty slot
            eventTextView.setBackground(getResources().getDrawable(R.drawable.event_background, null));  // Custom background
            eventTextView.setPadding(16, 18, 16, 18);  // Padding inside the event block
            eventTextView.setTextColor(Color.WHITE); // Text color for event
            eventTextView.setGravity(Gravity.CENTER);

            // Add the time and event TextViews to the row
            timeRow.addView(timeTextView);
            timeRow.addView(eventTextView);

            // Add the time row to the schedule layout
            scheduleLayout.addView(timeRow);

            // Insert the horizontal line across the time row if the current time exactly matches the hour
            if (hour == currentHour && currentMinute == 0 && !lineInserted) {
                // Create a view to represent the line
                View line = new View(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 6);  // Line thickness of 6 pixels
                params.gravity = Gravity.CENTER_VERTICAL;  // Center the line vertically
                line.setLayoutParams(params);
                line.setBackgroundColor(Color.RED);  // Set the line color to red

                // Add the line over the time row by overlaying it
                scheduleLayout.addView(line);
                lineInserted = true; // Ensure the line is added only once
            }

            // If the current time falls between this hour and the next, insert the line between rows
            if (hour == currentHour && currentMinute > 0 && !lineInserted) {
                // Insert the line between this time and the next time row
                insertCurrentTimeLine(scheduleLayout);
                lineInserted = true; // Ensure we insert the line only once
            }
        }
    }
    // Function to insert the current time line
    private void insertCurrentTimeLine(LinearLayout scheduleLayout) {
        View currentTimeLine = new View(this);
        currentTimeLine.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 4));  // Height of 4 pixels
        currentTimeLine.setBackgroundColor(Color.RED);  // Set the color of the line
        scheduleLayout.addView(currentTimeLine);
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
