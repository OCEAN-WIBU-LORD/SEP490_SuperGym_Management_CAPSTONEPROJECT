package com.supergym.sep490_supergymmanagement;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.supergym.sep490_supergymmanagement.adapters.ExerciseSetDetailAdapter;
import com.supergym.sep490_supergymmanagement.models.Set;

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
            setsList = new ArrayList<>(SessionDataHolder.exerciseSetsMap.get(exerciseId)); // Get sets from map
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
            // Kiểm tra nếu có ít nhất một set thì lưu lại, ngược lại không
            if (setsList != null && !setsList.isEmpty()) {
                // Store the sets for the exercise in the static map
                SessionDataHolder.exerciseSetsMap.put(exerciseId, setsList);
            } else {
                // Nếu không có set nào, xóa bất kỳ dữ liệu cũ nào (nếu có)
                SessionDataHolder.exerciseSetsMap.remove(exerciseId);
            }
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
            String repsStr = repsInput.getText().toString().trim();
            String weightStr = weightInput.getText().toString().trim();

            if (!repsStr.isEmpty() && !weightStr.isEmpty()) {
                try {
                    int reps = Integer.parseInt(repsStr);
                    double weight = Double.parseDouble(weightStr);

                    if (reps <= 0 || weight < 0) {
                        Toast.makeText(ExerciseDetailActivity.this, "Reps and Weight must be positive numbers.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Create new Set and add to list
                    Set set = new Set(reps, weight);
                    setsList.add(set);
                    setsAdapter.notifyDataSetChanged(); // Notify adapter to refresh the RecyclerView
                    dialog.dismiss();
                } catch (NumberFormatException e) {
                    Toast.makeText(ExerciseDetailActivity.this, "Please enter valid numbers for Reps and Weight.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ExerciseDetailActivity.this, "Please enter Reps and Weight", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
}
