package com.supergym.sep490_supergymmanagement.models;

import android.graphics.Bitmap;

public class TrainerDTO {
    private String name;
    private String avatar;
    private String specialization;

    public TrainerDTO() {}

    public TrainerDTO(String name, String avatar, String specialization) {
        this.name = name;
        this.avatar = avatar;
        this.specialization = specialization;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String bio) {
        this.specialization = specialization;
    }
}
