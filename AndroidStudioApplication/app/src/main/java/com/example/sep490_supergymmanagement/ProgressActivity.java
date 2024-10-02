package com.example.sep490_supergymmanagement;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.SimpleDateFormat;
import java.util.Locale;


/**
 * A simple Fragment subclass for Progress functionality.
 */
public class ProgressActivity extends Fragment {

    private MaterialCalendarView calendarView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.progress_fragment, container, false);

        // Initialize MaterialCalendarView
        calendarView = view.findViewById(R.id.calendarView);

        // Set listener for date click
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                // Convert selected date into a unique string ID (e.g., yyyyMMdd)
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                String dateId = sdf.format(date.getDate());  // Example: "20230927" for September 27, 2024

                // Reference to Firebase Realtime Database for schedules
                DatabaseReference scheduleRef = FirebaseDatabase.getInstance().getReference("schedule").child(dateId);

                // Query the schedule for the selected date (using dateId as part of the path)
                scheduleRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Assuming only one schedule for simplicity (or loop through all schedules for the day)
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String time = snapshot.child("time").getValue(String.class);
                                String classTitle = snapshot.child("classTitle").getValue(String.class);
                                String trainerName = snapshot.child("trainerName").getValue(String.class);

                                String scheduleId = snapshot.getKey();  // Fetch the unique schedule ID
                                String membershipId = snapshot.child("membershipId").getValue(String.class);  // Fetch related membershipId
                                String seatsAvailable = snapshot.child("seatsAvailable").getValue(String.class);
                                // Show BottomSheetDialog with fetched data
                                ScheduleBottomSheetDialog bottomSheet = new ScheduleBottomSheetDialog(time, classTitle, trainerName, seatsAvailable, scheduleId, membershipId);
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                bottomSheet.show(fragmentManager, "ScheduleBottomSheet");
                            }
                        } else {
                            // Handle the case where no schedule is found for the selected date
                            Toast.makeText(getContext(), "No schedule available for this date.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle database error
                        Toast.makeText(getContext(), "Error fetching schedule.", Toast.LENGTH_SHORT).show();
                    }
                });
            }


        });

        return view;
    }

    // Method to show the Training Detail dialog
    private void showTrainingDetailDialog(CalendarDay date) {
        // Format the selected date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String selectedDate = dateFormat.format(date.getDate());

        // Create a custom dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_training_detail, null);
        builder.setView(dialogView);

        // Access the TextViews in the custom dialog layout
        TextView tvTrainingDate = dialogView.findViewById(R.id.tvTrainingDate);
        TextView tvTrainingDetails = dialogView.findViewById(R.id.tvTrainingDetails);

        // Set the date and details in the dialog
        tvTrainingDate.setText(selectedDate);
        tvTrainingDetails.setText("Training session details for " + selectedDate + ".");

        // Set up the dialog buttons
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
