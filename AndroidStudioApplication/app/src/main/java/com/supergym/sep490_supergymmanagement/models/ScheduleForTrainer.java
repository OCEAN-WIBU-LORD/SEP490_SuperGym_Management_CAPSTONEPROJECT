package com.supergym.sep490_supergymmanagement.models;

import java.util.List;

public class ScheduleForTrainer {
    private String date;
    private String timeSlot;
    private List<String> customers;

    // Getters và Setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public List<String> getCustomers() {
        return customers;
    }

    public void setCustomers(List<String> customers) {
        this.customers = customers;
    }
}
