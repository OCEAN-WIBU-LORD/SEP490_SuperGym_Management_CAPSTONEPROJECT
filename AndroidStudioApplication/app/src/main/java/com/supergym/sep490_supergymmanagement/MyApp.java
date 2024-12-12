package com.supergym.sep490_supergymmanagement;

import android.app.Application;

public class MyApp extends Application {
    private String userRole; // Global variable to store the user's role
    private boolean isRegistration;
    public boolean getIsRegistration() {
        return isRegistration;
    }

    public void setIsRegistration(boolean isRegistration) {
        this.isRegistration = isRegistration;
    }

    //private String JWTtoken;

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
        //JWTtoken = null; // Default value
    }

//    public String getJWTtoken() {
//        return JWTtoken;
//    }
//
//    public void setJWTtoken(String JWTtoken) {
//        this.JWTtoken = JWTtoken;
//    }
}
