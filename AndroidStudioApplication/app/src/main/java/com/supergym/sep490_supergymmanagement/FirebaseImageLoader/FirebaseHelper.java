package com.supergym.sep490_supergymmanagement.FirebaseImageLoader;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

public class FirebaseHelper {

    private FirebaseDatabase database;

    public FirebaseHelper() {
        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance();
    }

    // Callback interface to update the TextView from the Fragment
    public interface CountExpiredUsersCallback {
        void onCountExpiredUsers(int count);
    }

    // Helper function to get the first and last date of the current month
    private long[] getCurrentMonthRange() {
        Calendar calendar = Calendar.getInstance();

        // Set to the first day of the current month at midnight
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startOfMonth = calendar.getTimeInMillis();

        // Set to the last day of the current month at 23:59:59
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        long endOfMonth = calendar.getTimeInMillis();

        return new long[]{startOfMonth, endOfMonth};
    }

    // Function to count users with expired memberships within the current month
    public void countExpiredUsers(final CountExpiredUsersCallback callback) {
        long[] monthRange = getCurrentMonthRange();
        long startOfMonth = monthRange[0];
        long endOfMonth = monthRange[1];

        // Get today's date at midnight (current day)
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long today = calendar.getTimeInMillis();

        DatabaseReference boxingRef = database.getReference("BoxingRegistrations");
        DatabaseReference gymRef = database.getReference("GymRegistrations");

        // Query for BoxingRegistrations
        boxingRef.orderByChild("endDate")
                .startAt(startOfMonth)
                .endAt(endOfMonth)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int boxingCount = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            long endDate = snapshot.child("endDate").getValue(Long.class);
                            if (endDate > today) {
                                boxingCount++;
                            }
                        }
                        // Query for GymRegistrations
                        countGymRegistrations(boxingCount, startOfMonth, endOfMonth, today, callback);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle database error
                    }
                });
    }

    // Function to count GymRegistrations after counting BoxingRegistrations
    private void countGymRegistrations(int boxingCount, long startOfMonth, long endOfMonth, long today, final CountExpiredUsersCallback callback) {
        DatabaseReference gymRef = database.getReference("GymRegistrations");

        gymRef.orderByChild("endDate")
                .startAt(startOfMonth)
                .endAt(endOfMonth)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int gymCount = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            long endDate = snapshot.child("endDate").getValue(Long.class);
                            if (endDate > today) {
                                gymCount++;
                            }
                        }

                        // Final count
                        int totalCount = boxingCount + gymCount;
                        // Call the callback to update the TextView
                        callback.onCountExpiredUsers(totalCount);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle database error
                    }
                });
    }
}
