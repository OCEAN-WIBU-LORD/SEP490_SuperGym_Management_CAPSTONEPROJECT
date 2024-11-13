package com.example.sep490_supergymmanagement.models;

// LogEntry.java
public class LogEntry {
    private String imageUrl;
    private String userName;
    private String role;
    private String checkInTime;
    private String checkOutTime;
    private String datetime;

    public LogEntry(String imageUrl, String userName, String role, String checkInTime, String checkOutTime, String datetime) {
        this.imageUrl = imageUrl;
        this.userName = userName;
        this.role = role;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.datetime = datetime;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(String checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}

