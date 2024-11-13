package com.example.sep490_supergymmanagement.models;

public class Booking {
    private String trainerName;
    private String trainerType;
    private String sessionDays;
    private String sessionTime;
    private String notes;

    // Default constructor (needed for Firebase)
    public Booking() {}

    // Constructor with parameters
    public Booking(String trainerName, String trainerType, String sessionDays, String sessionTime, String notes) {
        this.trainerName = trainerName;
        this.trainerType = trainerType;
        this.sessionDays = sessionDays;
        this.sessionTime = sessionTime;
        this.notes = notes;
    }

    // Getter and Setter for trainerName
    public String getTrainerName() {
        return trainerName;
    }

    public void setTrainerName(String trainerName) {
        this.trainerName = trainerName;
    }

    // Getter and Setter for trainerType
    public String getTrainerType() {
        return trainerType;
    }

    public void setTrainerType(String trainerType) {
        this.trainerType = trainerType;
    }

    // Getter and Setter for sessionDays
    public String getSessionDays() {
        return sessionDays;
    }

    public void setSessionDays(String sessionDays) {
        this.sessionDays = sessionDays;
    }

    // Getter and Setter for sessionTime
    public String getSessionTime() {
        return sessionTime;
    }

    public void setSessionTime(String sessionTime) {
        this.sessionTime = sessionTime;
    }

    // Getter and Setter for notes
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}