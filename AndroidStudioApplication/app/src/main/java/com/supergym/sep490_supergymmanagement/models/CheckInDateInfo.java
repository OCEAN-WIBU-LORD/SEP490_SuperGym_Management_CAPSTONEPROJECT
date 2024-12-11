// CheckInDateInfo.java
package com.supergym.sep490_supergymmanagement.models;

import com.google.gson.annotations.SerializedName;

public class CheckInDateInfo {

    @SerializedName("date")
    private String date; // Dạng chuỗi ISO 8601: "2024-11-25T00:00:00"

    @SerializedName("lastCheckInTime")
    private String lastCheckInTime; // Dạng "HH:mm", ví dụ: "16:11"

    // Getters và Setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLastCheckInTime() {
        return lastCheckInTime;
    }

    public void setLastCheckInTime(String firstCheckInTime) {
        this.lastCheckInTime = firstCheckInTime;
    }
}
