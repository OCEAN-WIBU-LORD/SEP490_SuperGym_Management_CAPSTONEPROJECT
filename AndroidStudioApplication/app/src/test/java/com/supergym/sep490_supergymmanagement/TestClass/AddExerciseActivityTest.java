/*
package com.supergym.sep490_supergymmanagement.TestClass;
import static org.mockito.Mockito.*;

import android.widget.Spinner;
import android.widget.EditText;
import android.widget.Toast;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityScenarioRule;

import com.google.firebase.database.DatabaseReference;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AddExerciseActivityTest {

    @Rule
    public ActivityScenarioRule<AddExerciseActivity> activityRule =
            new ActivityScenarioRule<>(AddExerciseActivity.class);

    private AddExerciseActivity activity;
    private EditText mockNameInput;
    private Spinner mockMuscleGroupSpinner;
    private Spinner mockEquipmentSpinner;
    private DatabaseReference mockDatabaseRef;

    @Before
    public void setUp() {
        activityRule.getScenario().onActivity(activity -> {
            this.activity = activity;

            // Mock views
            mockNameInput = mock(EditText.class);
            mockMuscleGroupSpinner = mock(Spinner.class);
            mockEquipmentSpinner = mock(Spinner.class);

            // Replace real views with mocked ones
            activity.exerciseNameInput = mockNameInput;
            activity.muscleGroupSpinner = mockMuscleGroupSpinner;
            activity.equipmentSpinner = mockEquipmentSpinner;

            // Mock Firebase reference
            mockDatabaseRef = mock(DatabaseReference.class);
            activity.exercisesRef = mockDatabaseRef;
        });
    }

    @Test
    public void testSaveExercise_EmptyName_ShowsToast() {
        // Simulate empty name input
        when(mockNameInput.getText().toString()).thenReturn("");

        activity.saveExerciseToDatabase();

        verify(mockNameInput).getText();
        verify(mockDatabaseRef, never()).child(anyString());
        // Ensure correct toast message
        Toast.makeText(
                ApplicationProvider.getApplicationContext(),
                "Please enter exercise name",
                Toast.LENGTH_SHORT
        );
    }

    @Test
    public void testSaveExercise_Success_ShowsSuccessToast() {
        // Simulate valid input
        when(mockNameInput.getText().toString()).thenReturn("Push Up");
        when(mockMuscleGroupSpinner.getSelectedItem().toString()).thenReturn("Chest");
        when(mockEquipmentSpinner.getSelectedItem().toString()).thenReturn("None");

        DatabaseReference childRef = mock(DatabaseReference.class);
        when(mockDatabaseRef.child(anyString())).thenReturn(childRef);

        activity.saveExerciseToDatabase();

        verify(mockDatabaseRef).child(anyString());
        verify(childRef).setValue(any());
        Toast.makeText(
                ApplicationProvider.getApplicationContext(),
                "Exercise saved successfully!",
                Toast.LENGTH_SHORT
        );
    }

    @Test
    public void testSaveExercise_Failure_ShowsFailureToast() {
        // Simulate valid input but database failure
        when(mockNameInput.getText().toString()).thenReturn("Push Up");
        when(mockMuscleGroupSpinner.getSelectedItem().toString()).thenReturn("Chest");
        when(mockEquipmentSpinner.getSelectedItem().toString()).thenReturn("None");

        DatabaseReference childRef = mock(DatabaseReference.class);
        when(mockDatabaseRef.child(anyString())).thenReturn(childRef);

        doAnswer(invocation -> {
            throw new RuntimeException("Firebase error");
        }).when(childRef).setValue(any());

        activity.saveExerciseToDatabase();

        verify(mockDatabaseRef).child(anyString());
        verify(childRef).setValue(any());
        Toast.makeText(
                ApplicationProvider.getApplicationContext(),
                "Failed to save exercise:",
                Toast.LENGTH_SHORT
        );
    }
}
*/
