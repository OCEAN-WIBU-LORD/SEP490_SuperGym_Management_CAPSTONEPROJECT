package com.supergym.sep490_supergymmanagement.models;

public class SearchUser {
    private String userId;
    private String name;
    private String email;

    // Constructor mặc định
    public SearchUser() {
    }

    // Constructor đầy đủ
    public SearchUser(String userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
    }

    // Getters và Setters
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

    // Override toString (nếu cần debug)
    @Override
    public String toString() {
        return "SearchUser{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
