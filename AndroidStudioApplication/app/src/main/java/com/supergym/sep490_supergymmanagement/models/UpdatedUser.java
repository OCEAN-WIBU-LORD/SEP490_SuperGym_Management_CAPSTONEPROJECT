package com.supergym.sep490_supergymmanagement.models;

import java.util.Date;
import java.util.Objects;

public class UpdatedUser {
    private String userId;
    private String name;
    private String email;
    private String gender;
    private Date dob; // Using Date
    private String address;
    private String phone;
    private String idCard;
    private String roleId;
    private String userAvatar;

    // Default constructor
    public UpdatedUser() {
    }

    // Parameterized constructor
    public UpdatedUser(String userId, String name, String email, String gender, Date dob, String address, String phone, String idCard, String roleId, String userAvatar) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.dob = dob;
        this.address = address;
        this.phone = phone;
        this.idCard = idCard;
        this.roleId = roleId;
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
                ", roleId='" + roleId + '\'' +
                ", userAvatar='" + userAvatar + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, name, email, gender, dob, address, phone, idCard,  roleId, userAvatar);
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
                Objects.equals(roleId, that.roleId) &&
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
