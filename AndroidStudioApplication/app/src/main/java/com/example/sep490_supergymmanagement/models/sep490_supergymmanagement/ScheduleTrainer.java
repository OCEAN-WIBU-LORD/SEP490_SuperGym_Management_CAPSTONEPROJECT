package com.example.sep490_supergymmanagement.models.sep490_supergymmanagement;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.sep490_supergymmanagement.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScheduleTrainer#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScheduleTrainer extends Fragment {

    private TextView scheduleTitle, calendarMonth;
    private GridLayout calendarGrid;
    private Button newEventButton, outfitsButton, adviceButton;
    private ImageButton backButton, refreshButton, addButton, outfitsNavButton, scheduleNavButton;
    private int dayNumber;
    private int theSelectedMonth;
    private int monthPicked;

    // Factory method to create a new instance of this fragment using the provided parameters.
    public static ScheduleTrainer newInstance(String param1, String param2) {
        ScheduleTrainer fragment = new ScheduleTrainer();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Handle any arguments passed to the fragment
            String mParam1 = getArguments().getString("param1");
            String mParam2 = getArguments().getString("param2");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule_trainer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI elements
        scheduleTitle = view.findViewById(R.id.scheduleTitle);
        calendarMonth = view.findViewById(R.id.calendarMonth);
        calendarGrid = view.findViewById(R.id.calendarGrid);
        generateCalendarForCurrentMonth();
        allFunctionScheduleTrainer();

    }

    private void allFunctionScheduleTrainer() {
        // Ensure calendarMonth is initialized
        if (calendarMonth == null) {
            calendarMonth = getView().findViewById(R.id.calendarMonth); // Make sure the ID is correct
        }

        // Set an OnClickListener on the calendarMonth TextView to open the DatePickerDialog
        calendarMonth.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Update the selected month and year
                        calendar.set(Calendar.YEAR, selectedYear);
                        calendar.set(Calendar.MONTH, selectedMonth);



                        // Format and update the calendarMonth TextView
                        String monthName = new SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.getTime());
                        calendarMonth.setText(String.format("%s %d", monthName, selectedYear));
                        monthPicked = selectedMonth;
                        // Refresh the calendar grid based on the new month and year
                        updateCalendarGrid(selectedYear, selectedMonth);
                    },
                    year, month, calendar.get(Calendar.DAY_OF_MONTH)
            );

            // Safely hide the day picker to prevent a crash
            try {
                int dayPickerId = getResources().getIdentifier("day", "id", "android");
                View dayPicker = datePickerDialog.getDatePicker().findViewById(dayPickerId);
                if (dayPicker != null) {
                    dayPicker.setVisibility(View.GONE); // Hide the day picker if it exists
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Log or handle the exception in case the day picker view is not found
            }

            datePickerDialog.show();
        });

    }


    private void updateCalendarGrid(int year, int month) {
        // Clear the previous views in the GridLayout
        calendarGrid.removeAllViews();
        // Get the number of days in the selected month and year
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        // Save the selected month to a global variable
        this.theSelectedMonth = month;
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Loop to generate days for the selected month and add them to the GridLayout
        for (int day = 1; day <= daysInMonth; day++) {
            TextView dayView = new TextView(getContext());

            // Set GridLayout parameters for the dayView
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = GridLayout.LayoutParams.WRAP_CONTENT;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.setMargins(8, 16, 8, 16); // Adjust margins for spacing

            dayView.setLayoutParams(params);
            dayView.setText(String.valueOf(day));
            dayView.setGravity(Gravity.CENTER);
            dayView.setTextSize(16);
            dayView.setPadding(30, 30, 30, 30);
            dayView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.circle_background));

            // Set OnClickListener for each day
            int finalDay = day;
            dayView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create an Intent to start ScheduleTrainerDetailsActivity
                    Intent intent = new Intent(getActivity(), ScheduleTrainerDetails.class); // Assuming ScheduleTrainerDetails is now an Activity

                    // Pass a number (for example, the day number)
                    dayNumber = Integer.parseInt(dayView.getText().toString()); // Example number
                    intent.putExtra("day_number", dayNumber);
                    Toast.makeText(getActivity(), "Selected: " + dayNumber, Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
            });


            // Add the day view to the GridLayout
            calendarGrid.addView(dayView);
        }
    }

    private  void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frame_layout, fragment);  // Use add instead of replace
        fragmentTransaction.addToBackStack(null);  // Add to backstack so user can go back
        fragmentTransaction.commit();
    }

    private void generateCalendarForCurrentMonth() {
        // Day Headers (Mon, Tue, Wed, etc.)
        String[] headers = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

        // Add the day headers to the GridLayout
        for (String header : headers) {
            TextView headerView = new TextView(getContext());
            headerView.setText(header);
            headerView.setGravity(Gravity.CENTER);
            headerView.setTextSize(14);
            headerView.setPadding(8, 8, 8, 8);
            calendarGrid.addView(headerView);
        }

        // Get the current month and year
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK) - 2; // Adjust to start on Monday

        // Add blank views before the first day of the month
        for (int i = 0; i < firstDayOfMonth; i++) {
            TextView emptyView = new TextView(getContext());
            calendarGrid.addView(emptyView);
        }

        // Create an array of days that will have icons
        SparseArray<Integer> daysWithIcons = new SparseArray<>();
        daysWithIcons.put(5, R.drawable.ic_shirt);   // Day 5 with a shirt icon
        daysWithIcons.put(7, R.drawable.ic_luggage); // Day 7 with a luggage icon
        daysWithIcons.put(9, R.drawable.baseline_water_drop_24);    // Day 9 with a water drop icon

        daysWithIcons.put(15, R.drawable.ic_shirt);   // Day 5 with a shirt icon
        daysWithIcons.put(27, R.drawable.ic_luggage); // Day 7 with a luggage icon
        daysWithIcons.put(31, R.drawable.baseline_water_drop_24);    // Day 9 with a water drop icon
        // Add more icons as necessary

        // Generate days and add them to the GridLayout
        // Generate days and add them to the GridLayout
        for (int day = 1; day <= daysInMonth; day++) {
            // Create a final or effectively final variable to hold the day value for the lambda expression
            final int currentDay = day;

            TextView dayView = new TextView(getContext());

            // Set GridLayout parameters for the dayView
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = GridLayout.LayoutParams.WRAP_CONTENT;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;

            // Add margins to create space between rows
            params.setMargins(8, 16, 8, 16); // Adjust these values as needed (left, top, right, bottom)


            dayView.setLayoutParams(params);

            dayView.setText(String.valueOf(day));
            dayView.setGravity(Gravity.CENTER);
            dayView.setTextSize(16);
            dayView.setPadding(30, 30, 30, 30);

            // Set circular background for each day
            dayView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.circle_background));

            // Check if the day should have an icon
            if (daysWithIcons.get(day) != null) {
                Drawable icon = ContextCompat.getDrawable(getContext(), daysWithIcons.get(day));
                dayView.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null);
                dayView.setCompoundDrawablePadding(8);
            }

            // Set OnClickListener for each day using the final 'currentDay' variable
            dayView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create an Intent to start ScheduleTrainerDetailsActivity
                    Intent intent = new Intent(getActivity(), ScheduleTrainerDetails.class); // Assuming ScheduleTrainerDetails is now an Activity
                    // Pass a number (for example, the day number)
                    dayNumber = Integer.parseInt(dayView.getText().toString()); // Example number
                    intent.putExtra("day_number", dayNumber);
                    // Pass the selected month (which was stored when the user selected a month)
                    intent.putExtra("calendar_real",theSelectedMonth);

                    Toast.makeText(getActivity(), "Selected: " + dayNumber, Toast.LENGTH_SHORT).show();

                    startActivity(intent);
                }
            });



            // Add the day view to the GridLayout
            calendarGrid.addView(dayView);
        }

    }

}
