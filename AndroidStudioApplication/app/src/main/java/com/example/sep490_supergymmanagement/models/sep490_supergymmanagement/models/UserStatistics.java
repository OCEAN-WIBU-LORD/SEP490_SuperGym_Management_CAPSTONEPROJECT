package com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.models;

public class UserStatistics {
    private String date;
    private double bmi;
    private String bmiStatus;
    private int calories;
    private int steps;
    private double waterIntake;

    // Default constructor required for calls to DataSnapshot.getValue(UserStatistics.class)
    public UserStatistics() {
    }

    public UserStatistics(String date, double bmi, String bmiStatus, int calories, int steps, double waterIntake) {
        this.date = date;
        this.bmi = bmi;
        this.bmiStatus = bmiStatus;
        this.calories = calories;
        this.steps = steps;
        this.waterIntake = waterIntake;
    }

    // Getters and Setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getBmi() {
        return bmi;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
    }

    public String getBmiStatus() {
        return bmiStatus;
    }

    public void setBmiStatus(String bmiStatus) {
        this.bmiStatus = bmiStatus;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public double getWaterIntake() {
        return waterIntake;
    }

    public void setWaterIntake(double waterIntake) {
        this.waterIntake = waterIntake;
    }
}
