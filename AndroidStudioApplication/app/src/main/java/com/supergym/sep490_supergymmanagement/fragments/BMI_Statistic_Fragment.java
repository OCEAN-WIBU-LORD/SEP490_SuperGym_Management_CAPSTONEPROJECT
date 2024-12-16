package com.supergym.sep490_supergymmanagement.fragments;


import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.supergym.sep490_supergymmanagement.animations.CupOfWaterView;
import com.supergym.sep490_supergymmanagement.models.UserStatistics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.supergym.sep490_supergymmanagement.FragmentUserProfile;
import com.supergym.sep490_supergymmanagement.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.components.Description;



public class BMI_Statistic_Fragment extends Fragment {

    FirebaseAuth mAuth;
    // Firebase Database reference
    private DatabaseReference databaseReference;

    private Button saveBtn, clearButton;
    private boolean isButtonClickable = true;
    // Views
    private CardView selectDateButton;
    private LinearLayout advanceInfoContentLayout;
    private CardView btnReturnBtn, editBMICard, editCaloriesCard, editStepsCard, editWaterIntakeCard, allTimeStatistic, userWater, editAdvanceCard, dropdownBtn;
    private TextView DatePicked, DatePicked3, txtBMITextView, txtBMIStatus, txtcaloriesTextView, stepsTextView, txtWaterTextView, appearText;

    // Get the current year, month, and day
    // Set up the Calendar instance for the current date
    Calendar calendar = Calendar.getInstance();

    private TextView tvNoData;

    private int year, month , day ;
     private String userId,  selectedDate, datePicked;
    private SimpleDateFormat dateFormat;
    private Animation cardClickAnimation;
    CupOfWaterView cupOfWaterView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bmi_statistic, container, false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        // Initialize Firebase Database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("users");

// Test Demo Listing - Duong DDHE176496
        // Initialize Button and set OnClickListener
        btnReturnBtn = view.findViewById(R.id.btnReturnBtn);
        btnReturnBtn.setOnClickListener(v -> replaceFragment(new FragmentUserProfile()));

        // Get reference to the button
        selectDateButton = view.findViewById(R.id.selectDateButton);
        DatePicked = view.findViewById(R.id.DatePicked);
        DatePicked3 = view.findViewById(R.id.DatePicked3);

        // Get the references for TextView and CardView
        txtBMITextView = view.findViewById(R.id.txtBMITextView);
        editBMICard = view.findViewById(R.id.editBMI);
        txtBMIStatus = view.findViewById(R.id.txtBMIStatus);
        editCaloriesCard = view.findViewById(R.id.editCaloriesCard);
        editStepsCard = view.findViewById(R.id.editStepsCard);
        txtcaloriesTextView = view.findViewById(R.id.txtcaloriesTextView);
        stepsTextView = view.findViewById(R.id.stepsTextView);
        txtWaterTextView = view.findViewById(R.id.txtWaterTextView);
        editWaterIntakeCard = view.findViewById(R.id.editWater);
        userWater= view.findViewById(R.id.userWater);

         cupOfWaterView = view.findViewById(R.id.cupOfWater);

        advanceInfoContentLayout = view.findViewById(R.id.advanceInfoContentLayout);
        advanceInfoContentLayout.setVisibility(View.GONE);
        editAdvanceCard = view.findViewById(R.id.editAdvanceCard);
        dropdownBtn = view.findViewById(R.id.dropdownBtn);
        appearText = view.findViewById(R.id.appearText);
        appearText.setVisibility(View.VISIBLE);

        //date Selected
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);



         cardClickAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.card_click_animation);


        // Set the DatePicked TextView to the current date
        String currentDate = day + "/" + (month + 1) + "/" + year;
       // DatePicked.setText(currentDate);

// Find the allTimeStatistic button
        allTimeStatistic = view.findViewById(R.id.allTimeStatistic);

// Date Format for extracting and parsing the date from DatePicked TextView
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

// Extract date from the DatePicked TextView
        try {
            String selectedDateString = DatePicked.getText().toString().trim();
            Date selectedDate = dateFormat.parse(selectedDateString);  // Parse the String into a Date object
        } catch (ParseException e) {
            e.printStackTrace();  // Handle parsing exception
        }

        // getUserStatisticsFromFirebase(userId, selectedDate);
        saveBtn = view.findViewById(R.id.saveBtn);

        // Inside the onCreateView method
        clearButton = view.findViewById(R.id.clearButton);
        functionAllButton();
        displayWaterLevel2();
        displayTodayData(currentDate);
        return view;
    }

    private void displayTodayData(String currentDate){
        String selectedDate = currentDate;
        displayWaterLevel2();
        // Now fetch the statistics for the selected date
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && selectedDate != null) {
            getUserStatisticsFromFirebase(userId, selectedDate);

            // Add this line to ensure water level is displayed after data is retrieved
            // Use a post-delayed method to ensure UI is updated
            txtWaterTextView.post(new Runnable() {
                @Override
                public void run() {
                    displayWaterLevel2();
                }
            });
        }
    }
    private void displayWaterLevel2() {
        try {
            // Replace commas with dots for consistency
            String waterText = txtWaterTextView.getText().toString().replace(",", ".");
            float waterInLiters = Float.parseFloat(waterText);  // Convert to float
            float maxLiters = 2.0f;

            // Update water level view
            cupOfWaterView.setWaterLevelInLiters(waterInLiters, maxLiters);
        } catch (NumberFormatException e) {
            Log.e("WaterLevel", "Invalid water input: " + e.getMessage());
        }
    }


    private void displayWaterLevel() {
        String waterText = txtWaterTextView.getText().toString().trim();
        float waterInLiters = 0.0f; // Default to 0 liters
        try {
            NumberFormat numberFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
            Number waterNumber = numberFormat.parse(waterText);
            waterInLiters = waterNumber.floatValue();
        } catch (Exception e) {
            Log.e("WaterLevel", "Failed to parse water level, defaulting to 0 liters");
        }

        float maxLiters = 2.0f; // Maximum capacity of the cup
        cupOfWaterView.setWaterLevelInLiters(waterInLiters, maxLiters);
    }




    private void functionAllButton(){



        // Set an OnClickListener for the editBMI card
        editBMICard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditBMIDialog();
            }
        });

        // Set OnClickListener for Calories Burned Card
        // Set OnClickListener for Calories Burned Card
        editCaloriesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog("Edit Calories Burned", "Enter calories burned", new InputCallback() {
                    @Override
                    public void onInputConfirmed(String input) {
                        txtcaloriesTextView.setText(input);
                    }
                });
            }
        });

// Set OnClickListener for Steps Walked Card
        editStepsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog("Edit Steps Walked", "Enter steps walked", new InputCallback() {
                    @Override
                    public void onInputConfirmed(String input) {
                        stepsTextView.setText(input);
                    }
                });
            }
        });


        // Set OnClickListener for Water Intake Card
        editWaterIntakeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog("Edit Water Intake", "Enter water intake (liters)", new InputCallback() {
                    @Override
                    public void onInputConfirmed(String input) {
                        // Update the water intake TextView
                        txtWaterTextView.setText(input);

                        // Update water level display
                        displayWaterLevel2();
                    }
                });
            }
        });



        // Set an OnClickListener on the button to show the DatePickerDialog
        selectDateButton.setOnClickListener(v -> {
            // Create and show a DatePickerDialog, passing in the Activity context
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getActivity(),
                    (view, year, month, day) -> {
                        // Format the date and set it as the button text
                        String selectedDate = day + "/" + (month + 1) + "/" + year;
                        DatePicked.setText(selectedDate);


                        // Now fetch the statistics for the selected date
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser != null) {
                            getUserStatisticsFromFirebase(userId, selectedDate);
                        }
                    },
                    year, month, day
            );
            displayWaterLevel2();

            // Show the DatePickerDialog
            datePickerDialog.show();
        });


        allTimeStatistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBarChartDialog();
            }
        });

        // Set an OnClickListener on the button to show the DatePickerDialog
        selectDateButton.setOnClickListener(v -> {

            // Create and show a DatePickerDialog, passing in the Activity context
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getActivity(), // Use getActivity() to get the parent activity context
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                            // Format the date and set it as the button text
                            String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                            DatePicked.setText(selectedDate);

                            // Get the userId (you need to have it already, maybe from Firebase Auth or passed from another activity)
                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            // Call getUserStatisticsFromFirebase with the selected date
                            getUserStatisticsFromFirebase(userId, selectedDate);
                        }
                    },
                    year, month, day
            );

            // Show the DatePickerDialog
            datePickerDialog.show();
        });



        userWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the button is currently clickable
                if (!isButtonClickable) {
                    return;
                }

                // Disable the button immediately
                isButtonClickable = false;

                // Start animation
                v.startAnimation(cardClickAnimation);
                String datePicked = DatePicked.getText().toString().trim();

                if (compareDates(datePicked)) {
                    if (checkUserInput()) {
                        try {
                            // Dynamically get the current locale
                            Locale currentLocale = Locale.getDefault();
                            NumberFormat numberFormat = NumberFormat.getInstance(currentLocale);

                            // Parse water intake value from TextView
                            double currentWater = numberFormat.parse(txtWaterTextView.getText().toString().trim()).doubleValue();

                            // Parse the BMI value using NumberFormat
                            double bmi = numberFormat.parse(txtBMITextView.getText().toString().trim()).doubleValue();

                            // Add 0.25 to the current water value
                            double updatedWater = currentWater + 0.25;

                            // Parse calories and steps (integers are locale-independent)
                            int calories = Integer.parseInt(txtcaloriesTextView.getText().toString().trim());
                            int steps = Integer.parseInt(stepsTextView.getText().toString().trim());

                            // Save updated water intake and other statistics to Firebase
                            saveStatisticsToFirebase(userId, datePicked, bmi, txtBMIStatus.getText().toString().trim(), calories, steps, updatedWater);

                            // Update the UI with the updated water value
                            txtWaterTextView.setText(String.format(currentLocale, "%.2f", updatedWater));

                            // Trigger animations and display water level
                            controlWaterAnimation();
                            displayWaterLevel2();

                            // Re-enable the button after 1 second
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    isButtonClickable = true;
                                }
                            }, 1000);

                        } catch (ParseException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Invalid water input format!", Toast.LENGTH_SHORT).show();

                            // Re-enable the button
                            isButtonClickable = true;
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Please enter valid numeric values!", Toast.LENGTH_SHORT).show();

                            // Re-enable the button
                            isButtonClickable = true;
                        }
                    } else {
                        Toast.makeText(getContext(), "Please check again. The input is null.", Toast.LENGTH_SHORT).show();

                        // Re-enable the button
                        isButtonClickable = true;
                    }
                } else {
                    Toast.makeText(getContext(), "You cannot update statistics for a future day!", Toast.LENGTH_SHORT).show();

                    // Re-enable the button
                    isButtonClickable = true;
                }
            }
        });





        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String datePicked = DatePicked.getText().toString().trim();
                String bmiText = txtBMITextView.getText().toString().trim().replace(",", "."); // Standardize input
                String caloriesText = txtcaloriesTextView.getText().toString().trim();
                String stepsText = stepsTextView.getText().toString().trim();
                String waterText = txtWaterTextView.getText().toString().trim().replace(",", ".");

                // Validate fields
                if (bmiText.isEmpty() || caloriesText.isEmpty() || stepsText.isEmpty() || waterText.isEmpty()) {
                    Toast.makeText(getContext(), "All fields must be filled!", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    // Parse numeric values
                    double bmi = Double.parseDouble(bmiText); // Locale-independent parsing
                    int calories = Integer.parseInt(caloriesText);
                    int steps = Integer.parseInt(stepsText);
                    double water = Double.parseDouble(waterText);

                    if (compareDates(datePicked)) {
                        // Save statistics to Firebase
                        saveStatisticsToFirebase(
                                userId,
                                datePicked,
                                bmi,
                                txtBMIStatus.getText().toString().trim(),
                                calories,
                                steps,
                                water
                        );

                        // Update water level display
                        displayWaterLevel2();
                    } else {
                        Toast.makeText(getContext(), "You cannot update statistics for a future day!", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Please enter valid numeric values!", Toast.LENGTH_SHORT).show();
                }
            }
        });




        // Set click listener for edit card to toggle visibility
        dropdownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (advanceInfoContentLayout.getVisibility() == View.GONE) {
                    advanceInfoContentLayout.setVisibility(View.VISIBLE); // Expand
                    appearText.setVisibility(View.GONE);
                } else {
                    advanceInfoContentLayout.setVisibility(View.GONE); // Collapse
                    appearText.setVisibility(View.VISIBLE);
                }
            }
        });






        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllFields();
            }
        });



    }

    public void controlWaterAnimation2(){
        // Get current water level and increase it by a small amount
        float currentLevel = cupOfWaterView.getWaterLevel();
        float newLevel = Math.min(currentLevel + 0.125f, 1f);  // Cap the water level at 1.0 (full)

        // Animate the water level change
        ObjectAnimator animator = ObjectAnimator.ofFloat(cupOfWaterView, "waterLevel", currentLevel, newLevel);
        animator.setDuration(1000);  // 1 second animation
        animator.setInterpolator(new DecelerateInterpolator());  // Smooth animation
        animator.start();
    }
    public void controlWaterAnimation(){
        // Get current water level and increase it by a small amount
        float currentLevel = cupOfWaterView.getWaterLevel();
        float newLevel = Math.min(currentLevel + 0.125f, 1f);  // Cap the water level at 1.0 (full)

        // Animate the water level change
        ObjectAnimator animator = ObjectAnimator.ofFloat(cupOfWaterView, "waterLevel", currentLevel, newLevel);
        animator.setDuration(1000);  // 1 second animation
        animator.setInterpolator(new DecelerateInterpolator());  // Smooth animation
        animator.start();
    }

    public boolean checkUserInput(){
        if(txtBMITextView.getText().toString().trim().equals("None")){
            Toast.makeText(getContext(), "Please Input The BMI before saving.", Toast.LENGTH_SHORT).show();
            return false;
        }else if(txtcaloriesTextView.getText().toString().trim() == null){
            Toast.makeText(getContext(), "Please Input The Calories before saving.", Toast.LENGTH_SHORT).show();
            return false;
        }else if(txtWaterTextView.getText().toString().trim() == null){
            Toast.makeText(getContext(), "Please Input The Water before saving.", Toast.LENGTH_SHORT).show();
            return false;
        }else if(stepsTextView.getText().toString().trim() == null){
            Toast.makeText(getContext(), "Please Input The Steps before saving.", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }

    public boolean compareDates(String datePickedString) {
        // Step 1: Get the current date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDateString = dateFormat.format(calendar.getTime());

        // Step 2: Parse the DatePicked string
        try {
            // Parse the DatePicked string to a Date object
            Date datePicked = dateFormat.parse(datePickedString);

            // Parse the current date string to a Date object
            Date currentDate = dateFormat.parse(currentDateString);

            // Step 3: Compare the two dates
            if (datePicked.compareTo(currentDate) < 0) {
                // datePicked is earlier than the current date
                return true;
            } else if (datePicked.compareTo(currentDate) > 0) {
                // datePicked is later than the current date
                Toast.makeText(getActivity(), "The picked date is later than the current date", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                // Both dates are the same
                return true;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error parsing the date", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    // Method to save BMI and statistics to Firebase
    // Method to save BMI and statistics to Firebase
    private void saveStatisticsToFirebase(String userId, String date, double bmi, String bmiStatus, int calories, int steps, double waterIntake) {
        // Create an object of UserStatistics
        UserStatistics userStatistics = new UserStatistics(date, bmi, bmiStatus, calories, steps, waterIntake);

        // Save the data under the "Statistics" node in Firebase
        DatabaseReference statisticsRef = FirebaseDatabase.getInstance().getReference("Statistics");
        statisticsRef.child(userId).child(date).setValue(userStatistics)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Data saved successfully
                        DatePicked3.setText("");
                        Toast.makeText(getContext(), "Data saved successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        // Failed to save data
                        Toast.makeText(getContext(), "Failed to save data", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // Method to retrieve and display user statistics from Firebase based on the selected date
    // Method to retrieve and display user statistics from Firebase based on the selected date
    private void getUserStatisticsFromFirebase(String userId, String selectedDate) {
        // Create a reference to the "Statistics" node for the selected date
        DatabaseReference statisticsRef = FirebaseDatabase.getInstance().getReference("Statistics").child(userId).child(selectedDate);

        // Add a listener to retrieve data once for the specific date
        statisticsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get the user statistics for the selected date
                    UserStatistics userStatistics = dataSnapshot.getValue(UserStatistics.class);

                    if (userStatistics != null) {
                        // Get current and fallback locales
                        Locale currentLocale = Locale.getDefault();
                        Locale vietnameseLocale = new Locale("vi", "VN");
                        Locale englishLocale = Locale.US;

                        // Create multiple NumberFormat instances
                        NumberFormat[] formatters = {
                                NumberFormat.getInstance(currentLocale),
                                NumberFormat.getInstance(vietnameseLocale),
                                NumberFormat.getInstance(englishLocale)
                        };

                        // Try parsing with different locales
                        double bmi = parseNumberSafely(String.valueOf(userStatistics.getBmi()), formatters);
                        double waterIntake = parseNumberSafely(String.valueOf(userStatistics.getWaterIntake()), formatters);

                        try {
                            // Update TextViews with parsed values
                            txtBMITextView.setText(String.format(currentLocale, "%.2f", bmi));
                            txtBMIStatus.setText(userStatistics.getBmiStatus());

                            // Integers are typically locale-independent
                            txtcaloriesTextView.setText(String.valueOf(userStatistics.getCalories()));
                            stepsTextView.setText(String.valueOf(userStatistics.getSteps()));

                            txtWaterTextView.setText(String.format(currentLocale, "%.2f", waterIntake));

                            // Update the DatePicked TextView with the selected date
                            DatePicked.setText(selectedDate);
                            DatePicked3.setText("");

                            // Refresh water level display
                            displayWaterLevel2();

                        } catch (Exception e) {
                            // Log any unexpected errors during UI update
                            Log.e("StatisticsUpdate", "Error updating UI", e);
                            Toast.makeText(getContext(), "Error updating statistics display", Toast.LENGTH_SHORT).show();
                            clearAllFields();
                        }
                    }
                } else {
                    // Handle case where no data exists for the selected date
                    clearAllFields();
                    displayNoData();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database errors
                Log.e("FirebaseError", "Error retrieving data", databaseError.toException());
                Toast.makeText(getContext(), "Error retrieving data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                clearAllFields();
            }
        });
    }

    // Safe number parsing method with multiple locale support
    private double parseNumberSafely(String numberString, NumberFormat[] formatters) {
        // Replace commas with dots to standardize float format
        numberString = numberString.replace(",", ".");

        for (NumberFormat formatter : formatters) {
            try {
                return formatter.parse(numberString).doubleValue();
            } catch (ParseException | NumberFormatException e) {
                continue;
            }
        }

        // Log failure and return 0.0 as default
        Log.w("NumberParsing", "Could not parse number: " + numberString);
        return 0.0;
    }



    private void displayNoData() {
        txtBMITextView.setText("None");
        txtBMIStatus.setText("None");
        txtcaloriesTextView.setText("0");
        stepsTextView.setText("0");
        txtWaterTextView.setText("0");

        DatePicked3.setText("No data available");

        // Ensure water level is displayed even when no data
        displayWaterLevel2();
    }

    // Create a method to clear all input fields and reset the date
    private void clearAllFields() {
        // Reset BMI text fields
        txtBMITextView.setText("None");
        txtBMIStatus.setText("None");

        // Reset Calories, Steps, and Water Intake text fields
        txtcaloriesTextView.setText("0");
        stepsTextView.setText("0");
        txtWaterTextView.setText("0");

        // Reset the date to the current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String currentDate = day + "/" + (month + 1) + "/" + year;
       // DatePicked.setText(currentDate);

        // Optionally reset any other UI elements or variables
        Toast.makeText(getContext(), "All fields have been cleared", Toast.LENGTH_SHORT).show();
    }






    private void showBarChartDialog() {
        // Create a dialog
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_chart);

        // Initialize BarChart and TextView
        BarChart barChart = dialog.findViewById(R.id.dialogLineChart);
         tvNoData = dialog.findViewById(R.id.tvNoData); // TextView for "no data" message

        // Initialize close button
        ImageView btnCloseDialog = dialog.findViewById(R.id.btnCloseDialog);
        btnCloseDialog.setOnClickListener(v -> dialog.dismiss());

        // DatePicker button
        Button btnDatePicker = dialog.findViewById(R.id.btnDatePicker);
        btnDatePicker.setOnClickListener(v -> showDatePicker(barChart, tvNoData));

        // Default chart data for current month and year
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // Months are 0-indexed
        int currentYear = calendar.get(Calendar.YEAR);

        loadChartData(barChart, tvNoData, currentMonth, currentYear); // Load default data

        // Show dialog
        dialog.show();
    }


    // Show DatePicker for selecting month and year
    private void showDatePicker(BarChart barChart, TextView tvNoData) {
        // Create a custom dialog
        final Dialog datePickerDialog = new Dialog(getActivity());
        datePickerDialog.setContentView(R.layout.dialog_month_year_picker);
        datePickerDialog.setTitle("Select Month and Year");

        // Initialize NumberPickers for Month and Year
        NumberPicker npMonth = datePickerDialog.findViewById(R.id.npMonth);
        NumberPicker npYear = datePickerDialog.findViewById(R.id.npYear);

        // Set month picker values
        String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        npMonth.setMinValue(1);
        npMonth.setMaxValue(12);
        npMonth.setDisplayedValues(months);
        npMonth.setValue(Calendar.getInstance().get(Calendar.MONTH) + 1); // Default to current month

        // Set year picker values
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        npYear.setMinValue(currentYear - 10); // Allow 10 years in the past
        npYear.setMaxValue(currentYear + 10); // Allow 10 years in the future
        npYear.setValue(currentYear); // Default to current year

        // Handle "OK" button click
        Button btnOk = datePickerDialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(v -> {
            int selectedMonth = npMonth.getValue(); // Selected month (1-12)
            int selectedYear = npYear.getValue();   // Selected year

            // Load chart data for the selected month and year
            loadChartData(barChart, tvNoData, selectedMonth, selectedYear);

            // Dismiss the dialog
            datePickerDialog.dismiss();
        });

        // Handle "Cancel" button click
        Button btnCancel = datePickerDialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(v -> datePickerDialog.dismiss());

        // Display the dialog
        datePickerDialog.show();
    }



    // Load chart data based on selected month and year
    private void loadChartData(BarChart barChart,TextView tvNoData, int selectedMonth, int selectedYear) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Fetch current user's ID
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("Statistics")
                .child(userId);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<BarEntry> stepsEntries = new ArrayList<>();
                ArrayList<BarEntry> caloriesEntries = new ArrayList<>();
                ArrayList<BarEntry> bmiEntries = new ArrayList<>();
                boolean hasData = false;

                int index = 0;

                // Loop through the days of the selected month
                for (DataSnapshot daySnapshot : snapshot.getChildren()) {
                    String dayKey = daySnapshot.getKey(); // Day "1", "2", etc.
                    if (daySnapshot.hasChild(String.valueOf(selectedMonth))) {
                        DataSnapshot monthSnapshot = daySnapshot.child(String.valueOf(selectedMonth));
                        if (monthSnapshot.hasChild(String.valueOf(selectedYear))) {
                            hasData = true;
                            DataSnapshot dataSnapshot = monthSnapshot.child(String.valueOf(selectedYear));

                            // Extract data
                            float steps = dataSnapshot.child("steps").getValue(Float.class) != null
                                    ? dataSnapshot.child("steps").getValue(Float.class) : 0;
                            float calories = dataSnapshot.child("calories").getValue(Float.class) != null
                                    ? dataSnapshot.child("calories").getValue(Float.class) : 0;
                            float bmi = dataSnapshot.child("bmi").getValue(Float.class) != null
                                    ? dataSnapshot.child("bmi").getValue(Float.class) : 0;

                            // Add entries for chart
                            stepsEntries.add(new BarEntry(index, steps));
                            caloriesEntries.add(new BarEntry(index, calories));
                            bmiEntries.add(new BarEntry(index, bmi));
                            index++;
                        }
                    }
                }

                if (hasData) {
                    // Prepare datasets
                    BarDataSet stepsDataSet = new BarDataSet(stepsEntries, "Steps");
                    stepsDataSet.setColor(getResources().getColor(R.color.blue));

                    BarDataSet caloriesDataSet = new BarDataSet(caloriesEntries, "Calories");
                    caloriesDataSet.setColor(getResources().getColor(R.color.red));

                    BarDataSet bmiDataSet = new BarDataSet(bmiEntries, "BMI");
                    bmiDataSet.setColor(getResources().getColor(R.color.green));

                    // Combine datasets
                    BarData barData = new BarData(stepsDataSet, caloriesDataSet, bmiDataSet);
                    barData.setBarWidth(0.2f); // Adjust bar width for better visibility

                    // Configure chart
                    barChart.setData(barData);
                    barChart.groupBars(0, 0.3f, 0.05f); // Group the bars
                    barChart.getDescription().setEnabled(false);
                    barChart.getXAxis().setGranularity(1f);
                    barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                    barChart.getAxisLeft().setGranularity(1f);

                    // Refresh chart
                    barChart.invalidate();
                    barChart.setVisibility(View.VISIBLE);
                    tvNoData.setVisibility(View.GONE);
                } else {
                    // No data available for the selected month
                    barChart.setVisibility(View.GONE);
                    tvNoData.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Failed to load data!", Toast.LENGTH_SHORT).show();
            }
        });
    }









    //Show Input for Calories, Steps and Water
    // Generic method to handle input dialogs for Calories, Steps, and Water Intake
    private void showInputDialog(String dialogTitle, String hint, InputCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_input_dialog, null);  // Reuse dialog layout
        builder.setView(dialogView);

        final EditText editInput = dialogView.findViewById(R.id.edit_input);
        editInput.setHint(hint);
        builder.setTitle(dialogTitle);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newValue = editInput.getText().toString().trim();
                if (!newValue.isEmpty()) {
                    // Use the callback to handle the input
                    if (callback != null) {
                        callback.onInputConfirmed(newValue);
                    }
                } else {
                    Toast.makeText(requireContext(), "Input cannot be empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public interface InputCallback {
        void onInputConfirmed(String input);
    }



    // Method to show an AlertDialog for editing BMI
    // Method to show an AlertDialog for editing BMI in a Fragment
    // Method to show an AlertDialog for editing weight and height, and calculate BMI
    // Method to show an AlertDialog for editing weight and height, and calculate BMI
    private void showEditBMIDialog() {
        // Use requireContext() in a Fragment
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        // Inflate the custom dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_bmi, null);
        builder.setView(dialogView);

        // Get the EditText fields from the dialog view for weight and height
        final EditText editWeightInput = dialogView.findViewById(R.id.edit_weight_input);
        final EditText editHeightInput = dialogView.findViewById(R.id.edit_height_input);

        // Set up the dialog buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get the user input for weight and height
                String weightStr = editWeightInput.getText().toString();
                String heightStr = editHeightInput.getText().toString();

                // Check if the inputs are not empty
                if (!weightStr.isEmpty() && !heightStr.isEmpty()) {
                    // Parse the inputs to double
                    double weight = Double.parseDouble(weightStr);
                    double height = Double.parseDouble(heightStr);

                    // Calculate BMI: weight / (height^2)
                    double bmi = weight / (height * height);

                    // Update the BMI TextView with the calculated value
                    txtBMITextView.setText(String.format("%.2f", bmi));

                    // Determine BMI status and update the txtBMIStatus TextView
                    String bmiStatus = getBMIStatus(bmi);
                    txtBMIStatus.setText(bmiStatus);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();  // Close the dialog
            }
        });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Method to return BMI status based on the calculated BMI
    private String getBMIStatus(double bmi) {
        if (bmi <= 18.4) {
            return "Underweight";
        } else if (bmi >= 18.5 && bmi <= 24.9) {
            return "Normal";
        } else if (bmi >= 25.0 && bmi <= 39.9) {
            return "Overweight";
        } else if (bmi >= 40.0) {
            return "Obese";
        }
        return "Unknown";
    }








    // Replaces the current fragment with another fragment
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment); // Ensure R.id.frame_layout exists in the parent activity
        fragmentTransaction.commit();
    }
}
