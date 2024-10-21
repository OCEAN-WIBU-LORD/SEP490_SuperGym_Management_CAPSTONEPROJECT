package com.example.sep490_supergymmanagement.object;

import java.util.Arrays;
import java.util.List;

public class MuscleGroup {
    public static final String CORE = "Core";
    public static final String CHEST = "Chest";
    public static final String HIPS = "Hips";
    public static final String CALVES = "Calves";
    public static final String SHOULDERS = "Shoulders";
    public static final String GLUTES = "Glutes";
    public static final String HAMSTRINGS = "Hamstrings";
    public static final String QUADRICEPS = "Quadriceps";
    public static final String BACK = "Back";
    public static final String NECK = "Neck";
    public static final String BICEPS = "Biceps";
    public static final String FOREARMS = "Forearms and Wrists";
    public static final String TRICEPS = "Triceps";

    public static List<String> getAllMuscleGroups() {
        return Arrays.asList(
                CORE,
                HIPS,
                CALVES,
                GLUTES,
                HAMSTRINGS,
                QUADRICEPS,
                BACK,
                NECK,
                BICEPS,
                FOREARMS,
                TRICEPS,
                CHEST,
                SHOULDERS
        );
    }
}
