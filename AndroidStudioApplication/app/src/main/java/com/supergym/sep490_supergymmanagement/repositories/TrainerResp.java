package com.supergym.sep490_supergymmanagement.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.supergym.sep490_supergymmanagement.models.Trainer;
import com.supergym.sep490_supergymmanagement.repositories.callbacks.Callback;

import java.util.ArrayList;
import java.util.List;

public class TrainerResp {
    private final DatabaseReference trainerTable;

    public TrainerResp() {
        // Reference to the "Trainers" node in Firebase
        trainerTable = FirebaseDatabase.getInstance().getReference("Trainers");
    }

    // Fetch all trainers
    public void getAllTrainers(Callback<Trainer> callback) {
        trainerTable.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<Trainer> trainers = new ArrayList<>();
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    Trainer trainer = snapshot.getValue(Trainer.class);
                    if (trainer != null) {
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
                        trainers.add(trainer);
                    }
                }
                callback.onCallback(trainers);
            } else {
                Log.e("TrainerResp", "Failed to search trainers: ", task.getException());
            }
        });
    }

    // Fetch a trainer by ID
    public void getTrainerById(String trainerId, Callback<Trainer> callback) {
        trainerTable.child(trainerId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Trainer trainer = task.getResult().getValue(Trainer.class);
                if (trainer != null) {
                    callback.onCallback(List.of(trainer));
                } else {
                    callback.onCallback(new ArrayList<>());
                }
            } else {
                Log.e("TrainerResp", "Failed to fetch trainer by ID: ", task.getException());
            }
        });
    }
}
