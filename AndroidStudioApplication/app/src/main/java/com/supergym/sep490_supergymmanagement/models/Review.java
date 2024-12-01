package com.supergym.sep490_supergymmanagement.models;

public class Review {
    private String review;
    private String trainerId;
    private String userId;

    // Empty constructor required for Firebase
    public Review() {}

    public Review(String review, String trainerId, String userId) {
        this.review = review;
        this.trainerId = trainerId;
        this.userId = userId;
    }

    public String getReview() {
        return review;
    }

    public String getTrainerId() {
        return trainerId;
    }

    public String getUserId() {
        return userId;
    }
}
