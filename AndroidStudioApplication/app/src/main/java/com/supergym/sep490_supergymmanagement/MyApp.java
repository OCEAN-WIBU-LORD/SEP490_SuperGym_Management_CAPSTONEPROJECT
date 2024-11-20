package com.supergym.sep490_supergymmanagement;

import android.app.Application;

public class MyApp extends Application {
    private String userRole; // Global variable to store the user's role

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the userRole or other global variables if needed
        userRole = null; // Default value
    }
}
