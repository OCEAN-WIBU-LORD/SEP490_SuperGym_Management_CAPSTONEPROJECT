package com.supergym.sep490_supergymmanagement;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.supergym.sep490_supergymmanagement.models.Trainer;
import com.supergym.sep490_supergymmanagement.repositories.TrainerResp;
import com.supergym.sep490_supergymmanagement.repositories.callbacks.Callback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrainerEditBioActivity extends AppCompatActivity {

    // Declare UI components
    private EditText editBio, editSpecialization;
    private ImageView userAvatarImg, button3;
    private Button saveBtn, cancelBtn;
    private ProgressBar progressBar;
    private TrainerResp trainerResp = new TrainerResp();
    private String userIdFinal;
    private CardView returnCardView;
    private String trainerId, specialization, bio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trainer_profile);

        // Initialize UI components
         editBio = findViewById(R.id.editBio);
         returnCardView = findViewById(R.id.returnCardView);
        userAvatarImg = findViewById(R.id.userAvatarImg);
        returnCardView.setOnClickListener(view -> onBackPressed());
        loadUserInfor();
        editSpecialization = findViewById(R.id.editSpecialization);

         trainerId = getIntent().getStringExtra("trainerId");

        // Fetch trainer details
        if (trainerId != null) {
            Log.d("TrainerId", "Received Trainer ID: " + trainerId);
            fetchTrainerDetails(trainerId);
        } else {
            Toast.makeText(this, "Trainer ID is null!", Toast.LENGTH_SHORT).show();
        }
        saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(view -> saveTrainerProfile());
        progressBar = findViewById(R.id.progressBar);
        cancelBtn = findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(view -> cancelEditing());

      /*  returnCardView = findViewById(R.id.returnCardView);
        button3 = findViewById(R.id.button3);




        // Set up button actions



        // Handle return button
        returnCardView.setOnClickListener(view -> finish());

        // Additional actions for other components
        button3.setOnClickListener(view -> showMessage("Mail icon clicked!"));*/
    }

    private void fetchTrainerDetails(String trainerId) {
        trainerResp.getTrainerById(trainerId, new Callback<Trainer>() {
            @Override
            public void onCallback(List<Trainer> trainers) {
                if (!trainers.isEmpty()) {
                    Trainer trainer = trainers.get(0); // There will be only one trainer
                    displayTrainerDetails(trainer);
                    // Fetch and display the avatar using userId
                } else {
                    Toast.makeText(TrainerEditBioActivity.this, "Trainer not found!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void displayTrainerDetails(Trainer trainer) {
        editSpecialization.setText(trainer.getSpecialization());
        editBio.setText(trainer.getBio());
    }

    private void loadUserInfor() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
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
                                // Handle the case where the Base64 string could not be decoded
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
    }
    public Bitmap base64ToBitmap(String base64Str) throws IllegalArgumentException {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }




    /**
     * Save trainer profile details.
     */
    private void saveTrainerProfile() {
        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Get user input
         bio = editBio.getText().toString().trim();
         specialization = editSpecialization.getText().toString().trim();

        // Validate input
        if (bio.isEmpty() || specialization.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Please fill in all the details!", Toast.LENGTH_SHORT).show();
            return;
        }
        updateTrainerDetails(trainerId,bio,specialization);
        // Simulate saving data (replace with real backend/API call)
        Toast.makeText(this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();

        // Hide progress bar
        progressBar.setVisibility(View.GONE);

        // Close the activity or redirect to another screen
        finish();
    }

    private void updateTrainerDetails(String trainerId, String newBio, String newSpecialization) {
        // Reference to the Trainers node in Firebase
        DatabaseReference trainersRef = FirebaseDatabase.getInstance().getReference("Trainers");

        // Create a map to hold the fields to be updated
        Map<String, Object> updates = new HashMap<>();
        updates.put("bio", newBio);
        updates.put("specialization", newSpecialization);

        // Update the specific fields for the given trainerId
        trainersRef.child(trainerId).updateChildren(updates)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Trainer details updated successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to update trainer: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }




    /**
     * Handle cancel action.
     */
    private void cancelEditing() {
        Toast.makeText(this, "Edit canceled.", Toast.LENGTH_SHORT).show();
        finish();
    }

    /**
     * Utility to display a short message.
     *
     * @param message The message to show in a Toast.
     */
    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
