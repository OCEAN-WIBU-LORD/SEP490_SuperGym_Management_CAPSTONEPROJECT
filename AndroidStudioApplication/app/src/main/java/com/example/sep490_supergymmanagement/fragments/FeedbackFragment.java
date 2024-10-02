package com.example.sep490_supergymmanagement.fragments;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.sep490_supergymmanagement.R;
import com.example.sep490_supergymmanagement.models.Feedback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class FeedbackFragment extends Fragment {

    // Firebase database reference
    private DatabaseReference databaseReference;
    private CardView returnBtn;

    // Firebase Authentication
    private FirebaseAuth mAuth;

    // Views
    private EditText feedbackEditText;
    private RatingBar ratingBar;
    private Button submitFeedbackButton;

    private Spinner feedbackTypeSpinner;
    // To store selected feedback type
    private String selectedFeedbackType;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firebase Database reference (Assuming "feedback" node exists)
        databaseReference = FirebaseDatabase.getInstance().getReference("feedback");

        // Initialize views
        feedbackEditText = view.findViewById(R.id.feedbackEditText);
        ratingBar = view.findViewById(R.id.feedbackRatingBar);
        feedbackTypeSpinner = view.findViewById(R.id.feedbackTypeSpinner);

        returnBtn = view.findViewById(R.id.returnCardView);

        returnBtn.setOnClickListener(v -> replaceFragment(new com.example.sep490_supergymmanagement.FragmentUserProfile()));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.feedback_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        feedbackTypeSpinner.setAdapter(adapter);

        // Handle Spinner item selection
        feedbackTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedFeedbackType = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedFeedbackType = null; // No selection made
            }
        });

        submitFeedbackButton = view.findViewById(R.id.submitFeedbackButton);



        // Set an OnClickListener for the submit button
        submitFeedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get feedback input from EditText
                String feedback = feedbackEditText.getText().toString().trim();

                // Get the rating from RatingBar
                float rating = ratingBar.getRating();

                // Get current user's ID
                FirebaseUser user = mAuth.getCurrentUser();
                String userId = user != null ? user.getUid() : null;

                // Check if the feedback is not empty and user is authenticated
                if (!feedback.isEmpty() && userId != null) {
                    // Save feedback and rating to Firebase with userId
                    saveFeedbackToFirebase(feedback, rating, userId);
                } else if (userId == null) {
                    Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
                } else {
                    // Show error message if feedback is empty
                    Toast.makeText(getContext(), "Please enter your feedback", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    // Method to save feedback and rating to Firebase Realtime Database
    private void saveFeedbackToFirebase(String feedback, float rating, String userId) {
        // Generate a unique ID for each feedback entry
        String feedbackId = databaseReference.push().getKey();

        // Create a Feedback object to store feedback details
        Feedback feedbackObj = new Feedback(feedbackId, userId, feedback, rating);

        // Check if feedback is valid and user is authenticated
        if (!feedback.isEmpty() && userId != null) {
            // Create a feedback object to store in Firebase
            Feedback feedbackData = new Feedback(userId, selectedFeedbackType, feedback, rating);

            // Save feedback to Firebase under the user's node
            databaseReference.child(userId).push().setValue(feedbackData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Notify user of successful submission
                            Toast.makeText(getContext(), "Feedback submitted successfully!", Toast.LENGTH_SHORT).show();
                            // Clear input fields
                            feedbackEditText.setText("");
                            ratingBar.setRating(0);
                            feedbackTypeSpinner.setSelection(0);
                        } else {
                            // Notify user of failure
                            Toast.makeText(getContext(), "Failed to submit feedback. Try again!", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Notify user if feedback input is invalid or user is not authenticated
            Toast.makeText(getContext(), "Please provide feedback and ensure you're logged in.", Toast.LENGTH_SHORT).show();
        }
    }


}