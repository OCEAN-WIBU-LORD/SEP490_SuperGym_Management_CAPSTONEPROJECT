package com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.models;

public class Schedule {

    private String scheduleId;      // Unique identifier for the schedule (based on the date or some unique value)
    private String time;            // Class time (e.g., "08:00 ➜ 09:00")
    private String classTitle;      // Title of the class (e.g., "Yoga", "Dance Class")
    private String trainerName;     // Trainer's name (e.g., "Alice Johnson")
    private int seatsAvailable;     // Number of available seats for the class
    private boolean isRegistered;   // Whether the user is registered for this class or not

    // Constructor
    public Schedule(String scheduleId, String time, String classTitle, String trainerName, int seatsAvailable, boolean isRegistered) {
        this.scheduleId = scheduleId;
        this.time = time;
        this.classTitle = classTitle;
        this.trainerName = trainerName;
        this.seatsAvailable = seatsAvailable;
        this.isRegistered = isRegistered;
    }

    // Default constructor (for Firebase Realtime Database)
    public Schedule() {
    }

    // Getters and Setters
    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getClassTitle() {
        return classTitle;
    }

    public void setClassTitle(String classTitle) {
        this.classTitle = classTitle;
    }

    public String getTrainerName() {
        return trainerName;
    }

    public void setTrainerName(String trainerName) {
        this.trainerName = trainerName;
    }

    public int getSeatsAvailable() {
        return seatsAvailable;
    }

    public void setSeatsAvailable(int seatsAvailable) {
        this.seatsAvailable = seatsAvailable;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public void setRegistered(boolean registered) {
        isRegistered = registered;
    }
}
