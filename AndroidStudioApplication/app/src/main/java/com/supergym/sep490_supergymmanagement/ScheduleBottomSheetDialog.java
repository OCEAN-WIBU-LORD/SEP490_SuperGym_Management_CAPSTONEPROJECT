package com.supergym.sep490_supergymmanagement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material .bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ScheduleBottomSheetDialog extends BottomSheetDialogFragment {

    private String time, classTitle, trainerName, seatsAvailable, scheduleId, membershipId;

    public ScheduleBottomSheetDialog(String time, String classTitle, String trainerName, String seatsAvailable, String scheduleId, String membershipId) {
        this.time = time;
        this.classTitle = classTitle;
        this.trainerName = trainerName;
        this.seatsAvailable = seatsAvailable;
        this.scheduleId = scheduleId;
        this.membershipId = membershipId; // Membership related to the user
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_schedule, container, false);

        // Bind data to the layout views
        TextView tvTime = view.findViewById(R.id.tvTime);
        TextView tvClassTitle = view.findViewById(R.id.tvClassTitle);
        TextView tvTrainerName = view.findViewById(R.id.tvTrainerName);
        TextView tvSeatsAvailable = view.findViewById(R.id.tvSeatsAvailable);

        tvTime.setText(time);
        tvClassTitle.setText(classTitle);
        tvTrainerName.setText(trainerName);
        tvSeatsAvailable.setText(seatsAvailable);

        // Handle sign-up button click
        view.findViewById(R.id.btnSignUp).setOnClickListener(v -> registerForClass(scheduleId,membershipId));

        return view;
    }




    // Function to register the user for a class and save the data in Firebase Firestore
    // Function to register the user for a class and save the data in Firebase Realtime Database
    public void registerForClass(String scheduleId, String userId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("registrations");
        String registrationId = ref.push().getKey(); // Generate a unique registration ID

        // Create registration object
        Map<String, Object> registration = new HashMap<>();
        registration.put("scheduleId", scheduleId);
        registration.put("userId", userId);
        registration.put("registrationDate", System.currentTimeMillis());

        // Save registration data to Firebase under 'registrations' node
        ref.child(registrationId).setValue(registration)
                .addOnSuccessListener(aVoid -> {
                    // Success, notify user
                    Toast.makeText(getContext(), "Successfully registered!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Error, notify user
                    Toast.makeText(getContext(), "Failed to register.", Toast.LENGTH_SHORT).show();
                });
    }

}
