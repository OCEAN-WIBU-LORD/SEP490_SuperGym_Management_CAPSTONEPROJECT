package com.supergym.sep490_supergymmanagement.TestClass;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

public class ChangeColorProgress {

    public void changeColor(GradientDrawable drawable, int totalCalories) {
        if (drawable == null) {
            throw new IllegalArgumentException("Drawable cannot be null");
        }

        // Change color based on calories
        if (totalCalories > 3000) {
            drawable.setColor(Color.RED); // Set to red if calories exceed 3000
        } else {
            drawable.setColor(Color.parseColor("#009688")); // Set to default greenish color
        }
    }
}
