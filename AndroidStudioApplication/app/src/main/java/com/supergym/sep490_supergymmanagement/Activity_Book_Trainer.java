package com.supergym.sep490_supergymmanagement;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
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

import com.bumptech.glide.Glide;
import com.supergym.sep490_supergymmanagement.adapters.UserAdapter;
import com.supergym.sep490_supergymmanagement.models.Booking;
import com.supergym.sep490_supergymmanagement.models.Trainer;
import com.supergym.sep490_supergymmanagement.models.TrainerDTO;
import com.supergym.sep490_supergymmanagement.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Activity_Book_Trainer extends AppCompatActivity {
    private CardView returnBtn;
    private RadioGroup trainerTypeRadioGroup, optionRadioGroup;
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
    private TextView bioTextView, trainerNameTextView;

    private ImageView userAvatarImg;

    CheckBox monday  , tuesday, saturday, wednesday, friday, thursday;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_trainer);

        trainerTypeRadioGroup = findViewById(R.id.trainerTypeRadioGroup);
        optionRadioGroup = findViewById(R.id.optionRadioGroup);
        returnBtn = findViewById(R.id.returnBtn);
        returnBtn.setOnClickListener(v -> onBackPressed());

        trainerSpinner = findViewById(R.id.trainerSpinner);
        userSearchView = findViewById(R.id.userSearchView);
        userRecyclerView = findViewById(R.id.userRecyclerView);
        extraUserEditText = findViewById(R.id.extraUser);

        userAvatarImg = findViewById(R.id.trainerAvartar);
        bioTextView = findViewById(R.id.bioTextView);
        trainerNameTextView = findViewById(R.id.trainerNameTextView);
        functionedRadioBtn();
// Listener for RadioGroup
        loadTrainers("Gym");
        selectAndDisableAll(monday, tuesday, wednesday, thursday, friday, saturday);

        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, user -> extraUserEditText.setText(user.getEmail()));
        userRecyclerView.setAdapter(userAdapter);
        usersRef = FirebaseDatabase.getInstance().getReference("users");





        userSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.isEmpty()) {
                    Toast.makeText(Activity_Book_Trainer.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                } else {
                    searchUsers(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty()) {
                    searchUsers(newText);
                }
                return false;
            }

        });

        loadTimeSlots();
        setupSubmitButton();
    }


    private void functionedRadioBtn(){
         monday = findViewById(R.id.checkboxMonday);
         tuesday = findViewById(R.id.checkboxTuesday);
         wednesday = findViewById(R.id.checkboxWednesday);
         thursday = findViewById(R.id.checkboxThursday);
         friday = findViewById(R.id.checkboxFriday);
         saturday = findViewById(R.id.checkboxSaturday);

// Hide the Option RadioGroup initially
        optionRadioGroup.setVisibility(View.GONE);

// Listener for the main RadioGroup
        trainerTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioBoxing) {
                    // Show options and reset checkboxes
                    loadTrainers("Boxing");
                    optionRadioGroup.setVisibility(View.VISIBLE);
                    resetCheckboxes();

                }else if (checkedId == R.id.radioGym) {
                    // Show options and reset checkboxes
                    loadTrainers("Gym");
                    optionRadioGroup.setVisibility(View.GONE);

                    resetCheckboxes();
                    selectAndDisableAll(monday, tuesday, wednesday, thursday, friday, saturday);

                } else if(checkedId == R.id.radioYoga){
                    loadTrainers("Yoga");
                }else if(checkedId == R.id.radioPilates){
                    loadTrainers("Pilates");
                }

                else {
                    // Hide options and reset checkboxes for other trainer types
                    optionRadioGroup.setVisibility(View.GONE);
                    resetCheckboxes();
                }
            }
        });

// Listener for the Option RadioGroup
        optionRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                resetCheckboxes(); // Reset all checkboxes before applying the new selection

                if (checkedId == R.id.option1) {
                    // Automatically check Monday, Wednesday, Friday
                    monday.setChecked(true);
                    wednesday.setChecked(true);
                    friday.setChecked(true);
                } else if (checkedId == R.id.option2) {
                    // Automatically check Tuesday, Thursday, Saturday
                    tuesday.setChecked(true);
                    thursday.setChecked(true);
                    saturday.setChecked(true);
                }
            }
        });

// Helper function to reset all checkboxes
        Spinner trainerSpinner = findViewById(R.id.trainerSpinner);
        ImageView dropdownBtn = findViewById(R.id.dropdownBtn);

        dropdownBtn.setOnClickListener(v -> trainerSpinner.performClick());
        trainerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (trainerIds.isEmpty()) {
                    Toast.makeText(Activity_Book_Trainer.this, "No trainers available", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get the selected trainer's ID
                String selectedTrainerId = trainerIds.get(position);

                // Load trainer details based on the selected ID
                loadTrainerDetails(selectedTrainerId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: Handle case where no selection is made
            }
        });


    }

    // Function to reset all CheckBoxes
    private void resetCheckboxes() {
        monday.setChecked(false);
        tuesday.setChecked(false);
        wednesday.setChecked(false);
        thursday.setChecked(false);
        friday.setChecked(false);
        saturday.setChecked(false);
    }

    // Function to select and disable all CheckBoxes
    private void selectAndDisableAll(CheckBox... checkBoxes) {
        for (CheckBox checkBox : checkBoxes) {
            checkBox.setChecked(true);
            checkBox.setEnabled(false);
        }
    }

    // Function to enable specific combinations
    private void enableCombination(CheckBox... combination) {
        for (CheckBox checkBox : combination) {
            checkBox.setEnabled(true);
        }
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

    private void loadTrainerDetails(String trainerId) {
        // Getting references for Trainers and Users
        DatabaseReference trainersRef = FirebaseDatabase.getInstance().getReference("Trainers").child(trainerId);
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Fetching trainer details from the "Trainers" node
        trainersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot trainerSnapshot) {
                // Converting the snapshot into a Trainer object
                Trainer trainer = trainerSnapshot.getValue(Trainer.class);

                if (trainer != null) {
                    // Getting the userId from the trainer object
                    String userId = trainer.getUserId();

                    // Fetching the user details using the userId
                    usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                            // Converting the user snapshot into a User object
                            User user = userSnapshot.getValue(User.class);

                            if (user != null) {
                                // Extracting user name and trainer bio
                                String userName = trainer.getName();
                                if(userName != null){
                                    trainerNameTextView.setText("Trainer Name: " + userName);
                                }else{
                                    trainerNameTextView.setText("null");
                                }
                                String trainerBio = trainer.getSpecialization(); // Assuming specialization is the
                                if (trainerBio != null){
                                    bioTextView.setText(trainerBio.trim());
                                }else{
                                    bioTextView.setText("null");
                                }

                                loadTrainerDetail(userId);

                                /*// Now create a TrainerDTO object with the fetched data
                                TrainerDTO trainerDTO = new TrainerDTO(userName,  avatarBitmap, trainerBio);

                                // Populate the trainer details in the UI
                                populateTrainerDetails(trainerDTO);*/
                            } else {
                                Toast.makeText(Activity_Book_Trainer.this, "User details not found.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle error when fetching user details
                            Toast.makeText(Activity_Book_Trainer.this, "Failed to load user details.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(Activity_Book_Trainer.this, "Trainer details not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error when fetching trainer details
                Toast.makeText(Activity_Book_Trainer.this, "Failed to load trainer details.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadTrainerDetail(String userId){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String avatarBase64 = dataSnapshot.child("userAvatar").getValue(String.class);
                    if (avatarBase64 != null) {
                        try {
                            Bitmap avatarBitmap = base64ToBitmap(avatarBase64);
                            userAvatarImg.setImageBitmap(avatarBitmap);
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                    }




                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }


    public Bitmap base64ToBitmap(String base64String) {
        try {
            byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null; // Return null if decoding fails
        }
    }



    /*private void populateTrainerDetails(TrainerDTO trainerDTO) {
        TextView trainerNameTextView = findViewById(R.id.trainerNameTextView);
        TextView bioTextView = findViewById(R.id.bioTextView);
        ImageView avatarImageView = findViewById(R.id.trainerAvartar);

        // Set trainer name and specialization (bio)
        trainerNameTextView.setText("Trainer Name: " + trainerDTO.getName());
        bioTextView.setText("Specialization: " + trainerDTO.getBio());

        // Load avatar image (if avatar is a Bitmap, set it directly to ImageView)
        String avatarBitmap = trainerDTO.getAvatar();
        if (avatarBitmap != null) {
            avatarImageView.setImageBitmap(avatarBitmap); // Directly set the Bitmap to ImageView
        } else {
            // If avatar is not available, set a placeholder image
            avatarImageView.setImageResource(R.drawable.ic_placeholder);
        }
    }*/



    private void searchUsers(String emailQuery) {
        usersRef.orderByChild("email").startAt(emailQuery).endAt(emailQuery + "\uf8ff")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userList.clear();
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            User user = userSnapshot.getValue(User.class);
                            if (user != null && user.getEmail() != null) {
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
