package com.supergym.sep490_supergymmanagement;

import static org.junit.Assert.assertEquals;

import com.supergym.sep490_supergymmanagement.models.Meal;
import com.supergym.sep490_supergymmanagement.fragments.Diet_Eating_Fragment;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DietEatingFragmentTest {

    @Test
    public void testCalculateTotalCalories() {
        // Arrange
        Diet_Eating_Fragment fragment = new Diet_Eating_Fragment();
        List<Meal> meals = new ArrayList<>();

        // Add meals to the list
        meals.add(new Meal("2024-12-06", "Breakfast", "Omelette", 300));
        meals.add(new Meal("2024-12-06", "Lunch", "Grilled Chicken", 700));
        meals.add(new Meal("2024-12-05", "Dinner", "Salad", 200));

        // Act
        int totalCalories = fragment.calculateTotalCalories(meals, "2024-12-06");

        // Assert
        assertEquals(1000, totalCalories); // Expect 300 + 700
    }

    @Test
    public void testCalculateTotalCalories_NoMeals() {
        // Arrange
        Diet_Eating_Fragment fragment = new Diet_Eating_Fragment();
        List<Meal> meals = new ArrayList<>();

        // Act
        int totalCalories = fragment.calculateTotalCalories(meals, "2024-12-06");

        // Assert
        assertEquals(0, totalCalories); // Expect 0
    }

    @Test
    public void testCalculateTotalCalories_WrongDate() {
        // Arrange
        Diet_Eating_Fragment fragment = new Diet_Eating_Fragment();
        List<Meal> meals = new ArrayList<>();

        meals.add(new Meal("2024-12-05", "Dinner", "Salad", 200));

        // Act
        int totalCalories = fragment.calculateTotalCalories(meals, "2024-12-06");

        // Assert
        assertEquals(0, totalCalories); // No meal matches the date
    }
}
