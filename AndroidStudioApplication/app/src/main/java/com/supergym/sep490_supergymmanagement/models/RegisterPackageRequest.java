package com.supergym.sep490_supergymmanagement.models;

import java.util.List;

public class RegisterPackageRequest {

    private List<String> emails;
    private String boxingMembershipPlanId;
    private String gymMembershipId;
    private String trainerRentalPlanId;
    private boolean qrPayment;
    private int duration;
    private String selectedTimeSlot;
    private boolean isMonWedFri;
    // Getters and setters
    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public String getBoxingMembershipPlanId() {
        return boxingMembershipPlanId;
    }

    public void setBoxingMembershipPlanId(String boxingMembershipPlanId) {
        this.boxingMembershipPlanId = boxingMembershipPlanId;
    }

    public String getTrainerRentalPlanId() {
        return trainerRentalPlanId;
    }

    public void setTrainerRentalPlanId(String trainerRentalPlanId) {
        this.trainerRentalPlanId = trainerRentalPlanId;
    }

    public boolean isQrPayment() {
        return qrPayment;
    }

    public void setQrPayment(boolean qrPayment) {
        this.qrPayment = qrPayment;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getSelectedTimeSlot() {
        return selectedTimeSlot;
    }

    public void setSelectedTimeSlot(String selectedTimeSlot) {
        this.selectedTimeSlot = selectedTimeSlot;
    }

    public boolean isMonWedFri() {
        return isMonWedFri;
    }

    public void setMonWedFri(boolean monWedFri) {
        isMonWedFri = monWedFri;
    }
}

