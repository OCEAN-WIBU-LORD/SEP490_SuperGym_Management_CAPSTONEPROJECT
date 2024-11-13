package com.example.sep490_supergymmanagement;

import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Activity_Book_Trainer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_trainer);

        // Set up window insets to manage padding for system bars dynamically


        // Setting up listeners and other initialization logic here
        setupSubmitButton();
    }

    private void setupSubmitButton() {
        View submitButton = findViewById(R.id.submit_button); // Ensure you have this ID set in XML layout
        if (submitButton != null) {
            submitButton.setOnClickListener(v -> {
                // Logic for handling submit action here
                Toast.makeText(Activity_Book_Trainer.this, "Request Submitted", Toast.LENGTH_SHORT).show();
                // Further actions such as saving the booking details
                saveBookingDetails();
            });
        }
    }

    private void saveBookingDetails() {
        // Add your logic to save booking details here, like sending data to Firebase
        // Example placeholder code:
        // FirebaseDatabase.getInstance().getReference().child("Bookings").setValue(bookingData);

        Toast.makeText(this, "Booking details saved successfully", Toast.LENGTH_SHORT).show();
    }
}
