package com.supergym.sep490_supergymmanagement.repositories;

import androidx.annotation.NonNull;

import com.supergym.sep490_supergymmanagement.models.Record;
import com.supergym.sep490_supergymmanagement.repositories.callbacks.Callback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RecordResp {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    // Get records by recordId
    public void getRecordById(String recordId, Callback<Record> callback) {
        if (recordId == null || recordId.isEmpty()) {
            callback.onCallback(new ArrayList<>());
            return;
        }

        Query query = databaseReference.child("medical_records").child(recordId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Record> records = new ArrayList<>();
                Record record = new Record();
                record.setRecordId(Objects.requireNonNull(snapshot.getKey()));
                record.setDiagnosis(Objects.requireNonNull(snapshot.child("diagnosis").getValue()).toString());
                record.setTreatment(Objects.requireNonNull(snapshot.child("treatment").getValue()).toString());
                int date = Objects.requireNonNull(snapshot.child("date").child("seconds").getValue(Integer.class));
                com.google.firebase.Timestamp dateTimestamp = new com.google.firebase.Timestamp(date, 0);
                record.setDate(dateTimestamp);

                records.add(record);

                callback.onCallback(records);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Error: " + error.getMessage());
            }
        });
    }

    // Create an record
    public void createRecord(Record record, Callback<String> callback) {
        DatabaseReference ref = databaseReference.child("medical_records").push();
        String recordId = ref.getKey();
        List<String> recordIds = new ArrayList<>();
        recordIds.add(recordId);
        ref.setValue(record).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onCallback(recordIds);
            } else {
                System.out.println("Error: " + task.getException());
            }
        });
    }

    // Update an record
    public void updateRecord(Record record, Callback<Record> callback) {
        DatabaseReference ref = databaseReference.child("medical_records").child(record.getRecordId());
        ref.setValue(record).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Record> records = new ArrayList<>();
                records.add(record);
                callback.onCallback(records);
            } else {
                System.out.println("Error: " + task.getException());
            }
        });
    }

    // Delete an record
    public void deleteRecord(String recordId, Callback<Record> callback) {
        DatabaseReference ref = databaseReference.child("medical_records").child(recordId);
        ref.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Record> records = new ArrayList<>();
                callback.onCallback(records);
            } else {
                System.out.println("Error: " + task.getException());
            }
        });
    }
}
