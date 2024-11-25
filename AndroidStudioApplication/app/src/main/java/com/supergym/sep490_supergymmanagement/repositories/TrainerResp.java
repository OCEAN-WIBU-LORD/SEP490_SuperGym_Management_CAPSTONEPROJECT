package com.supergym.sep490_supergymmanagement.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.supergym.sep490_supergymmanagement.models.Trainer;
import com.supergym.sep490_supergymmanagement.repositories.callbacks.Callback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrainerResp {
    private final DatabaseReference trainerTable;
    private DatabaseReference trainerRef;
    private DatabaseReference userRef;

    public TrainerResp() {
        // Reference to the "Trainers" node in Firebase
        trainerTable = FirebaseDatabase.getInstance().getReference("Trainers");
    }

    public void getTrainersWithDetails(Callback<Trainer> callback) {
        trainerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Trainer> trainers = new ArrayList<>();
                for (DataSnapshot trainerSnapshot : snapshot.getChildren()) {
                    Trainer trainer = trainerSnapshot.getValue(Trainer.class);

                    if (trainer != null) {
                        // Fetch additional details from the "users" table
                        String userId = trainer.getUserId();
                        userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                if (userSnapshot.exists()) {
                                    String email = userSnapshot.child("email").getValue(String.class);
                                    String phone = userSnapshot.child("phone").getValue(String.class);
                                    String gender = userSnapshot.child("gender").getValue(String.class);

                                    // Add details to the Trainer object
                                    trainer.setEmail(email);
                                    trainer.setPhone(phone);
                                    trainer.setGender(gender);

                                    trainers.add(trainer);

                                    // Notify callback once all trainers are loaded
                                    if (trainers.size() == snapshot.getChildrenCount()) {
                                        callback.onCallback(trainers);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle error
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    // Fetch all trainers
    public void getAllTrainers(Callback<Trainer> callback) {
        trainerTable.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<Trainer> trainers = new ArrayList<>();
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    Trainer trainer = snapshot.getValue(Trainer.class);
                    if (trainer != null) {
                        trainer.setTrainerId(snapshot.getKey()); // Set the ID from the key
                        trainers.add(trainer);
                    }
                }
                callback.onCallback(trainers);
            } else {
                Log.e("TrainerResp", "Failed to fetch trainers: ", task.getException());
            }
        });
    }




    // Fetch trainers by name
    public void getTrainersByName(String searchQuery, Callback<Trainer> callback) {
        trainerTable.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<Trainer> trainers = new ArrayList<>();
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    Trainer trainer = snapshot.getValue(Trainer.class);
                    if (trainer != null && trainer.getName().toLowerCase().contains(searchQuery.toLowerCase())) {
                        trainer.setTrainerId(snapshot.getKey());
                        trainers.add(trainer);
                    }
                }
                callback.onCallback(trainers);
            } else {
                Log.e("TrainerResp", "Failed to search trainers: ", task.getException());
            }
        });
    }

   /* public void getAllDataByTrainerId(String trainerId, Callback<Trainer> callback) {
        trainerTable.child(trainerId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Trainer trainer = task.getResult().getValue(Trainer.class);
                if (trainer != null) {
                    Log.d("TrainerData", "Trainer Data Retrieved: " + trainer.toString());
                    callback.onCallback(List.of(trainer)); // Pass the trainer object
                } else {
                    Log.e("TrainerData", "Trainer not found with ID: " + trainerId);
                    callback.onCallback(new ArrayList<>()); // Return an empty list
                }
            } else {
                Log.e("TrainerData", "Failed to retrieve data: ", task.getException());
                callback.onCallback(new ArrayList<>()); // Return an empty list on failure
            }
        });
    }*/


    public void loadAllTrainers(Callback<Trainer> callback) {
        trainerTable.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Trainer> trainers = new ArrayList<>();
                for (DataSnapshot trainerSnapshot : snapshot.getChildren()) {
                    Trainer trainer = trainerSnapshot.getValue(Trainer.class);
                    if (trainer != null) {
                        // Set the TrainerId from the key
                        trainer.setTrainerId(trainerSnapshot.getKey());
                        trainers.add(trainer);
                    }
                }
                callback.onCallback(trainers); // Pass the loaded trainers to the callback
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TrainerResp", "Failed to fetch trainers: ", error.toException());
            }
        });
    }
   /* public void getUserAvatarByUserId(String userId, Callback<String> callback) {
        trainerTable.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String userAvatar = snapshot.child("userAvatar").getValue(String.class);
                        if (userAvatar != null) {
                            callback.onCallback(userAvatar); // Return the userAvatar
                            return;
                        }
                    }
                    callback.onCallback(null); // No userAvatar found
                } else {
                    callback.onCallback(null); // User ID not found
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Error fetching userAvatar: " + databaseError.getMessage());
                callback.onCallback(null);
            }
        });
    }*/




    // Fetch a trainer by ID
    public void getTrainerById(String trainerId, Callback<Trainer> callback) {
        trainerTable.child(trainerId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Trainer trainer = task.getResult().getValue(Trainer.class);
                if (trainer != null) {
                    // Use Collections.singletonList for backward compatibility
                    callback.onCallback(Collections.singletonList(trainer));
                } else {
                    callback.onCallback(new ArrayList<>());
                }
            } else {
                Log.e("TrainerResp", "Failed to fetch trainer by ID: ", task.getException());
            }
        });
    }

}
