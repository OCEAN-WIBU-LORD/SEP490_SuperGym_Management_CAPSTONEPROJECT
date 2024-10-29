package com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.models;

import java.io.Serializable;

public class Trainer implements Serializable {
    private String TrainerId;
    private String userId;
    private String bio;
    private String[] trainingLessionIds;
    private String name;

    public Trainer() {
    }

    public Trainer(String TrainerId, String userId, String bio, String[] trainingLessionIds, String name) {
        this.TrainerId = TrainerId;
        this.userId = userId;
        this.bio = bio;
        this.trainingLessionIds = trainingLessionIds;
        this.name = name;
    }

    public String getTrainerId() {
        return TrainerId;
    }

    public void setTrainerId(String TrainerId) {
        this.TrainerId = TrainerId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String[] getTrainingLessionIds() {
        return trainingLessionIds;
    }

    public void setTrainingLessionIds(String[] trainingLessionIds) {
        this.trainingLessionIds = trainingLessionIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

