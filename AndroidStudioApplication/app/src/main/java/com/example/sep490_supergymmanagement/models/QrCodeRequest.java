package com.example.sep490_supergymmanagement.models;

import java.util.List;

public class QrCodeRequest {
    private String uid;
    private List<String> emails;
    private String boxingMembershipPlanId;
    private String gymMembershipId;
    private String trainerRentalPlanId;
    private Integer durationMonths;
    private Integer sessions;
    private String scheduleId;
    private boolean qrPayment;

    public QrCodeRequest(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

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

    public Integer getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(Integer durationMonths) {
        this.durationMonths = durationMonths;
    }

    public Integer getSessions() {
        return sessions;
    }

    public void setSessions(Integer sessions) {
        this.sessions = sessions;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public boolean isQrPayment() {
        return qrPayment;
    }

    public void setQrPayment(boolean qrPayment) {
        this.qrPayment = qrPayment;
    }
}

