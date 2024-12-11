package com.supergym.sep490_supergymmanagement.fragments;

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

import com.supergym.sep490_supergymmanagement.R;
import com.supergym.sep490_supergymmanagement.models.Feedback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

        // Initialize Firebase Database reference (Assuming "Feedback" node exists)
        databaseReference = FirebaseDatabase.getInstance().getReference("Feedback");

        // Initialize views
        feedbackEditText = view.findViewById(R.id.feedbackEditText);
        ratingBar = view.findViewById(R.id.feedbackRatingBar);
        feedbackTypeSpinner = view.findViewById(R.id.feedbackTypeSpinner);
        returnBtn = view.findViewById(R.id.returnCardView);

        // Return button logic
        returnBtn.setOnClickListener(v -> replaceFragment(new com.supergym.sep490_supergymmanagement.FragmentUserProfile()));

        // Spinner setup
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.feedback_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        feedbackTypeSpinner.setAdapter(adapter);

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

        // Submit feedback button logic
        submitFeedbackButton.setOnClickListener(v -> {
            String message = feedbackEditText.getText().toString().trim();
            float rating = ratingBar.getRating();
            FirebaseUser user = mAuth.getCurrentUser();

            if (user != null && !message.isEmpty() && selectedFeedbackType != null) {
                String userId = user.getUid();
                saveFeedbackToFirebase(message, rating, userId);
            } else if (user == null) {
                Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            } else if (selectedFeedbackType == null) {
                Toast.makeText(getContext(), "Please select feedback type", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Please enter your feedback", Toast.LENGTH_SHORT).show();
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

    private void saveFeedbackToFirebase(String message, float rating, String userId) {
        // Generate a unique ID for each feedback entry
        String feedbackId = databaseReference.push().getKey();

        // Get current timestamp
        String submittedAt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(new Date());

        // Create a Feedback object
        Feedback feedbackData = new Feedback(message, rating, userId, submittedAt);

        // Save feedback to Firebase (directly under the generated feedbackId)
        if (feedbackId != null) {
            databaseReference.child(feedbackId).setValue(feedbackData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Feedback submitted successfully!", Toast.LENGTH_SHORT).show();
                            feedbackEditText.setText("");
                            ratingBar.setRating(0);
                            feedbackTypeSpinner.setSelection(0);
                        } else {
                            Toast.makeText(getContext(), "Failed to submit feedback. Try again!", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Failed to generate feedback ID. Try again!", Toast.LENGTH_SHORT).show();
        }
    }

}
