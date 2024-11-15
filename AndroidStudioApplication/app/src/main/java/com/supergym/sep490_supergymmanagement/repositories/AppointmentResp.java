package com.supergym.sep490_supergymmanagement.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.supergym.sep490_supergymmanagement.models.Appointment;
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

public class AppointmentResp {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    // Get all appointments by userId
    public void getAppointments(String userId, Callback<Appointment> callback) {
        Query query = databaseReference.child("appointments").orderByChild("userId").equalTo(userId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Appointment> appointments = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Appointment appointment = new Appointment();
                    appointment.setAppointmentId(Objects.requireNonNull(childSnapshot.getKey()));
                    appointment.setTrainingLessionId(Objects.requireNonNull(childSnapshot.child("diseaseId").getValue()).toString());
                    appointment.setDoctorId(Objects.requireNonNull(childSnapshot.child("doctorId").getValue()).toString());
                    appointment.setNote(Objects.requireNonNull(childSnapshot.child("note").getValue()).toString());
                    appointment.setStatus(Objects.requireNonNull(childSnapshot.child("status").getValue()).toString());
                    appointment.setUserId(Objects.requireNonNull(childSnapshot.child("userId").getValue()).toString());
                    appointment.setRecordId(Objects.requireNonNull(childSnapshot.child("recordId").getValue()).toString());

                    int endTime = Objects.requireNonNull(childSnapshot.child("endTime").child("seconds").getValue(Integer.class));
                    com.google.firebase.Timestamp endTs = new com.google.firebase.Timestamp(endTime, 0);
                    appointment.setEndTime(endTs);

                    int startTime = Objects.requireNonNull(childSnapshot.child("startTime").child("seconds").getValue(Integer.class));
                    com.google.firebase.Timestamp startTs = new com.google.firebase.Timestamp(startTime, 0);
                    appointment.setStartTime(startTs);

                    appointments.add(appointment);
                }

                callback.onCallback(appointments);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Error: " + error.getMessage());
            }
        });
    }

    // Get appointments by appointmentId
    public void getAppointmentById(String appointmentId, Callback<Appointment> callback) {
        Query query = databaseReference.child("appointments").child(appointmentId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Appointment> appointments = new ArrayList<>();
                Appointment appointment = new Appointment();
                appointment.setAppointmentId(Objects.requireNonNull(snapshot.getKey()));
                appointment.setTrainingLessionId(Objects.requireNonNull(snapshot.child("diseaseId").getValue()).toString());
                appointment.setDoctorId(Objects.requireNonNull(snapshot.child("doctorId").getValue()).toString());
                appointment.setNote(Objects.requireNonNull(snapshot.child("note").getValue()).toString());
                appointment.setStatus(Objects.requireNonNull(snapshot.child("status").getValue()).toString());
                appointment.setUserId(Objects.requireNonNull(snapshot.child("userId").getValue()).toString());
                appointment.setRecordId(Objects.requireNonNull(snapshot.child("recordId").getValue()).toString());

                int endTime = Objects.requireNonNull(snapshot.child("endTime").child("seconds").getValue(Integer.class));
                com.google.firebase.Timestamp endTs = new com.google.firebase.Timestamp(endTime, 0);
                appointment.setEndTime(endTs);

                int startTime = Objects.requireNonNull(snapshot.child("startTime").child("seconds").getValue(Integer.class));
                com.google.firebase.Timestamp startTs = new com.google.firebase.Timestamp(startTime, 0);
                appointment.setStartTime(startTs);

                appointments.add(appointment);

                callback.onCallback(appointments);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Error: " + error.getMessage());
            }
        });
    }

    // Create an appointment
    public void createAppointment(Appointment appointment, Callback<Appointment> callback) {
        DatabaseReference ref = databaseReference.child("appointments").push();
        ref.setValue(appointment).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Appointment> appointments = new ArrayList<>();
                appointments.add(appointment);
                callback.onCallback(appointments);
            } else {
                System.out.println("Error: " + task.getException());
            }
        });
    }

    // Update an appointment
    public void updateAppointment(Appointment appointment, Callback<Appointment> callback) {
        DatabaseReference ref = databaseReference.child("appointments").child(appointment.getAppointmentId());
        ref.setValue(appointment).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Appointment> appointments = new ArrayList<>();
                appointments.add(appointment);
                callback.onCallback(appointments);
            } else {
                System.out.println("Error: " + task.getException());
            }
        });
    }

    // Delete an appointment
    public void deleteAppointment(String appointmentId, Callback<Appointment> callback) {
        DatabaseReference ref = databaseReference.child("appointments").child(appointmentId);
        ref.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Appointment> appointments = new ArrayList<>();
                callback.onCallback(appointments);
            } else {
                System.out.println("Error: " + task.getException());
            }
        });
    }

    // Check doctor and time to add Appointment
    public void getAppointmentsByDoctorAndTime(String doctorId, long timestamp, Callback<Appointment> callback) {

        long endTime = timestamp + 86400;

        Query query = databaseReference.child("appointments")
                .orderByChild("doctorId")
                .equalTo(doctorId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Appointment> appointments = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Appointment appointment = new Appointment();
                    appointment.setAppointmentId(childSnapshot.getKey());
                    appointment.setTrainingLessionId(childSnapshot.child("diseaseId").getValue(String.class));
                    appointment.setDoctorId(childSnapshot.child("doctorId").getValue(String.class));
                    appointment.setNote(childSnapshot.child("note").getValue(String.class));
                    appointment.setStatus(childSnapshot.child("status").getValue(String.class));
                    appointment.setUserId(childSnapshot.child("userId").getValue(String.class));
                    appointment.setRecordId(childSnapshot.child("recordId").getValue(String.class));

                    // Kiểm tra endTime
                    if (childSnapshot.child("endTime").child("seconds").exists()) {
                        Integer endTime = childSnapshot.child("endTime").child("seconds").getValue(Integer.class);
                        if (endTime != null) {
                            com.google.firebase.Timestamp endTs = new com.google.firebase.Timestamp(endTime, 0);
                            appointment.setEndTime(endTs);
                        } else {
                            Log.e("FirebaseData", "endTime.seconds is null for appointment ID: " + appointment.getAppointmentId());
                        }
                    } else {
                        Log.e("FirebaseData", "endTime.seconds does not exist for appointment ID: " + appointment.getAppointmentId());
                    }

                    // Kiểm tra startTime
                    if (childSnapshot.child("startTime").child("seconds").exists()) {
                        Integer startTime = childSnapshot.child("startTime").child("seconds").getValue(Integer.class);
                        if (startTime != null) {
                            com.google.firebase.Timestamp startTs = new com.google.firebase.Timestamp(startTime, 0);
                            appointment.setStartTime(startTs);

                            if (startTime >= timestamp && startTime <= (timestamp + 86400)) {
                                appointments.add(appointment);
                            }
                        } else {
                            Log.e("FirebaseData", "startTime.seconds is null for appointment ID: " + appointment.getAppointmentId());
                        }
                    } else {
                        Log.e("FirebaseData", "startTime.seconds does not exist for appointment ID: " + appointment.getAppointmentId());
                    }
                }

                callback.onCallback(appointments);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Error: " + error.getMessage());
            }
        });
    }
    public void getAppointmentsByDoctor(String doctorId, Callback<Appointment> callback) {


        Query query = databaseReference.child("appointments")
                .orderByChild("doctorId")
                .equalTo(doctorId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Appointment> appointments = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Appointment appointment = new Appointment();
                    appointment.setAppointmentId(childSnapshot.getKey());
                    appointment.setTrainingLessionId(childSnapshot.child("diseaseId").getValue(String.class));
                    appointment.setDoctorId(childSnapshot.child("doctorId").getValue(String.class));
                    appointment.setNote(childSnapshot.child("note").getValue(String.class));
                    appointment.setStatus(childSnapshot.child("status").getValue(String.class));
                    appointment.setUserId(childSnapshot.child("userId").getValue(String.class));
                    appointment.setRecordId(childSnapshot.child("recordId").getValue(String.class));

                    int endTime = childSnapshot.child("endTime").child("seconds").getValue(Integer.class);
                    com.google.firebase.Timestamp endTs = new com.google.firebase.Timestamp(endTime, 0);
                    appointment.setEndTime(endTs);

                    int startTime = childSnapshot.child("startTime").child("seconds").getValue(Integer.class);
                    com.google.firebase.Timestamp startTs = new com.google.firebase.Timestamp(startTime, 0);
                    appointment.setStartTime(startTs);

                        appointments.add(appointment);
                }

                callback.onCallback(appointments);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Error: " + error.getMessage());
            }
        });
    }

    public void getAppointmentsByStatusAndTime(String status, long timestamp, Callback<Appointment> callback) {

        long endTime = timestamp + 86400;

        Query query = databaseReference.child("appointments")
                .orderByChild("startTime")
                .startAt(timestamp)
                .endAt(endTime)
                .equalTo("status", status);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Appointment> appointments = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Appointment appointment = new Appointment();
                    appointment.setAppointmentId(childSnapshot.getKey());
                    appointment.setTrainingLessionId(childSnapshot.child("diseaseId").getValue(String.class));
                    appointment.setDoctorId(childSnapshot.child("doctorId").getValue(String.class));
                    appointment.setNote(childSnapshot.child("note").getValue(String.class));
                    appointment.setStatus(childSnapshot.child("status").getValue(String.class));
                    appointment.setUserId(childSnapshot.child("userId").getValue(String.class));
                    appointment.setRecordId(childSnapshot.child("recordId").getValue(String.class));

                    int endTime = childSnapshot.child("endTime").child("seconds").getValue(Integer.class);
                    com.google.firebase.Timestamp endTs = new com.google.firebase.Timestamp(endTime, 0);
                    appointment.setEndTime(endTs);

                    int startTime = childSnapshot.child("startTime").child("seconds").getValue(Integer.class);
                    com.google.firebase.Timestamp startTs = new com.google.firebase.Timestamp(startTime, 0);
                    appointment.setStartTime(startTs);

                    if (Long.parseLong(String.valueOf(startTime)) >= timestamp && Long.parseLong(String.valueOf(startTime)) <= (timestamp + 86400)) {
                        appointments.add(appointment);
                    }
                }

                callback.onCallback(appointments);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Error: " + error.getMessage());
            }
        });
    }

}
