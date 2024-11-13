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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Activity_Book_Trainer extends AppCompatActivity {
    private CardView returnBtn;
    private RadioGroup trainerTypeRadioGroup;
    private Spinner trainerSpinner;
    private ArrayList<String> trainerNames = new ArrayList<>();
    private ArrayList<String> timeSlots = new ArrayList<>();
    private List<String> trainerIds = new ArrayList<>(); // Lưu trainer IDs tương ứng

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

        trainerTypeRadioGroup = findViewById(R.id.trainerTypeRadioGroup);
        returnBtn = findViewById(R.id.returnBtn);
        returnBtn.setOnClickListener(v -> onBackPressed());

        trainerSpinner = findViewById(R.id.trainerSpinner);
        userSearchView = findViewById(R.id.userSearchView);
        userRecyclerView = findViewById(R.id.userRecyclerView);
        extraUserEditText = findViewById(R.id.extraUser);

        trainerTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioGym) {
                loadTrainers("Gym");
            } else if (checkedId == R.id.radioBoxing) {
                loadTrainers("Boxing");
            } else if (checkedId == R.id.radioYoga) {
                loadTrainers("Yoga");
            } else if (checkedId == R.id.radioPilates) {
                loadTrainers("Pilates");
            }
        });

        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, user -> extraUserEditText.setText(user.getEmail()));
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
        trainerNames.clear();
        trainerIds.clear();
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
                    String trainerId = trainerSnapshot.getKey(); // Lấy PT ID

                    if ("Gym".equals(trainerType) && Boolean.TRUE.equals(isTrainerGym)) {
                        trainerNames.add(trainerName);
                        trainerIds.add(trainerId);
                    } else if ("Boxing".equals(trainerType) && Boolean.TRUE.equals(isTrainerBoxing)) {
                        trainerNames.add(trainerName);
                        trainerIds.add(trainerId);
                    } else if ("Yoga".equals(trainerType) && Boolean.TRUE.equals(isTrainerYoga)) {
                        trainerNames.add(trainerName);
                        trainerIds.add(trainerId);
                    } else if ("Pilates".equals(trainerType) && Boolean.TRUE.equals(isTrainerPilates)) {
                        trainerNames.add(trainerName);
                        trainerIds.add(trainerId);
                    }
                }

                if (trainerNames.isEmpty()) {
                    Toast.makeText(Activity_Book_Trainer.this, "No classes available for " + trainerType, Toast.LENGTH_SHORT).show();
                } else {
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
        sessionTimeSeekBar.setMax(timeSlots.size() - 1);

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
        View submitButton = findViewById(R.id.submit_button);
        if (submitButton != null) {
            submitButton.setOnClickListener(v -> {
                Toast.makeText(Activity_Book_Trainer.this, "Request Submitted", Toast.LENGTH_SHORT).show();
                saveBookingDetails();
            });
        }
    }

    private void saveBookingDetails() {
        // Lấy `customerId` từ thông tin đăng nhập hiện tại
        String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Lấy `ptId` từ trainer đã chọn
        int selectedTrainerIndex = trainerSpinner.getSelectedItemPosition();
        String ptId = trainerIds.get(selectedTrainerIndex); // Lấy `ptId` từ vị trí của trainer

        // Tên PT từ trainer đã chọn
        String trainerName = trainerSpinner.getSelectedItem().toString();

        // Lấy loại hình huấn luyện từ RadioGroup
        String trainerType = trainerTypeRadioGroup.getCheckedRadioButtonId() == R.id.radioGym ? "Gym" :
                (trainerTypeRadioGroup.getCheckedRadioButtonId() == R.id.radioBoxing ? "Boxing" :
                        (trainerTypeRadioGroup.getCheckedRadioButtonId() == R.id.radioYoga ? "Yoga" : "Pilates"));

        // Lấy time slot từ SeekBar
        SeekBar sessionTimeSeekBar = findViewById(R.id.sessionTimeSeekBar);
        String selectedTimeSlot = timeSlots.get(sessionTimeSeekBar.getProgress());
        String timeSlotId = sessionTimeSeekBar.getProgress() + ""; // ID khung giờ dựa trên vị trí của SeekBar

        // Lấy ngày bắt đầu từ ngày hiện tại + 1
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        String startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());

        // Lấy số buổi tập từ EditText
        EditText sessionCountEditText = findViewById(R.id.sessionCountEditText);
        int sessionCount;
        try {
            sessionCount = Integer.parseInt(sessionCountEditText.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number of sessions", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy ghi chú
        EditText notesEditText = findViewById(R.id.notesEditText);
        String notes = notesEditText.getText().toString();

        // Tạo đối tượng Booking với các thông tin đã thu thập
        Booking booking = new Booking(customerId, ptId, startDate, sessionCount, timeSlotId, trainerName, trainerType,
                selectedTimeSlot, notes);

        // Lưu vào Firebase
        DatabaseReference bookingsRef = FirebaseDatabase.getInstance().getReference("Bookings");
        String bookingId = bookingsRef.push().getKey();
        bookingsRef.child(bookingId).setValue(booking)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Booking details saved successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save booking", Toast.LENGTH_SHORT).show());
    }


}
