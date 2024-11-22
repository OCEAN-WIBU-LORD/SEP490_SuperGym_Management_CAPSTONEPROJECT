package com.supergym.sep490_supergymmanagement.models;

public class MembershipPackage {
    private String gymMembershipId; // Primary Key
    private String name; // Membership name
    private Integer durationMonths; // Duration in months
    private Integer sessionCount; // Number of sessions
    private Double price; // Price in decimal format

    // Default Constructor
    public MembershipPackage() {}

    // Getters and Setters
    public String getGymMembershipId() {
        return gymMembershipId;
    }

    public void setGymMembershipId(String gymMembershipId) {
        this.gymMembershipId = gymMembershipId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(Integer durationMonths) {
        this.durationMonths = durationMonths;
    }

    public Integer getSessionCount() {
        return sessionCount;
    }

    public void setSessionCount(Integer sessionCount) {
        this.sessionCount = sessionCount;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
