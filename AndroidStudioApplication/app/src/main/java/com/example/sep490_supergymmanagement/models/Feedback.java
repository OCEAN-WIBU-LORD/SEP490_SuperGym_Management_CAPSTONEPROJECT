package com.example.sep490_supergymmanagement.models;
// Inner class to represent the Feedback object

public class Feedback {

    private String userId;
    private String feedbackType;
    private String feedbackText;
    private float rating;

    // Empty constructor required for Firebase
    public Feedback() {
    }

    public Feedback(String userId, String feedbackType, String feedbackText, float rating) {
        this.userId = userId;
        this.feedbackType = feedbackType;
        this.feedbackText = feedbackText;
        this.rating = rating;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(String feedbackType) {
        this.feedbackType = feedbackType;
    }

    public String getFeedbackText() {
        return feedbackText;
    }

    public void setFeedbackText(String feedbackText) {
        this.feedbackText = feedbackText;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}