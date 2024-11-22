package com.supergym.sep490_supergymmanagement.models;

import java.util.List;

public class QrCodeRequest {
    private List<String> emails; // Emails to notify
    private String boxingMembershipPlanId; // ID of the boxing membership plan
    private String gymMembershipId; // ID of the gym membership
    private String trainerRentalPlanId; // ID of the trainer rental plan
    private boolean qrPayment; // Whether payment is via QR code
    private Integer duration; // Duration in months or days
    private String selectedTimeSlot; // Chosen time slot
    private boolean isMonWedFri; // True for Mon/Wed/Fri schedule, false for Tue/Thu/Sat

    // Default Constructor
    public QrCodeRequest() {}

    // Getters and Setters
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

    public String getGymMembershipId() {
        return gymMembershipId;
    }

    public void setGymMembershipId(String gymMembershipId) {
        this.gymMembershipId = gymMembershipId;
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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
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

    public void setMonWedFri(boolean isMonWedFri) {
        this.isMonWedFri = isMonWedFri;
    }
}
