package com.supergym.sep490_supergymmanagement.models;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

// Model class representing a Check-In Request
public class CheckInRequest {

    @SerializedName("userId") // Maps to the JSON field 'userId'
    private String userId;

    @SerializedName("time")
    private String time; // Use String instead of Date


    // Default constructor (required for deserialization)
    public CheckInRequest() {
    }

    // Parameterized constructor for easier object creation
    public CheckInRequest(String userId, String time) {
        this.userId = userId;
        this.time = time;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
