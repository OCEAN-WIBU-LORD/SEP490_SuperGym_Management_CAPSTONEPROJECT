package com.example.sep490_supergymmanagement.fragments;


import com.example.sep490_supergymmanagement.models.UserStatistics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
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

import androidx.appcompat.app.AppCompatActivity;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.sep490_supergymmanagement.FragmentUserProfile;
import com.example.sep490_supergymmanagement.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BMI_Statistic_Fragment extends Fragment {

    FirebaseAuth mAuth;
    // Firebase Database reference
    private DatabaseReference databaseReference;

    private Button saveBtn;

    // Views
    private CardView selectDateButton;
    private CardView btnReturnBtn, editBMICard, editCaloriesCard, editStepsCard, editWaterIntakeCard, allTimeStatistic;
    private TextView DatePicked, txtBMITextView, txtBMIStatus, txtcaloriesTextView, stepsTextView, txtWaterTextView;

    // Get the current year, month, and day
    // Set up the Calendar instance for the current date
    Calendar calendar = Calendar.getInstance();

    private int year, month , day ;
     private String userId,  selectedDate;
    private SimpleDateFormat dateFormat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bmi_statistic, container, false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        // Initialize Firebase Database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("users");


        // Initialize Button and set OnClickListener
        btnReturnBtn = view.findViewById(R.id.btnReturnBtn);
        btnReturnBtn.setOnClickListener(v -> replaceFragment(new FragmentUserProfile()));

        // Get reference to the button
        selectDateButton = view.findViewById(R.id.selectDateButton);
        DatePicked = view.findViewById(R.id.DatePicked);

        // Get the references for TextView and CardView
        txtBMITextView = view.findViewById(R.id.txtBMITextView);
        editBMICard = view.findViewById(R.id.editBMI);
        txtBMIStatus = view.findViewById(R.id.txtBMIStatus);
        editCaloriesCard = view.findViewById(R.id.editCaloriesCard);
        editStepsCard = view.findViewById(R.id.editStepsCard);
        editCaloriesCard = view.findViewById(R.id.editCaloriesCard);
        txtcaloriesTextView = view.findViewById(R.id.txtcaloriesTextView);
        stepsTextView = view.findViewById(R.id.stepsTextView);
        txtWaterTextView = view.findViewById(R.id.txtWaterTextView);
        editWaterIntakeCard = view.findViewById(R.id.editWater);

        //date Selected
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        // Set the DatePicked TextView to the current date
        String currentDate = day + "/" + (month + 1) + "/" + year;
        DatePicked.setText(currentDate);

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
        functionAllButton();
        return view;
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
        editCaloriesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog(txtcaloriesTextView, "Edit Calories Burned", "Enter calories burned");
            }
        });

        // Set OnClickListener for Steps Walked Card
        editStepsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog(stepsTextView, "Edit Steps Walked", "Enter steps walked");
            }
        });

        // Set OnClickListener for Water Intake Card
        editWaterIntakeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog(txtWaterTextView, "Edit Water Intake", "Enter water intake (liters)");
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

            // Show the DatePickerDialog
            datePickerDialog.show();
        });


        allTimeStatistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLineChartDialog();
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



        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveStatisticsToFirebase(userId, String.valueOf(DatePicked.getText()),
                        Double.valueOf(txtBMITextView.getText().toString().trim()),
                        txtBMIStatus.getText().toString().trim(),
                        Integer.valueOf(txtcaloriesTextView.getText().toString().trim()),
                        Integer.valueOf((stepsTextView.getText().toString().trim())),
                        Double.valueOf(txtWaterTextView.getText().toString().trim()));
            }
        });



    }



    // Method to save BMI and statistics to Firebase
    private void saveStatisticsToFirebase(String userId, String date, double bmi, String bmiStatus, int calories, int steps, double waterIntake) {
        // Create an object of UserStatistics
        UserStatistics userStatistics = new UserStatistics(date, bmi, bmiStatus, calories, steps, waterIntake);

        // Save the data under the user's node in Firebase
        databaseReference.child(userId).child("statistics").child(date).setValue(userStatistics)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Data saved successfully
                        Toast.makeText(getContext(), "Data saved successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        // Failed to save data
                        Toast.makeText(getContext(), "Failed to save data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to retrieve and display user statistics from Firebase based on the selected date
    private void getUserStatisticsFromFirebase(String userId, String selectedDate) {
        // Create a reference to the user's statistics for the selected date in the database
        DatabaseReference statisticsRef = databaseReference.child(userId).child("statistics").child(selectedDate);

        // Add a listener to retrieve data once for the specific date
        statisticsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get the user statistics for the selected date
                    UserStatistics userStatistics = dataSnapshot.getValue(UserStatistics.class);

                    if (userStatistics != null) {
                        // Update the corresponding TextViews with the fetched data
                        txtBMITextView.setText(String.format("%.2f", userStatistics.getBmi()));
                        txtBMIStatus.setText(userStatistics.getBmiStatus());
                        txtcaloriesTextView.setText(String.valueOf(userStatistics.getCalories()));
                        stepsTextView.setText(String.valueOf(userStatistics.getSteps()));
                        txtWaterTextView.setText(String.format("%.2f", userStatistics.getWaterIntake()));

                        // Update the DatePicked TextView with the selected date
                        DatePicked.setText(selectedDate);
                    }
                } else {
                    // Handle case where no data exists for the selected date
                    displayNoData();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database errors
                Toast.makeText(getContext(), "Error retrieving data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayNoData() {
        // Display "None" in all TextViews when no data is found for the selected date
        txtBMITextView.setText("None");
        txtBMIStatus.setText("None");
        txtcaloriesTextView.setText("None");
        stepsTextView.setText("None");
        txtWaterTextView.setText("None");

        // Optionally, update the DatePicked TextView as well
        DatePicked.setText("No data available");
    }






    private void showLineChartDialog() {
        // Create a dialog using the activity context
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_chart);

        // Initialize LineChart inside the dialog
        LineChart dialogLineChart = dialog.findViewById(R.id.dialogLineChart);

        // Initialize close button and set OnClickListener
        ImageView btnCloseDialog = dialog.findViewById(R.id.btnCloseDialog);
        btnCloseDialog.setOnClickListener(v -> dialog.dismiss());

        // Data for the LineChart
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 2000));  // Jan
        entries.add(new Entry(1, 4000));  // Feb
        entries.add(new Entry(2, 2678));  // Mar
        entries.add(new Entry(3, 5000));  // Apr
        entries.add(new Entry(4, 7000));  // May
        entries.add(new Entry(5, 3000));  // Jun
        entries.add(new Entry(6, 4500));  // Jul
        entries.add(new Entry(7, 3200));  // Aug
        entries.add(new Entry(8, 6000));  // Sep
        entries.add(new Entry(9, 7000));  // Oct

        LineDataSet lineDataSet = new LineDataSet(entries, "Earnings");
        lineDataSet.setColor(getResources().getColor(R.color.blue));
        lineDataSet.setValueTextColor(getResources().getColor(R.color.black));
        lineDataSet.setLineWidth(2f);

        LineData lineData = new LineData(lineDataSet);
        dialogLineChart.setData(lineData);

        // Hide description label
        Description description = new Description();
        description.setText("");
        dialogLineChart.setDescription(description);

        dialogLineChart.invalidate();  // Refresh chart

        // Show the dialog
        dialog.show();
    }



    //Show Input for Calories, Steps and Water
    // Generic method to handle input dialogs for Calories, Steps, and Water Intake
    private void showInputDialog(final TextView targetTextView, String dialogTitle, String hint) {
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
                String newValue = editInput.getText().toString();
                if (!newValue.isEmpty()) {
                    targetTextView.setText(newValue);
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
