package com.supergym.sep490_supergymmanagement.models;

public class Day {
    private String day;
    private String dayOfWeek;

    public Day(String day, String dayOfWeek) {
        this.day = day;
        this.dayOfWeek = dayOfWeek;
    }

    public String getDay() {
        return day;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }
}