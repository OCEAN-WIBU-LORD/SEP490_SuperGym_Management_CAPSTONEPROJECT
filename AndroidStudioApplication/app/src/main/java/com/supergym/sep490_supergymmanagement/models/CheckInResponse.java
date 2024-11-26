package com.supergym.sep490_supergymmanagement.models;

import com.google.gson.annotations.SerializedName;

// Response Model
public class CheckInResponse {
    @SerializedName("message")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}