package com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.object;

import java.util.Arrays;
import java.util.List;

public class Equipment {
    public static final String ATLAS_STONE = "Atlas Stone";
    public static final String BARBELL = "Barbell";
    public static final String BODYWEIGHT = "Bodyweight";
    public static final String BROOM_STICK = "Broom Stick";
    public static final String CABLE = "Cable";
    public static final String DUMBBELLS = "Dumbbell";
    public static final String KETTLEBELL = "Kettlebell";
    public static final String LEVER = "Lever";
    public static final String MACHINE = "Machine";
    public static final String MEDICINE_BALL = "Medicine Ball";
    public static final String RESISTANCE_BAND = "Resistance Band";
    public static final String ROPE = "Rope";
    public static final String ROPE_MACHINE = "Rope Machine";
    public static final String SLED = "Sled";
    public static final String SMITH_MACHINE = "Smith Machine";
    public static final String STABILITY_BALL = "Stability Ball";
    public static final String SUSPENSION = "Suspension";
    public static final String WEIGHT = "Weight";

    public static List<String> getAllEquipment() {
        return Arrays.asList(
                ATLAS_STONE,
                BARBELL,
                BODYWEIGHT,
                BROOM_STICK,
                CABLE,
                DUMBBELLS,
                KETTLEBELL,
                LEVER,
                MACHINE,
                MEDICINE_BALL,
                RESISTANCE_BAND,
                ROPE,
                ROPE_MACHINE,
                SLED,
                SMITH_MACHINE,
                STABILITY_BALL,
                SUSPENSION,
                WEIGHT
        );
    }
}
