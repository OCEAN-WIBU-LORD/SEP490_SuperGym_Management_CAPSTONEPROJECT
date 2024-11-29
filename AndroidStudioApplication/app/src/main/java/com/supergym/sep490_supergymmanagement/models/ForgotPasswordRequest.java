package com.supergym.sep490_supergymmanagement.models;

public class ForgotPasswordRequest {
    private String email;

    // Constructor
    public ForgotPasswordRequest(String email) {
        this.email = email;
    }

    // Getter and Setter
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
