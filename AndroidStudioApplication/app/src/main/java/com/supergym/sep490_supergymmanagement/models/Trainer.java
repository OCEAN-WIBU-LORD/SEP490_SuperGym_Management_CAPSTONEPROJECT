package com.supergym.sep490_supergymmanagement.models;

import java.io.Serializable;

public class Trainer implements Serializable {
    private String trainerId;
    private String name;
    private String userId;
    private String specialization;
    private boolean isTrainerBoxing;
    private boolean isTrainerGym;

    // Additional fields for detailed information
    private String email;
    private String phone;
    private String gender;

    // Empty constructor (required for Firebase)
    public Trainer() {
    }

    // Full constructor
    public Trainer(String trainerId, String name, String userId, String specialization, boolean isTrainerBoxing, boolean isTrainerGym, String email, String phone, String gender) {
        this.trainerId = trainerId;
        this.name = name;
        this.userId = userId;
        this.specialization = specialization;
        this.isTrainerBoxing = isTrainerBoxing;
        this.isTrainerGym = isTrainerGym;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
    }

    // Getters and Setters
    public String getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(String trainerId) {
        this.trainerId = trainerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
