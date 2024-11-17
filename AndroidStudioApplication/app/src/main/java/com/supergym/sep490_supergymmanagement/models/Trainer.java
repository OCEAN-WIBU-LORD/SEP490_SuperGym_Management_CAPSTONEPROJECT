package com.supergym.sep490_supergymmanagement.models;

import java.io.Serializable;

public class Trainer implements Serializable {
    private String userId;
    private String name;
    private boolean isTrainerBoxing;
    private boolean isTrainerGym;

    public Trainer() {
        // Default constructor required for Firebase
    }

    public Trainer(String userId, String name, boolean isTrainerBoxing, boolean isTrainerGym) {
        this.userId = userId;
        this.name = name;
        this.isTrainerBoxing = isTrainerBoxing;
        this.isTrainerGym = isTrainerGym;
    }

    // Getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isTrainerBoxing() {
        return isTrainerBoxing;
    }

    public void setTrainerBoxing(boolean trainerBoxing) {
        isTrainerBoxing = trainerBoxing;
    }

    public boolean isTrainerGym() {
        return isTrainerGym;
    }

    public void setTrainerGym(boolean trainerGym) {
        isTrainerGym = trainerGym;
    }

    @Override
    public String toString() {
        return "Trainer{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", isTrainerBoxing=" + isTrainerBoxing +
                ", isTrainerGym=" + isTrainerGym +
                '}';
    }
}
