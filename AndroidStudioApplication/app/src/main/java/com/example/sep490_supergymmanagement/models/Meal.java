package com.example.sep490_supergymmanagement.models;
public class Meal {
    private String date;
    private String type;
    private String description;
    private int calories;

    public Meal() {
        // Default constructor required for calls to DataSnapshot.getValue(Meal.class)
    }

    public Meal(String date, String type, String description, int calories) {
        this.date = date;
        this.type = type;
        this.description = description;
        this.calories = calories;
    }

    // Getters and setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }
}
