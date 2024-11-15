package com.supergym.sep490_supergymmanagement.animations;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CupOfWaterView extends View {

    private Paint cupPaint;
    private Paint waterPaint;
    private Paint overflowPaint;
    private float waterLevel = 0f;  // Water level starts empty
    private boolean isOverflowing = false;  // Track if overflow animation is running
    private List<OverflowParticle> overflowParticles = new ArrayList<>();  // List of overflow particles

    public CupOfWaterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Paint for the cup's outline
        cupPaint = new Paint();
        cupPaint.setColor(Color.parseColor("#6D4C41"));  // Brown cup outline color
        cupPaint.setStyle(Paint.Style.STROKE);  // Only draw the outline
        cupPaint.setStrokeWidth(10f);  // Cup outline thickness

        // Paint for the water inside the cup
        waterPaint = new Paint();
        waterPaint.setColor(Color.parseColor("#96d2f4"));  // Light blue water color
        waterPaint.setStyle(Paint.Style.FILL);  // Fill the water area

        // Paint for the overflowing water
        overflowPaint = new Paint();
        overflowPaint.setColor(Color.parseColor("#afdff6"));  // Overflow same color as the water
        overflowPaint.setStyle(Paint.Style.FILL);  // Fill the overflow particles
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Cup dimensions
        float cupLeft = getWidth() * 0.25f;
        float cupRight = getWidth() * 0.75f;
        float cupBottom = getHeight() * 0.9f;
        float cupTop = getHeight() * 0.1f;

        // Cup width and height
        float cupWidth = cupRight - cupLeft;
        float cupHeight = cupBottom - cupTop;

        // Draw the cup with rounded top and bottom
        canvas.drawOval(new RectF(cupLeft, cupTop, cupRight, cupTop + cupWidth * 0.2f), cupPaint);  // Top of the cup
        canvas.drawOval(new RectF(cupLeft, cupBottom - cupWidth * 0.2f, cupRight, cupBottom), cupPaint);  // Bottom of the cup
        canvas.drawLine(cupLeft, cupTop + cupWidth * 0.1f, cupLeft, cupBottom - cupWidth * 0.1f, cupPaint);  // Left side
        canvas.drawLine(cupRight, cupTop + cupWidth * 0.1f, cupRight, cupBottom - cupWidth * 0.1f, cupPaint);  // Right side

        // Water level calculations
        if (waterLevel > 0) {
            float waterTop = cupBottom - (cupHeight * waterLevel);  // Calculate where the top of the water is
            float waterBottomOvalHeight = cupWidth * 0.1f;  // Height for the curved bottom

            // Draw the water: body, top, and bottom
            canvas.drawRect(cupLeft, waterTop, cupRight, cupBottom - waterBottomOvalHeight, waterPaint);  // Water body
            canvas.drawOval(new RectF(cupLeft, waterTop - waterBottomOvalHeight, cupRight, waterTop + waterBottomOvalHeight), waterPaint);  // Water top
            canvas.drawOval(new RectF(cupLeft, cupBottom - waterBottomOvalHeight * 2, cupRight, cupBottom), waterPaint);  // Water bottom

            // Trigger the overflow if the water is full
            if (waterLevel >= 1.0f && !isOverflowing) {
                startOverflowAnimation();
            }
        }

        // If overflow animation is running, draw the overflow particles
        if (isOverflowing) {
            drawOverflow(canvas);
        }
    }

    // Method to draw all overflow particles
    private void drawOverflow(Canvas canvas) {
        for (OverflowParticle particle : overflowParticles) {
            overflowPaint.setAlpha((int) (particle.alpha * 255));  // Fade-out effect
            canvas.drawOval(new RectF(
                            particle.x - particle.size,
                            particle.y - particle.size,
                            particle.x + particle.size,
                            particle.y + particle.size),
                    overflowPaint);
        }
    }

    // Method to start the overflow animation
    private void startOverflowAnimation() {
        isOverflowing = true;
        overflowParticles.clear();  // Clear previous particles
        createOverflowParticles();  // Create new overflow particles

        ValueAnimator overflowAnimator = ValueAnimator.ofFloat(0f, 1f);
        overflowAnimator.setDuration(1000);  // Overflow lasts 1 second
        overflowAnimator.addUpdateListener(animation -> {
            float progress = (float) animation.getAnimatedValue();
            updateOverflowParticles(progress);
            invalidate();  // Redraw the view to animate the overflow
        });

        overflowAnimator.start();
        overflowAnimator.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                isOverflowing = false;
            }
        });
    }

    // Method to create overflow particles
    private void createOverflowParticles() {
        Random random = new Random();
        float cupTop = getHeight() * 0.1f;
        float cupLeft = getWidth() * 0.25f;
        float cupRight = getWidth() * 0.75f;

        // Generate multiple overflow particles
        for (int i = 0; i < 15; i++) {
            OverflowParticle particle = new OverflowParticle();
            particle.x = random.nextFloat() * (cupRight - cupLeft) + cupLeft;  // Random horizontal position
            particle.y = cupTop + 0.2f;  // Start just above the top of the cup
            particle.size = random.nextFloat() * 10 + 5;  // Random size between 5 and 15
            particle.alpha = 1.0f;  // Start fully opaque
            particle.velocityY = random.nextFloat() * 50 + 20;  // Random downward velocity
            particle.gravity = 100;  // Gravity to pull the particle downwards

            overflowParticles.add(particle);
        }
    }

    // Method to update overflow particles during animation
    private void updateOverflowParticles(float progress) {
        for (OverflowParticle particle : overflowParticles) {
            particle.y += particle.velocityY * progress;  // Move downward
            particle.alpha = 1.0f - progress;  // Fade out over time
        }
    }

    // Method to set the water level as a percentage (0.0 to 1.0)
    public void setWaterLevel(float level) {
        if (level >= 0f && level <= 1f) {
            this.waterLevel = level;
            invalidate();  // Redraw the view with the updated water level
        }
    }

    // Method to set water level based on liters
    public void setWaterLevelInLiters(float liters, float maxLiters) {
        // Calculate the percentage (water level) based on the current liters and max capacity
        float level = liters / maxLiters;

        if (level >= 0f && level <= 1f) {
            this.waterLevel = level;
            invalidate();  // Redraw the view with the updated water level
        }
    }

    // Getter for the current water level
    public float getWaterLevel() {
        return waterLevel;
    }

    // Overflow particle class
    private static class OverflowParticle {
        float x, y;  // Position
        float size;  // Size of the particle
        float velocityY;  // Velocity for downward movement
        float gravity;  // Gravity to pull particles downwards
        float alpha;  // Transparency (fade out effect)
    }
}
