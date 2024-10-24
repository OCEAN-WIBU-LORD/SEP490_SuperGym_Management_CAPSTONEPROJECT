package com.example.sep490_supergymmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sep490_supergymmanagement.ExerciseDetailActivity;
import com.example.sep490_supergymmanagement.SelectExerciseActivity;
import com.example.sep490_supergymmanagement.SessionDataHolder;
import com.example.sep490_supergymmanagement.adapters.SelectedExerciseAdapter;
import com.example.sep490_supergymmanagement.models.Exercise;
import com.example.sep490_supergymmanagement.models.Session;
import com.example.sep490_supergymmanagement.models.Set;
import com.example.sep490_supergymmanagement.object.MuscleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddSessionActivity extends AppCompatActivity {

    private EditText sessionNameInput;
    private Button selectMuscleGroupsButton, addExerciseButton, startTimerButton, stopTimerButton, saveSessionButton;
    private TextView timerTextView;
    private RecyclerView exerciseRecyclerView;
    private SelectedExerciseAdapter selectedExerciseAdapter;
    private List<Exercise> selectedExercises;
    private String selectedMuscleGroups = "";
    private String userId;  // Thêm userId để lưu session cho đúng người dùng

    // Timer variables
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;
    private long startTime = 0L, elapsedTime = 0L, pauseTime = 0L;
    private boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_session);

        // Lấy userId từ Firebase Auth
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();  // Lấy userId của người dùng hiện tại
        }

        // Initialize UI components
        sessionNameInput = findViewById(R.id.session_name_input);
        selectMuscleGroupsButton = findViewById(R.id.select_muscle_groups_button);
        addExerciseButton = findViewById(R.id.add_exercise_button);
        startTimerButton = findViewById(R.id.start_timer_button);
        stopTimerButton = findViewById(R.id.stop_timer_button);
        timerTextView = findViewById(R.id.timer_text_view);
        saveSessionButton = findViewById(R.id.save_session_button);
        exerciseRecyclerView = findViewById(R.id.exercise_recycler_view);

        // Initialize the list of selected exercises
        selectedExercises = new ArrayList<>();
        selectedExerciseAdapter = new SelectedExerciseAdapter(selectedExercises, exercise -> {
            Intent intent = new Intent(AddSessionActivity.this, ExerciseDetailActivity.class);
            intent.putExtra("exercise_id", exercise.getId());
            if (SessionDataHolder.exerciseSetsMap.containsKey(exercise.getId())) {
                intent.putExtra("exercise_sets", exercise.getId());
            }
            startActivityForResult(intent, 2);
        });

        exerciseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        exerciseRecyclerView.setAdapter(selectedExerciseAdapter);

        // Start timer button functionality
        startTimerButton.setOnClickListener(v -> startTimer());

        // Stop timer button functionality
        stopTimerButton.setOnClickListener(v -> pauseTimer());

        // Save session button functionality
        saveSessionButton.setOnClickListener(v -> saveSession());

        // Event for selecting muscle groups
        selectMuscleGroupsButton.setOnClickListener(v -> showMuscleGroupDialog());

        // Event for adding exercises
        addExerciseButton.setOnClickListener(v -> {
            Intent intent = new Intent(AddSessionActivity.this, SelectExerciseActivity.class);
            ArrayList<String> selectedExerciseIds = new ArrayList<>();
            for (Exercise exercise : selectedExercises) {
                selectedExerciseIds.add(exercise.getId());
            }
            intent.putStringArrayListExtra("selectedExerciseIds", selectedExerciseIds);
            startActivityForResult(intent, 1);
        });
    }

    // Start the timer
    private void startTimer() {
        if (!isRunning) {
            if (startTime == 0L) {
                startTime = System.currentTimeMillis();  // Record start time
            } else {
                startTime += (System.currentTimeMillis() - pauseTime); // Adjust for paused time
            }

            // Start the timer runnable
            timerRunnable = new Runnable() {
                @Override
                public void run() {
                    elapsedTime = System.currentTimeMillis() - startTime;
                    updateTimerUI(elapsedTime);
                    timerHandler.postDelayed(this, 1000); // Update every second
                }
            };
            timerHandler.post(timerRunnable);
            isRunning = true;
        }
    }

    // Pause the timer
    private void pauseTimer() {
        if (isRunning) {
            timerHandler.removeCallbacks(timerRunnable);
            pauseTime = System.currentTimeMillis();  // Save the current time as pause time
            isRunning = false;
        }
    }

    // Update the UI to show the elapsed time
    private void updateTimerUI(long elapsedMillis) {
        int seconds = (int) (elapsedMillis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        timerTextView.setText(String.format("%02d:%02d", minutes, seconds));
    }

    // Save the session to Firebase
    private void saveSession() {
        if (isRunning) {
            timerHandler.removeCallbacks(timerRunnable); // Stop the timer if it's running
        }

        String sessionName = sessionNameInput.getText().toString().trim();
        if (sessionName.isEmpty()) {
            Toast.makeText(this, "Please enter a session name", Toast.LENGTH_SHORT).show();
            return;
        }

        long endTime = System.currentTimeMillis();  // End time is current time
        long sessionDurationMillis = elapsedTime;   // The total duration

        // Format the start time and end time for display and save
        String startTimeString = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date(startTime));
        String endTimeString = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date(endTime));

        // Get the current date
        String dateString = new java.text.SimpleDateFormat("MMMM").format(new java.util.Date());  // e.g., "January"
        String dayString = new java.text.SimpleDateFormat("dd").format(new java.util.Date());     // e.g., "25"

        // Before saving the session, ensure sets are associated with exercises
        for (Exercise exercise : selectedExercises) {
            if (SessionDataHolder.exerciseSetsMap.containsKey(exercise.getId())) {
                // Get the sets for this exercise and associate them
                List<Set> setsForExercise = SessionDataHolder.exerciseSetsMap.get(exercise.getId());
                exercise.setSets(setsForExercise);
            }
        }

        // Create the session object
        Session session = new Session();
        session.setId(String.valueOf(System.currentTimeMillis()));  // Generate a unique ID
        session.setMonth(dateString);                               // Set the current month
        session.setDay(dayString);                                  // Set the current day
        session.setName(sessionName);                               // Set the session name
        session.setMuscleGroups(selectedMuscleGroups);              // Set the selected muscle groups
        session.setExercises(selectedExercises);                    // Set the exercises with their sets
        session.setStartTime(startTimeString);                      // Set the start time
        session.setEndTime(endTimeString);                          // Set the end time
        session.setUserId(userId);                                  // Gán userId cho session

// Save the session to Firebase under "sessions"
        if (userId != null) {
            // Save session under "sessions" node, with a child node using session ID
            DatabaseReference sessionsRef = FirebaseDatabase.getInstance().getReference("Session");

            // Push the session data with userId
            sessionsRef.child(session.getId()).setValue(session)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Session saved successfully!", Toast.LENGTH_SHORT).show();
                        // Optionally clear the SessionDataHolder after saving
                        SessionDataHolder.exerciseSetsMap.clear();
                        finish();  // Close the activity
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to save session: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Failed to get user ID. Please log in.", Toast.LENGTH_SHORT).show();
        }
}

    // Show muscle group selection dialog
    private void showMuscleGroupDialog() {
        final boolean[] selectedGroups = new boolean[MuscleGroup.getAllMuscleGroups().size()];
        final String[] muscleGroups = MuscleGroup.getAllMuscleGroups().toArray(new String[0]);

        String[] currentSelections = selectMuscleGroupsButton.getText().toString().split(", ");
        for (String selection : currentSelections) {
            for (int i = 0; i < muscleGroups.length; i++) {
                if (muscleGroups[i].equals(selection)) {
                    selectedGroups[i] = true;
                    break;
                }
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Muscle Groups")
                .setMultiChoiceItems(muscleGroups, selectedGroups, (dialog, which, isChecked) -> {
                    selectedGroups[which] = isChecked;
                })
                .setPositiveButton("OK", (dialog, which) -> {
                    StringBuilder selected = new StringBuilder();
                    for (int i = 0; i < selectedGroups.length; i++) {
                        if (selectedGroups[i]) {
                            if (selected.length() > 0) {
                                selected.append(", ");
                            }
                            selected.append(muscleGroups[i]);
                        }
                    }
                    selectedMuscleGroups = selected.toString();
                    selectMuscleGroupsButton.setText(selectedMuscleGroups);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            ArrayList<String> selectedExerciseIds = data.getStringArrayListExtra("selectedExerciseIds");
            if (selectedExerciseIds != null) {
                fetchSelectedExercises(selectedExerciseIds);
            }
        }
    }

    private void fetchSelectedExercises(List<String> selectedExerciseIds) {
        DatabaseReference exercisesRef = FirebaseDatabase.getInstance().getReference("exercises");
        exercisesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Exercise> exercisesToUpdate = new ArrayList<>();
                for (String id : selectedExerciseIds) {
                    Exercise exercise = dataSnapshot.child(id).getValue(Exercise.class);
                    if (exercise != null) {
                        exercisesToUpdate.add(exercise);
                    }
                }
                selectedExercises.clear();
                selectedExercises.addAll(exercisesToUpdate);
                selectedExerciseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database errors
            }
        });
    }
}
