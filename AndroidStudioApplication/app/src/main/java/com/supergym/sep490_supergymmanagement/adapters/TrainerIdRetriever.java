package com.supergym.sep490_supergymmanagement.adapters;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TrainerIdRetriever {

    private DatabaseReference databaseRef;

    public TrainerIdRetriever() {
        databaseRef = FirebaseDatabase.getInstance().getReference();
    }

    public void getTrainerId(String userId, final Callback callback) {
        databaseRef.child("Trainers").orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String trainerId = snapshot.getKey();
                        callback.onSuccess(trainerId);
                        return; // Assuming only one trainer per user ID
                    }
                } else {
                    callback.onFailure("Trainer not found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure("Error retrieving trainer data: " + databaseError.getMessage());
            }
        });
    }

    public interface Callback {
        void onSuccess(String trainerId);
        void onFailure(String errorMessage);
    }
}