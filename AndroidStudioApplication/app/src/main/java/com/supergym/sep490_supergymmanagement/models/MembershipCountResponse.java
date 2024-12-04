package com.supergym.sep490_supergymmanagement.models;

public class MembershipCountResponse {
    private int totalGymMemberships;
    private int totalBoxingMemberships;
    private int totalMemberships;

    // Getters and Setters
    public int getTotalGymMemberships() {
        return totalGymMemberships;
    }

    public void setTotalGymMemberships(int totalGymMemberships) {
        this.totalGymMemberships = totalGymMemberships;
    }

    public int getTotalBoxingMemberships() {
        return totalBoxingMemberships;
    }

    public void setTotalBoxingMemberships(int totalBoxingMemberships) {
        this.totalBoxingMemberships = totalBoxingMemberships;
    }

    public int getTotalMemberships() {
        return totalMemberships;
    }

    public void setTotalMemberships(int totalMemberships) {
        this.totalMemberships = totalMemberships;
    }
}
