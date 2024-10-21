package com.example.sep490_supergymmanagement.adapters;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sep490_supergymmanagement.R;

import java.util.Calendar;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

public class DateGenerator {

    private static int selectedDay ; // Default to the first day of the month

    public static void generateMonthDates(Context context, LinearLayout daysLayout, int dayNumber) {
        // Clear any existing views in the layout
        daysLayout.removeAllViews();
        selectedDay = dayNumber;
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1); // Start at the first day of the month

        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Generate the dates for each day of the current month
        for (int i = 1; i <= daysInMonth; i++) {
            // Get the day of the week and the date
            String dateText = String.valueOf(i);
            String dayText = getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK));

            // Create a new LinearLayout for each day
            LinearLayout dayLayout = new LinearLayout(context);
            dayLayout.setOrientation(LinearLayout.VERTICAL);

            // Set LayoutParams with margins
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(10, 0, 10, 0); // Set 10dp margin for left and right
            dayLayout.setLayoutParams(params);
            dayLayout.setPadding(28, 28, 28, 28);
            dayLayout.setGravity(Gravity.CENTER);

            // Set the background based on whether it's the selected day
            if (i == selectedDay) {
                dayLayout.setBackgroundResource(R.drawable.selected_day_background);
            } else {
                dayLayout.setBackgroundResource(R.drawable.unselected_day_background);
            }

            // Create TextViews for day and date
            TextView dayTextView = new TextView(context);
            dayTextView.setText(dayText);
            dayTextView.setTextColor(Color.BLACK);
            dayTextView.setTypeface(null, Typeface.BOLD);

            TextView dateTextView = new TextView(context);
            dateTextView.setText(dateText);
            dateTextView.setTextColor(Color.BLACK);
            dateTextView.setTextSize(18);
            dateTextView.setTypeface(null, Typeface.BOLD);

            // Add TextViews to the day layout
            dayLayout.addView(dayTextView);
            dayLayout.addView(dateTextView);

            // Add a click listener to change selection
            int finalI = i; // Required for the inner class
            dayLayout.setOnClickListener(v -> {
                // Update the background for previously selected day
                daysLayout.getChildAt(selectedDay - 1).setBackgroundResource(R.drawable.unselected_day_background);

                // Set the selected day and update background
                selectedDay = finalI;
                dayLayout.setBackgroundResource(R.drawable.selected_day_background);
            });

            // Add the day layout to the daysLayout LinearLayout
            daysLayout.addView(dayLayout);

            // Move to the next day
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    // Helper method to get the day of the week text
    private static String getDayOfWeek(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.MONDAY: return "Mon";
            case Calendar.TUESDAY: return "Tue";
            case Calendar.WEDNESDAY: return "Wed";
            case Calendar.THURSDAY: return "Thu";
            case Calendar.FRIDAY: return "Fri";
            case Calendar.SATURDAY: return "Sat";
            case Calendar.SUNDAY: return "Sun";
            default: return "";
        }
    }
}
