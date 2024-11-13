package com.example.sep490_supergymmanagement;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sep490_supergymmanagement.adapters.UserAdapter;
import com.example.sep490_supergymmanagement.models.Booking;
import com.example.sep490_supergymmanagement.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Activity_Book_Trainer extends AppCompatActivity {
    private CardView returnBtn;
    private RadioGroup trainerTypeRadioGroup;
    private Spinner trainerSpinner;
    private ArrayList<String> trainerNames = new ArrayList<>(); // To hold trainer names for the Spinner
    private ArrayList<String> timeSlots = new ArrayList<>();

    private DatabaseReference usersRef;
    private List<User> userList;
    private UserAdapter userAdapter;
    private RecyclerView userRecyclerView;
    private EditText extraUserEditText;
    private SearchView userSearchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_trainer);

        // Initialize RadioGroup
        trainerTypeRadioGroup = findViewById(R.id.trainerTypeRadioGroup); // Ensure the RadioGroup ID is correct

        returnBtn = findViewById(R.id.returnBtn);
        returnBtn.setOnClickListener(v -> onBackPressed());

        trainerSpinner = findViewById(R.id.trainerSpinner);
        userSearchView = findViewById(R.id.userSearchView);
        userRecyclerView = findViewById(R.id.userRecyclerView);
        extraUserEditText = findViewById(R.id.extraUser);

        // Set listener for RadioGroup
        trainerTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioGym) {
                loadTrainers("Gym");
            } else if (checkedId == R.id.radioBoxing) {
                loadTrainers("Boxing");
            } else if (checkedId == R.id.radioYoga) { // Fixed condition for Yoga
                loadTrainers("Yoga");
            } else if (checkedId == R.id.radioPilates) { // Fixed condition for Pilates
                loadTrainers("Pilates");
            }
        });
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, user -> {
            // Set selected user email in EditText
            extraUserEditText.setText(user.getEmail());
        });
        userRecyclerView.setAdapter(userAdapter);

        usersRef = FirebaseDatabase.getInstance().getReference("users");

        userSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchUsers(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchUsers(newText);
                return false;
            }
        });

        loadTimeSlots();
        setupSubmitButton();
    }

    private void loadTrainers(String trainerType) {
        trainerNames.clear(); // Clear the existing list to avoid duplicates
        DatabaseReference trainersRef = FirebaseDatabase.getInstance().getReference("Trainers");

        trainersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot trainerSnapshot : snapshot.getChildren()) {
                    Boolean isTrainerGym = trainerSnapshot.child("isTrainerGym").getValue(Boolean.class);
                    Boolean isTrainerBoxing = trainerSnapshot.child("isTrainerBoxing").getValue(Boolean.class);
                    Boolean isTrainerYoga = trainerSnapshot.child("isTrainerYoga").getValue(Boolean.class);
                    Boolean isTrainerPilates = trainerSnapshot.child("isTrainerPilates").getValue(Boolean.class);
                    String trainerName = trainerSnapshot.child("name").getValue(String.class);

                    // Filter based on selected trainer type
                    if ("Gym".equals(trainerType) && Boolean.TRUE.equals(isTrainerGym)) {
                        trainerNames.add(trainerName);
                    } else if ("Boxing".equals(trainerType) && Boolean.TRUE.equals(isTrainerBoxing)) {
                        trainerNames.add(trainerName);
                    } else if ("Yoga".equals(trainerType) && Boolean.TRUE.equals(isTrainerYoga)) {
                        trainerNames.add(trainerName);
                    } else if ("Pilates".equals(trainerType) && Boolean.TRUE.equals(isTrainerPilates)) {
                        trainerNames.add(trainerName);
                    }
                }

                if (trainerNames.isEmpty()) {
                    // Show a Toast message if no trainers are available
                    Toast.makeText(Activity_Book_Trainer.this, "No classes available for " + trainerType, Toast.LENGTH_SHORT).show();
                } else {
                    // Update Spinner with the filtered list if trainers are available
                    updateSpinner();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Activity_Book_Trainer.this, "Failed to load trainers.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void searchUsers(String emailQuery) {
        usersRef.orderByChild("email").startAt(emailQuery).endAt(emailQuery + "\uf8ff")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userList.clear();
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            User user = userSnapshot.getValue(User.class);
                            if (user != null) {
                                userList.add(user);
                            }
                        }
                        userAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Activity_Book_Trainer.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadTimeSlots() {
        DatabaseReference timeSlotRef = FirebaseDatabase.getInstance().getReference("TimeSlot");
        timeSlotRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot timeSlotSnapshot : snapshot.getChildren()) {
                    String timeSlot = timeSlotSnapshot.child("timeSlot").getValue(String.class);
                    timeSlots.add(timeSlot);
                }
                setupSeekBar();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Activity_Book_Trainer.this, "Failed to load time slots.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSeekBar() {
        SeekBar sessionTimeSeekBar = findViewById(R.id.sessionTimeSeekBar);
        sessionTimeSeekBar.setMax(timeSlots.size() - 1); // Set max to number of time slots - 1

        // Display the selected time slot in a TextView when SeekBar is adjusted
        TextView selectedTimeSlotText = findViewById(R.id.selectedTimeSlotText);
        sessionTimeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                selectedTimeSlotText.setText(timeSlots.get(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }



    private void updateSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, trainerNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        trainerSpinner.setAdapter(adapter);
    }

    private void setupSubmitButton() {
        View submitButton = findViewById(R.id.submit_button); // Ensure you have this ID set in XML layout
        if (submitButton != null) {
            submitButton.setOnClickListener(v -> {
                Toast.makeText(Activity_Book_Trainer.this, "Request Submitted", Toast.LENGTH_SHORT).show();
                saveBookingDetails();
            });
        }
    }

    private void saveBookingDetails() {
        // Get the selected trainer from the spinner
        String selectedTrainer = trainerSpinner.getSelectedItem().toString();

        // Get the selected trainer type from the RadioGroup
        String trainerType = trainerTypeRadioGroup.getCheckedRadioButtonId() == R.id.radioGym ? "Gym" :
                (trainerTypeRadioGroup.getCheckedRadioButtonId() == R.id.radioBoxing ? "Boxing" :
                        (trainerTypeRadioGroup.getCheckedRadioButtonId() == R.id.radioYoga ? "Yoga" : "Pilates"));

        // Get the selected days from the checkboxes (Monday to Friday)
        StringBuilder selectedDays = new StringBuilder();
        GridLayout dayGridLayout = findViewById(R.id.dayGridLayout); // Ensure the GridLayout ID is correct
        for (int i = 0; i < dayGridLayout.getChildCount(); i++) {
            View dayView = dayGridLayout.getChildAt(i);
            if (dayView instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) dayView;
                if (checkBox.isChecked()) {
                    selectedDays.append(checkBox.getText().toString()).append(", ");
                }
            }
        }
        // Remove the trailing comma and space
        if (selectedDays.length() > 0) {
            selectedDays.setLength(selectedDays.length() - 2);
        }

        // Get the selected time slot from the SeekBar
        SeekBar sessionTimeSeekBar = findViewById(R.id.sessionTimeSeekBar); // Ensure SeekBar ID is correct
        String selectedTimeSlot = timeSlots.get(sessionTimeSeekBar.getProgress()); // Map SeekBar progress to time slot

        // Get the notes entered by the user
        EditText notesEditText = findViewById(R.id.notesEditText); // Ensure EditText ID is correct
        String notes = notesEditText.getText().toString();

        // Create a new booking object with all the collected details
        Booking booking = new Booking(selectedTrainer, trainerType, selectedDays.toString(), selectedTimeSlot, notes);

        // Save the booking details to Firebase
        DatabaseReference bookingsRef = FirebaseDatabase.getInstance().getReference("Bookings");
        String bookingId = bookingsRef.push().getKey();
        bookingsRef.child(bookingId).setValue(booking)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Booking details saved successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save booking", Toast.LENGTH_SHORT).show());
    }

}
