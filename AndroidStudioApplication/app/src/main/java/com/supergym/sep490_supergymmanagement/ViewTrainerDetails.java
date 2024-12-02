package com.supergym.sep490_supergymmanagement;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.supergym.sep490_supergymmanagement.adapters.ReviewAdapter;
import com.supergym.sep490_supergymmanagement.apiadapter.ApiService.ApiService;
import com.supergym.sep490_supergymmanagement.apiadapter.RetrofitClient;
import com.supergym.sep490_supergymmanagement.models.Review;
import com.supergym.sep490_supergymmanagement.models.Trainer;
import com.supergym.sep490_supergymmanagement.repositories.TrainerResp;
import com.supergym.sep490_supergymmanagement.repositories.callbacks.Callback;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ViewTrainerDetails extends AppCompatActivity {
    private CardView returnBtn, editBtn, editTrainingImageCardView, bookCardView, upcommentBtn;
    private TextView trainerName, trainerSpecialization, trainerBio;
    private LinearLayout anotherLayout2;

    private EditText reviewEditText;
    private ImageView userAvatarImg; // For displaying the userAvatar
    private TrainerResp trainerResp = new TrainerResp(); // Assuming this is your database handler class
    private Button bookingBtn, submit_button;
    private String roleCheck, trainerIdPro;
    private Button submitButton;
    private boolean isRegistered;
    private  String trainerId, userId;
    // Initialize Firebase database reference
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Review");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trainer_detail);

        reviewEditText = findViewById(R.id.reviewEditText);


        submit_button = findViewById(R.id.submit_button);
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
        anotherLayout2 = findViewById(R.id.anotherLayout2);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
             userId = user.getUid();
        }

        checkRegistration(userId);

        bookingBtn.setOnClickListener(v -> {

            if (isRegistered) {
                Log.d("ViewTrainerDetails", "User is registered");
                // Navigate to the booking activity
                Intent intent = new Intent(ViewTrainerDetails.this, Activity_Book_Trainer.class);
                intent.putExtra("trainerId", trainerId); // Pass trainerId to the next activity
                startActivity(intent);
            } else {
                Toast.makeText(ViewTrainerDetails.this, "You are not a member. Please register a Membership Package", Toast.LENGTH_SHORT).show();
            }

        });
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadReview(); // Call the uploadReview method when the button is clicked
            }
        });



        MyApp app = (MyApp) getApplicationContext();
        String userRole = app.getUserRole();
        if ("pt".equals(userRole)) {
            roleCheck = "pt";
            editBtn.setVisibility(View.VISIBLE);
            editTrainingImageCardView.setVisibility(View.VISIBLE);
            bookCardView.setVisibility(View.GONE);
        }

        Button viewTrainingImageButton = findViewById(R.id.viewTrainingImage);
        Button viewReviewSessionsButton = findViewById(R.id.viewReviewSesions);

        LinearLayout changedLayout = findViewById(R.id.changedLayout);
        LinearLayout anotherLayout = findViewById(R.id.anotherLayout);

        viewTrainingImageButton.setOnClickListener(v -> {
            changedLayout.setVisibility(View.GONE);
            anotherLayout.setVisibility(View.GONE);
            anotherLayout2.setVisibility(View.VISIBLE);
            bookCardView.setVisibility(View.GONE);
            viewReviewSessionsButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E0E0E0")));
            viewReviewSessionsButton.setTextColor(Color.BLACK);
            viewTrainingImageButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.light_red)));
            viewTrainingImageButton.setTextColor(Color.WHITE);
        });

        viewReviewSessionsButton.setOnClickListener(v -> {
            anotherLayout.setVisibility(View.VISIBLE);
            changedLayout.setVisibility(View.VISIBLE);
            bookCardView.setVisibility(View.VISIBLE);
            anotherLayout2.setVisibility(View.GONE);
            viewTrainingImageButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E0E0E0")));
            viewTrainingImageButton.setTextColor(Color.BLACK);
            viewReviewSessionsButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.light_red)));
            viewReviewSessionsButton.setTextColor(Color.WHITE);
        });

        returnBtn.setOnClickListener(v -> finish());

        // Get the trainerId from the intent
         trainerId = getIntent().getStringExtra("trainerId");
        if (trainerId != null) {
            Log.d("TrainerId", "Received Trainer ID: " + trainerId);
            trainerIdPro = trainerId;
            fetchTrainerDetails(trainerId);
        } else {
            Toast.makeText(this, "Trainer ID is null!", Toast.LENGTH_SHORT).show();
        }

        editBtn.setOnClickListener(v -> {
            Log.d("BookingButton", "CardView clicked!");
            Intent intent = new Intent(ViewTrainerDetails.this, TrainerEditBioActivity.class);
            intent.putExtra("trainerId", trainerIdPro);
            startActivity(intent);
        });
        // Set a listener for the submit button


        loadReview();
    }

    private void loadReview() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Review");
        List<Review> reviewList = new ArrayList<>();
        ReviewAdapter adapter = new ReviewAdapter(this, reviewList);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reviewList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Review review = dataSnapshot.getValue(Review.class);
                    reviewList.add(review);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewTrainerDetails.this, "Failed to load reviews", Toast.LENGTH_SHORT).show();
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
                    loadUserInfor(trainer.getUserId());
                } else {
                    Toast.makeText(ViewTrainerDetails.this, "Trainer not found!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void checkRegistration(String registrationId) {
        ApiService api = RetrofitClient.getApiService(this); // Use 'this' as the context since this is an Activity

        // Disable the button until the response is fetched
        bookingBtn.setEnabled(false);
        submit_button.setEnabled(false);
        api.checkRegistration(registrationId).enqueue(new retrofit2.Callback<Boolean>() {
            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                bookingBtn.setEnabled(true); // Re-enable the button
                submit_button.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                     isRegistered = response.body();
                } else {
                    Toast.makeText(ViewTrainerDetails.this, "Failed to fetch registration status.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                bookingBtn.setEnabled(true); // Enable the button even on failure
                submit_button.setEnabled(true);
                Log.e("ViewTrainerDetails", "Error checking registration: " + t.getMessage());
                Toast.makeText(ViewTrainerDetails.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void uploadReview() {
        String reviewText = reviewEditText.getText().toString().trim();
        if(reviewText == null){
            Toast.makeText(this, "No review found, Please write Review!", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = user.getUid();
        if (!reviewText.isEmpty()) {
            // Create a unique review key using push() or a static key
            String reviewId = databaseReference.push().getKey(); // Or you can use a fixed key like "review01"

            if (reviewId != null) {
                // Create a Review object
                Review review = new Review(reviewText, userId, trainerId); // Sample trainerId and userId

                // Set the review data to Firebase under the new review ID
                databaseReference.child(reviewId).setValue(review)
                        .addOnSuccessListener(aVoid -> {
                            // Handle success, maybe show a toast message
                            reviewEditText.setText(""); // Clear the text field after submission
                            // Show a success message
                        })
                        .addOnFailureListener(e -> {
                            // Handle failure, maybe show an error message
                        });
            }
        } else {
            // Handle empty review case, show a message
        }
    }



    private void loadUserInfor(String userId1) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId1);
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
                                e.printStackTrace(); // Handle the case where the Base64 string could not be decoded
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
