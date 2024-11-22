package com.supergym.sep490_supergymmanagement.models;

public class UserResponse {
    private String userId;
    private String name;
    private String email;
    private String specialization;
    private boolean isMonthlyPackage;
    private Integer minSessions; // Có thể null
    private Integer maxSessions; // Có thể null
    private int memberCount;
    private String boxingMembershipPlanId; // ID của BoxingMembershipPlan
    private String trainerRentalPlanId; // ID của TrainerRentalPlan

    // Getters and setters for UserResponse
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public boolean isMonthlyPackage() {
        return isMonthlyPackage;
    }

    public void setMonthlyPackage(boolean monthlyPackage) {
        isMonthlyPackage = monthlyPackage;
    }

    public Integer getMinSessions() {
        return minSessions;
    }

    public void setMinSessions(Integer minSessions) {
        this.minSessions = minSessions;
    }

    public Integer getMaxSessions() {
        return maxSessions;
    }

    public void setMaxSessions(Integer maxSessions) {
        this.maxSessions = maxSessions;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
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

    @Override
    public String toString() {
        return name; // Trả về tên huấn luyện viên
    }

}
