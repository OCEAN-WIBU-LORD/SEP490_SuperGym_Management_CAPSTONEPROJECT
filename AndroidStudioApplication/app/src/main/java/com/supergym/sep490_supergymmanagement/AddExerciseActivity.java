package com.supergym.sep490_supergymmanagement;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.supergym.sep490_supergymmanagement.models.Exercise;
import com.supergym.sep490_supergymmanagement.object.Equipment;
import com.supergym.sep490_supergymmanagement.object.MuscleGroup;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class AddExerciseActivity extends AppCompatActivity {

    public EditText exerciseNameInput;
    public Spinner muscleGroupSpinner;
    public Spinner equipmentSpinner;
    private Button saveExerciseButton;
    public DatabaseReference exercisesRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);

        // Ánh xạ view
        exerciseNameInput = findViewById(R.id.exercise_name_input);
        muscleGroupSpinner = findViewById(R.id.muscle_group_spinner);
        equipmentSpinner = findViewById(R.id.equipment_spinner);
        saveExerciseButton = findViewById(R.id.save_exercise_button);

        exercisesRef = FirebaseDatabase.getInstance().getReference("exercises");

        ArrayAdapter<String> muscleGroupAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, MuscleGroup.getAllMuscleGroups());
        muscleGroupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        muscleGroupSpinner.setAdapter(muscleGroupAdapter);

        ArrayAdapter<String> equipmentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Equipment.getAllEquipment());
        equipmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        equipmentSpinner.setAdapter(equipmentAdapter);

        saveExerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveExerciseToDatabase();
            }
        });
    }

    public void saveExerciseToDatabase() {
        String name = exerciseNameInput.getText().toString().trim();
        String muscleGroup = muscleGroupSpinner.getSelectedItem().toString();
        String equipment = equipmentSpinner.getSelectedItem().toString();

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter exercise name", Toast.LENGTH_SHORT).show();
            return;
        }

        String exerciseId = UUID.randomUUID().toString();

        Exercise newExercise = new Exercise(exerciseId, name, muscleGroup, equipment, null);

        exercisesRef.child(exerciseId).setValue(newExercise)
                .addOnSuccessListener(aVoid -> Toast.makeText(AddExerciseActivity.this, "Exercise saved successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(AddExerciseActivity.this, "Failed to save exercise: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}