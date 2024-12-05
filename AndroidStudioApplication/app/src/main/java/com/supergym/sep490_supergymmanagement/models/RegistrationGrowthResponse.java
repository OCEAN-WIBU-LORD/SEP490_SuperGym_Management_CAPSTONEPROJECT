package com.supergym.sep490_supergymmanagement.models;

public class RegistrationGrowthResponse {
    private int registrationsThisMonth;
    private int registrationsLastMonth;
    private int totalRegistrations;
    private double growthPercentage;

    // Getter và Setter
    public int getRegistrationsThisMonth() {
        return registrationsThisMonth;
    }

    public void setRegistrationsThisMonth(int registrationsThisMonth) {
        this.registrationsThisMonth = registrationsThisMonth;
    }

    public int getRegistrationsLastMonth() {
        return registrationsLastMonth;
    }

    public void setRegistrationsLastMonth(int registrationsLastMonth) {
        this.registrationsLastMonth = registrationsLastMonth;
    }

    public int getTotalRegistrations() {
        return totalRegistrations;
    }

    public void setTotalRegistrations(int totalRegistrations) {
        this.totalRegistrations = totalRegistrations;
    }

    public double getGrowthPercentage() {
        return growthPercentage;
    }

    public void setGrowthPercentage(double growthPercentage) {
        this.growthPercentage = growthPercentage;
    }
}

