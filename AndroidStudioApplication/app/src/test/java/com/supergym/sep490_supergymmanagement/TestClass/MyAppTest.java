package com.supergym.sep490_supergymmanagement.TestClass;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.supergym.sep490_supergymmanagement.MyApp;

import org.junit.Before;
import org.junit.Test;

public class MyAppTest {

    private MyApp myApp;

    @Before
    public void setUp() {
        myApp = new MyApp();
        myApp.onCreate(); // Simulate Application onCreate behavior
    }

    @Test
    public void testDefaultUserRole() {
        // Check the default value of userRole
        assertNull("Default userRole should be null", myApp.getUserRole());
    }

    @Test
    public void testSetUserRole() {
        // Set a userRole and check if it updates correctly
        String testRole = "Admin";
        myApp.setUserRole(testRole);
        assertEquals("UserRole should match the set value", testRole, myApp.getUserRole());
    }
}
