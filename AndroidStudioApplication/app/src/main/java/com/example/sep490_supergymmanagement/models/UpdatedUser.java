package com.example.sep490_supergymmanagement.models;

import java.util.Date;
import java.util.Objects;

public class UpdatedUser {
    private String userId;
    private String name;
    private String email;
    private String gender;
    private Date dob;  // Using Date
    private String address;
    private String phone;
    private String idCard; // Changed to specific ID Card class
    private HealthCard healthCard; // Changed to specific Health Card class
    private String role;
    private String userAvatar;

    // Default constructor
    public UpdatedUser() {
    }

    // Parameterized constructor
    public UpdatedUser(String userId, String name, String email, String gender, Date dob, String address, String phone, String idCard, String role, String userAvatar) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.dob = dob;
        this.address = address;
        this.phone = phone;
        this.idCard = idCard;
        this.role = role;
        this.userAvatar = userAvatar;
    }

    // Getters and Setters
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

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    // Set DOB using a timestamp
    public void setDobTimestamp(long timestamp) {
        this.dob = new Date(timestamp); // Convert timestamp to Date
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public HealthCard getHealthCard() {
        return healthCard;
    }

    public void setHealthCard(HealthCard healthCard) {
        this.healthCard = healthCard;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isTrainer() {
        return "trainer".equals(role);
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    // Additional methods
    @Override
    public String toString() {
        return "UpdatedUser{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", dob=" + dob +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", idCard=" + idCard +
                ", healthCard=" + healthCard +
                ", role='" + role + '\'' +
                ", userAvatar='" + userAvatar + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, name, email, gender, dob, address, phone, idCard, healthCard, role, userAvatar);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UpdatedUser)) return false;
        UpdatedUser that = (UpdatedUser) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(name, that.name) &&
                Objects.equals(email, that.email) &&
                Objects.equals(gender, that.gender) &&
                Objects.equals(dob, that.dob) &&
                Objects.equals(address, that.address) &&
                Objects.equals(phone, that.phone) &&
                Objects.equals(idCard, that.idCard) &&
                Objects.equals(healthCard, that.healthCard) &&
                Objects.equals(role, that.role) &&
                Objects.equals(userAvatar, that.userAvatar);
    }
}

// Sample classes for IdCard and HealthCard
class IdCard {
    private String id;

    public IdCard(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "IdCard{id='" + id + '\'' + '}';
    }
}

class HealthCard {
    private String healthId;

    public HealthCard(String healthId) {
        this.healthId = healthId;
    }

    public String getHealthId() {
        return healthId;
    }

    public void setHealthId(String healthId) {
        this.healthId = healthId;
    }

    @Override
    public String toString() {
        return "HealthCard{healthId='" + healthId + '\'' + '}';
    }
}
