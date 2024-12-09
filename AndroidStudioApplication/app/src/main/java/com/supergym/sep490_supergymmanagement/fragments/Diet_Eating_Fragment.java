package com.supergym.sep490_supergymmanagement.fragments;


import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RotateDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.supergym.sep490_supergymmanagement.FragmentUserProfile;
import com.supergym.sep490_supergymmanagement.R;
import com.supergym.sep490_supergymmanagement.adapters.MealAdapter;
import com.supergym.sep490_supergymmanagement.animations.CupOfWaterView;
import com.supergym.sep490_supergymmanagement.models.Meal;
import com.supergym.sep490_supergymmanagement.models.UserStatistics;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Diet_Eating_Fragment extends Fragment {

    FirebaseAuth mAuth;
    // Firebase Database reference
    private DatabaseReference databaseReference;

    private Button saveBtn, clearButton;

    // Views
    private CardView selectDateButton;
    private LinearLayout advanceInfoContentLayout;
    private CardView btnReturnBtn,  previousDateCard, futureDateCard;
    private TextView txtcaloriesTextView;


    private RecyclerView mealsRecyclerView;
    private MealAdapter mealAdapter;

    // Get the current year, month, and day
    // Set up the Calendar instance for the current date
    private Calendar calendar ;

    private int year, month , day ;
     private String userId,  selectedDate, datePicked;
    private SimpleDateFormat dateFormat;
    private Animation cardClickAnimation;

    private FloatingActionButton addMealButton;
    private ArrayList<Meal> mealList;
    CupOfWaterView cupOfWaterView;
    ProgressBar progressBar;


    private ProgressBar progressBarCalories;
    private TextView tvCalories, txtTotalMeals, txtBurnedCalories;
    private int maxCalories = 3000; // Maximum calorie intake (set for the progress bar)

    private Handler handler = new Handler();  // Declare the handler globally
    private Runnable progressRunnable;  // Declare the runnable globally
    private boolean isProgressRunning = false; // To check if progress is already running


    private Button dateButton;
    int i =1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_diet, container, false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        // Initialize Firebase Database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("users");


        btnReturnBtn = view.findViewById(R.id.btnReturnBtn);
        btnReturnBtn.setOnClickListener(v -> replaceFragment(new FragmentUserProfile()));


        // Initialize RecyclerView
        mealsRecyclerView = view.findViewById(R.id.mealsRecyclerView);
        mealsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        addMealButton = view.findViewById(R.id.addMealButton);
        // Define the meal list
        List<String> meals = Arrays.asList("Breakfast", "Lunch", "Dinner", "Snack");


        // Set the adapter to the RecyclerView
        mealsRecyclerView.setAdapter(mealAdapter);

        // Make sure mealList is initialized before adding meals
        mealList = new ArrayList<>();
        mealAdapter = new MealAdapter(mealList);
        mealsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mealsRecyclerView.setAdapter(mealAdapter);

        /*progressBar = view.findViewById(R.id.progressBar);
        progressBar.setProgress(2136);
        progressBar.setIndeterminate(false); // Ensure indeterminate is disabled
        progressBar.setRotation(270);  // To start at the top instead of default*/
        calendar = Calendar.getInstance();
        dateButton = view.findViewById(R.id.dateButton);
        txtcaloriesTextView = view.findViewById(R.id.tvTotalCalories);
        txtBurnedCalories = view.findViewById(R.id.txtBurnedCalories);
        //Calories Text
        txtTotalMeals = view.findViewById(R.id.txtTotalMeals);


        // Initialize views
        progressBarCalories = view.findViewById(R.id.progressBarCalories);
        tvCalories = view.findViewById(R.id.tvCalories);





        // Sample calorie intake (this should be dynamically retrieved from your logic)
        int totalCalories = getTotalCaloriesForTheDay();

        // Update the progress bar and text view
        //updateCaloriesProgress(totalCalories);


        // Set the current date as default on the button when the fragment loads
        updateDateButtonText();
        previousDateCard = view.findViewById(R.id.previousDate);
        futureDateCard = view.findViewById(R.id.futureDate);

        loadMealsForDate(dateButton.getText().toString().trim());
        allFunctionForDiet();


        return view;
    }

        private void allFunctionForDiet(){
        // Choosing Date Button
            dateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DatePickerDialog(getActivity(), dateSetListener,
                            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });

            // When previousDate CardView is clicked, move to previous day
            previousDateCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                    updateDateButtonText();
                    loadMealsForDate(dateButton.getText().toString().trim());
                }
            });

            // When futureDate CardView is clicked, move to next day
            futureDateCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    updateDateButtonText();
                    loadMealsForDate(dateButton.getText().toString().trim());

                }
            });

            // Set up click listener for the FloatingActionButton
            // Handle addMealButton click
            addMealButton.setOnClickListener(v -> showAddMealDialog());



        }
    // Update the button text to show the selected date
    private void updateDateButtonText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d MMM", Locale.getDefault());
        String dateString = dateFormat.format(calendar.getTime());
        dateButton.setText(dateString);
    }


    private void drawProgressBar(int totalCalories) {
        // If the progress is already running, stop it before starting a new one
        if (isProgressRunning) {
            stopProgressBar();  // Stop the current drawing if it is already running
        }


        // Access the ProgressBar's progressDrawable
        Drawable progressDrawable = progressBarCalories.getProgressDrawable();
        if (progressDrawable != null) {
            progressDrawable.mutate();  // Ensure the drawable is mutable for updates

            if (totalCalories > 3000) {
                // Change the progress bar color to red
                progressDrawable.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            } else {
                // Reset to default color
                progressDrawable.clearColorFilter();
            }
        }



        clearProgressBar();  // Ensure everything is reset before starting
        isProgressRunning = true;  // Set the flag to indicate that progress is running

        // Initialize the new Runnable
        progressRunnable = new Runnable() {
            @Override
            public void run() {
                if (i <= totalCalories && totalCalories <= 2000) {
                    txtcaloriesTextView.setText("" + i + " Kcal");
                    tvCalories.setText("" + i + " Kcal / 3000 Kcal max for Normal People");
                    tvCalories.setTextColor(Color.parseColor("#44FF88"));  // Custom red color
                    progressBarCalories.setProgress((i * 100) / 3000);  // Assuming 3000 is the max value for progress
                    i = i + 10;
                    handler.postDelayed(this, 2);  // Keep updating progress every 2ms
                } else if (i <= totalCalories &&  totalCalories > 2000 && totalCalories <= 3000) {
                    txtcaloriesTextView.setText("" + i + " Kcal");
                    tvCalories.setText("" + i + " Kcal / 3000 Kcal max for Normal People");
                    tvCalories.setTextColor(Color.parseColor("#FFFFFF00"));  // Custom red color
                    progressBarCalories.setProgress((i * 100) / 3000);  // Assuming 3000 is the max value for progress
                    i = i + 20;
                    handler.postDelayed(this, 2);  // Keep updating progress every 2ms
                }  else if(i <= totalCalories && totalCalories > 3000){
                    txtcaloriesTextView.setText("" + i + " Kcal");
                    tvCalories.setText("" + i + " Kcal / 3000 Kcal \n"+"You have eating too much for this day!");
                    tvCalories.setTextColor(Color.parseColor("#FF0000"));  // Custom red color

                    progressBarCalories.setProgress((i * 1000) / 3000);  // Assuming 3000 is the max value for progress
                    i = i + 100;
                    handler.postDelayed(this, 50);  // Keep updating progress every 2ms
                 } else if(i <= totalCalories && totalCalories > 10000){
                    txtcaloriesTextView.setText("" + i + " Kcal");
                    tvCalories.setText("" + i + " Kcal / 3000 Kcal \n"+"You have eating too much for this day!");
                    progressBarCalories.setProgress((i * 1000) / 3000);  // Assuming 3000 is the max value for progress
                    i = i + 300;
                    handler.postDelayed(this, 1);  // Keep updating progress every 2ms
                }
                else {
                    handler.removeCallbacks(this);  // Stop the handler once the target is reached
                    isProgressRunning = false;  // Reset the flag
                }
            }
        };

        // Start the new progress drawing
        handler.postDelayed(progressRunnable, 2);
    }

    private void clearProgressBar() {
        // Reset the UI elements
        txtcaloriesTextView.setText("" + 0 + " Kcal");
        tvCalories.setText("" + 0 + " Kcal / 3000 Kcal max for Normal People");
        progressBarCalories.setProgress(0);

        // Reset the progress counter
        i = 0;
    }

    // Function to stop the progress bar instantly
    private void stopProgressBar() {
        // If progress is running, stop it
        if (isProgressRunning) {
            handler.removeCallbacks(progressRunnable);  // Stop any pending updates
            isProgressRunning = false;  // Reset the flag
        }
    }

    public void changeColorProgress(int totalCalories){
        // Get the drawable (circle_shape.xml) and cast it to GradientDrawable
        // You can use this method to get the drawable safely
        GradientDrawable drawable = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.circle_shape);


        // Check if totalCalories is greater than 3000 and change color to Red
        if (totalCalories > 3000) {
            drawable.setColor(Color.RED); // Change to red if calories exceed 3000
        } else {
            drawable.setColor(Color.parseColor("#009688")); // Set back to original color (greenish)
        }
    }



    // Show dialog to input meal details
    // Show a dialog for user input
    private void showAddMealDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_meal, null);

        // Find the views inside dialog
        Spinner mealTypeSpinner = dialogView.findViewById(R.id.mealTypeSpinner);
        EditText foodDescriptionEditText = dialogView.findViewById(R.id.foodDescriptionEditText);
        EditText foodCaloriesText = dialogView.findViewById(R.id.foodCalories);

        // Validate that the Spinner and EditText exist
        if (mealTypeSpinner == null || foodDescriptionEditText == null || foodCaloriesText == null) {
            Log.e("AddMealDialog", "MealTypeSpinner, FoodDescriptionEditText, or FoodCalories is null!");
            return;
        }

        builder.setView(dialogView)
                .setTitle("Add New Meal")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Validate inputs
                        String mealType = mealTypeSpinner.getSelectedItem().toString();
                        String foodDescription = foodDescriptionEditText.getText().toString().trim();
                        String foodCaloriesInput = foodCaloriesText.getText().toString().trim();
                        String currentDate = dateButton.getText().toString().trim();

                        if (foodDescription.isEmpty()) {
                            Toast.makeText(getContext(), "Please enter a description.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (foodCaloriesInput.isEmpty()) {
                            Toast.makeText(getContext(), "Please enter the number of calories.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Integer foodCalories;
                        try {
                            foodCalories = Integer.parseInt(foodCaloriesInput);
                        } catch (NumberFormatException e) {
                            Toast.makeText(getContext(), "Calories must be a valid number.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Proceed if all inputs are valid
                        addMealToRecyclerView(currentDate, mealType, foodDescription, foodCalories);
                        Meal newMeal = new Meal(currentDate, mealType, foodDescription, foodCalories);
                        addMealToFirebase(newMeal);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }




    // Add the new meal to RecyclerView and update the adapter
    private void addMealToRecyclerView(String date, String mealType, String foodDescription, Integer foodCalories) {
        Meal newMeal = new Meal(date, mealType, foodDescription, foodCalories);
        mealList.add(newMeal);  // Add the new meal to the list
        mealAdapter.notifyDataSetChanged();  // Notify the adapter to refresh the RecyclerView
    }


    public int calculateTotalCalories(List<Meal> meals, String targetDate) {
        int totalCalories = 0;

        for (Meal meal : meals) {
            // Check if the meal's date matches the target date
            if (meal.getDate().equals(targetDate)) {
                totalCalories += meal.getCalories(); // Add the meal's calories
            }
        }

        return totalCalories;
    }


    private void addMealToFirebase(Meal meal) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        // Create a reference to the "meals" table in Firebase, without nesting it under the "users" node
        DatabaseReference mealsRef = FirebaseDatabase.getInstance()
                .getReference("meals")
                .child(userId)  // You can still store meals by user ID if needed
                .child(meal.getDate());

        // Push a new meal under the selected date
        mealsRef.push().setValue(meal)
                .addOnSuccessListener(aVoid -> {
                    // Meal added successfully
                    Toast.makeText(getContext(), "Meal added!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Error adding meal
                    Toast.makeText(getContext(), "Failed to add meal.", Toast.LENGTH_SHORT).show();
                });
    }


    private void loadMealsForDate(String selectedDate) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        // Reference to the "meals" table for the selected date
        DatabaseReference mealsRef = FirebaseDatabase.getInstance()
                .getReference("meals")
                .child(userId)
                .child(selectedDate);

        mealsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Meal> mealList = new ArrayList<>();
                int totalCalories = 0; // Initialize total calories counter
                int totalMealCount = 0;
                for (DataSnapshot mealSnapshot : dataSnapshot.getChildren()) {
                    Meal meal = mealSnapshot.getValue(Meal.class);
                    mealList.add(meal);
                    totalCalories += meal.getCalories(); // Sum up the calories
                    totalMealCount++;
                }

                // Update the meals in the adapter
                mealAdapter.updateMeals(mealList);

                // Update UI elements with meal data
                txtTotalMeals.setText("Total \n" + String.valueOf(totalMealCount) + "\n Meals");
                txtBurnedCalories.setText("Total \n" + String.valueOf(totalMealCount) + " Kcal" + "\n Burned");

                drawProgressBar(totalCalories);
                changeColorProgress(totalCalories);

                // If no meals exist, the adapter will show "No Meal Available"
                if (mealList.isEmpty()) {
                    Toast.makeText(getContext(), "No meals available for this date.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load meals.", Toast.LENGTH_SHORT).show();
            }
        });
    }



    // Method to get total calories for the day (replace this with actual calculation)
    private int getTotalCaloriesForTheDay() {
        // Example total calories for demonstration. This should be the calculated value.
        return 1750; // Replace this with actual logic to sum calories for the day
    }

    // Method to update progress bar and text view
    private void updateCaloriesProgress(int totalCalories) {
        progressBarCalories.setProgress(totalCalories);

        // Update the TextView with the total calorie count
        String calorieText = "Total Calories: " + totalCalories + " / " + maxCalories + " kcal";
        tvCalories.setText(calorieText);
    }





    // DateSetListener to handle the selected date
    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateButtonText();
        }
    };




    // Replaces the current fragment with another fragment
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment); // Ensure R.id.frame_layout exists in the parent activity
        fragmentTransaction.commit();
    }
}
