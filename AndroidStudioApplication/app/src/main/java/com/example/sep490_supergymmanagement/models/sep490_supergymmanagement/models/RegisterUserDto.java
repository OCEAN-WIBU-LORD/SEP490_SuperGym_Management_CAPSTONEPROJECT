package com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.models;

public class RegisterUserDto {
    private String name;
    private String email;
    private String password;

    public RegisterUserDto(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // Getters and setters (if needed)
}