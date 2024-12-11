package com.supergym.sep490_supergymmanagement.models;

public class Feedback {

    private String message; // Feedback message
    private float rating; // User rating
    private String userId; // ID of the user who submitted the feedback
    private String submittedAt; // Timestamp of feedback submission

    // Empty constructor required for Firebase
    public Feedback() {
    }

    public Feedback(String message, float rating, String userId, String submittedAt) {
        this.message = message;
        this.rating = rating;
        this.userId = userId;
        this.submittedAt = submittedAt;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(String submittedAt) {
        this.submittedAt = submittedAt;
    }
}
