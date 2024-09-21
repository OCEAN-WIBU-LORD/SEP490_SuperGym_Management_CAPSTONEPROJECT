package com.example.sep490_supergymmanagement.models;

import com.google.firebase.Timestamp;


public class Appointment {
    private String appointmentId;
    private Timestamp startTime;
    private Timestamp endTime;
    private String note;
    private String status;
    private String trainingLessionId;
    private String doctorId;
    private String userId;
    private String recordId;

    public Appointment() {
    }

    public Appointment(String appointmentId, String trainingLessionId, String doctorId, Timestamp endTime, String note, Timestamp startTime, String status, String userId, String recordIds) {
        this.appointmentId = appointmentId;
        this.trainingLessionId = trainingLessionId;
        this.doctorId = doctorId;
        this.endTime = endTime;
        this.note = note;
        this.startTime = startTime;
        this.status = status;
        this.userId = userId;
        this.recordId = recordIds;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public String getTrainingLessionId() {
        return trainingLessionId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public String getNote() {
        return note;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public String getStatus() {
        return status;
    }

    public String getUserId() {
        return userId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public void setTrainingLessionId(String trainingLessionId) {
        this.trainingLessionId = trainingLessionId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }
}