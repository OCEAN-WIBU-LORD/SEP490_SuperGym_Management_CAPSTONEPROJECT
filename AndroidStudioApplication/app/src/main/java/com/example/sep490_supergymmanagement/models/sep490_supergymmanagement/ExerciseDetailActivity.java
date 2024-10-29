package com.example.sep490_supergymmanagement.models.sep490_supergymmanagement;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sep490_supergymmanagement.R;
import com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.adapters.ExerciseSetDetailAdapter;
import com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.models.Set;

import java.util.ArrayList;
import java.util.List;

public class ExerciseDetailActivity extends AppCompatActivity {

    private RecyclerView setsRecyclerView;
    private ExerciseSetDetailAdapter setsAdapter;
    private List<Set> setsList;
    private Button addSetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_detail);

        setsRecyclerView = findViewById(R.id.recycler_view_sets);
        setsList = new ArrayList<>();

        // Retrieve the exercise ID
        String exerciseId = getIntent().getStringExtra("exercise_id");

        // Retrieve existing sets for the exercise from the static map
        if (exerciseId != null && SessionDataHolder.exerciseSetsMap.containsKey(exerciseId)) {
            setsList = SessionDataHolder.exerciseSetsMap.get(exerciseId); // Get sets from map
        }

        setsAdapter = new ExerciseSetDetailAdapter(setsList);
        setsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        setsRecyclerView.setAdapter(setsAdapter);

        addSetButton = findViewById(R.id.button_add_set);
        addSetButton.setOnClickListener(v -> showAddSetDialog());
    }

    @Override
    public void onBackPressed() {
        String exerciseId = getIntent().getStringExtra("exercise_id");
        if (exerciseId != null) {
            // Store the sets for the exercise in the static map
            SessionDataHolder.exerciseSetsMap.put(exerciseId, setsList);
        }
        super.onBackPressed();
    }


    private void showAddSetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_set, null);

        EditText repsInput = view.findViewById(R.id.edit_text_reps);
        EditText weightInput = view.findViewById(R.id.edit_text_weight);
        Button addButton = view.findViewById(R.id.button_add_set);

        builder.setView(view);
        AlertDialog dialog = builder.create();

        addButton.setOnClickListener(v -> {
            String reps = repsInput.getText().toString();
            String weight = weightInput.getText().toString();

            if (!reps.isEmpty() && !weight.isEmpty()) {
                // Create new Set and add to list
                Set set = new Set(Integer.parseInt(reps), Double.parseDouble(weight));
                setsList.add(set);
                setsAdapter.notifyDataSetChanged(); // Notify adapter to refresh the RecyclerView
                dialog.dismiss();
            } else {
                Toast.makeText(ExerciseDetailActivity.this, "Please enter reps and weight", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
}
