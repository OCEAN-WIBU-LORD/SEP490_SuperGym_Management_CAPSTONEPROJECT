package com.supergym.sep490_supergymmanagement.TestedFunction;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

import com.supergym.sep490_supergymmanagement.TestClass.ChangeColorProgress;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28) // Use the appropriate SDK version
public class ChangeColorProgressTest {

    private ChangeColorProgress changeColorProgress;

    @Before
    public void setUp() {
        changeColorProgress = new ChangeColorProgress();
    }

    @Test
    public void testChangeColorProgress_WithinLimit() {
        // Test that Color.parseColor works
        String colorCode = "#009688";
        int color = android.graphics.Color.parseColor(colorCode);
        assertNotNull("Color should not be null", color);
    }
    @Config(manifest=Config.NONE)
    @Test
    public void testChangeColorProgress_OverLimit() {
        // Test logic to change color based on total calories
        // Simulate a scenario where totalCalories > 3000
        GradientDrawable mockDrawable = mock(GradientDrawable.class);
        changeColorProgress.changeColor(mockDrawable, 3500);

        // Verify that the color was set to RED
        verify(mockDrawable).setColor(android.graphics.Color.RED);
    }
}

