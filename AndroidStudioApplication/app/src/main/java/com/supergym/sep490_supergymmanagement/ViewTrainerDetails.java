package com.supergym.sep490_supergymmanagement;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide; // For loading images
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

import java.util.List;

public class ViewTrainerDetails extends AppCompatActivity {
    private CardView returnBtn, editBtn, editTrainingImageCardView, bookCardView;
    private TextView trainerName, trainerSpecialization, trainerBio;
    private ImageView userAvatarImg; // For displaying the userAvatar
    private TrainerResp trainerResp = new TrainerResp(); // Assuming this is your database handler class
    private Button bookingBtn;
    private String roleCheck, trainerIdPro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trainer_detail);

        // Initialize views
        returnBtn = findViewById(R.id.returnBtn);
        trainerName = findViewById(R.id.trainerName);
        trainerSpecialization = findViewById(R.id.trainerSpecialization);
        trainerBio = findViewById(R.id.trainerBio);
        userAvatarImg = findViewById(R.id.userAvatarImg); // Assuming there's an ImageView for the avatar
         bookingBtn = findViewById(R.id.bookingBtn);
        editBtn = findViewById(R.id.editBtn);
        editTrainingImageCardView = findViewById(R.id.editTrainingImageCardView);
        bookCardView = findViewById(R.id.bookCardView);
        bookingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BookingButton", "CardView clicked!");
                Intent intent = new Intent(ViewTrainerDetails.this, Activity_Book_Trainer.class);
                startActivity(intent);
            }
        });
        MyApp app = (MyApp) getApplicationContext();
        String userRole = app.getUserRole();
        if ("pt".equals(userRole)) {
            roleCheck = "pt";
           // Toast.makeText(this, "PT oke", Toast.LENGTH_SHORT).show();
            editBtn.setVisibility(View.VISIBLE);
            editTrainingImageCardView.setVisibility(View.VISIBLE);
            bookCardView.setVisibility(View.GONE);
        }

        // Finding the button and the layout to swap
        Button viewTrainingImageButton = findViewById(R.id.viewTrainingImage);
        Button viewReviewSessionsButton = findViewById(R.id.viewReviewSesions);

        LinearLayout changedLayout = findViewById(R.id.changedLayout);
        LinearLayout anotherLayout = findViewById(R.id.anotherLayout);

// Show "anotherLayout" when "viewTrainingImage" is clicked
        viewTrainingImageButton.setOnClickListener(v -> {
            changedLayout.setVisibility(View.GONE); // Hide the first tab
            anotherLayout.setVisibility(View.VISIBLE); // Show the second tab
            viewReviewSessionsButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E0E0E0")));
            viewReviewSessionsButton.setTextColor(Color.BLACK);

            // Set the other button as inactive
            viewTrainingImageButton.setBackgroundTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.light_red))
            );

            viewTrainingImageButton.setTextColor(Color.WHITE);
        });

// Show "changedLayout" when "viewReviewSesions" is clicked
        viewReviewSessionsButton.setOnClickListener(v -> {
            anotherLayout.setVisibility(View.GONE); // Hide the second tab
            changedLayout.setVisibility(View.VISIBLE); // Show the first tab
            viewTrainingImageButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E0E0E0")));
            viewTrainingImageButton.setTextColor(Color.BLACK);

            // Set the other button as inactive
            viewReviewSessionsButton.setBackgroundTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.light_red))
            );
            viewReviewSessionsButton.setTextColor(Color.WHITE);
        });



        // Return button logic
        returnBtn.setOnClickListener(v -> finish());

        // Get the trainerId from the intent
        String trainerId = getIntent().getStringExtra("trainerId");

        // Fetch trainer details
        if (trainerId != null) {
            Log.d("TrainerId", "Received Trainer ID: " + trainerId);
            trainerIdPro = trainerId;
            fetchTrainerDetails(trainerId);
        } else {
            Toast.makeText(this, "Trainer ID is null!", Toast.LENGTH_SHORT).show();
        }

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BookingButton", "CardView clicked!");

                Intent intent = new Intent(ViewTrainerDetails.this, TrainerEditBioActivity.class);
                intent.putExtra("trainerId", trainerIdPro);
                startActivity(intent);
            }
        });
    }

    private void fetchTrainerDetails(String trainerId) {
        trainerResp.getTrainerById(trainerId, new Callback<Trainer>() {
            @Override
            public void onCallback(List<Trainer> trainers) {
                if (!trainers.isEmpty()) {
                    Trainer trainer = trainers.get(0); // There will be only one trainer
                    displayTrainerDetails(trainer);
                    // Fetch and display the avatar using userId
                    loadUserInfor(trainer.getUserId());
                } else {
                    Toast.makeText(ViewTrainerDetails.this, "Trainer not found!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void loadUserInfor(String userId1) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = userId1;
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
    private void displayTrainerDetails(Trainer trainer) {
        trainerName.setText(trainer.getName());
        trainerSpecialization.setText(trainer.getSpecialization());
        trainerBio.setText(trainer.getBio());
    }
}
