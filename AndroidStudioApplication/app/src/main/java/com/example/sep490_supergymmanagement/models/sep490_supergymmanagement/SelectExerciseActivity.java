package com.example.sep490_supergymmanagement.models.sep490_supergymmanagement;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sep490_supergymmanagement.R;
import com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.adapters.ExerciseSelectionAdapter;
import com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.models.Exercise;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SelectExerciseActivity extends AppCompatActivity {

    private EditText searchExerciseInput;
    private RecyclerView exerciseListRecyclerView;
    private ExerciseSelectionAdapter exerciseAdapter;
    private List<Exercise> exerciseList = new ArrayList<>();
    private List<Exercise> originalExerciseList = new ArrayList<>();
    private List<Exercise> selectedExercises = new ArrayList<>();
    private Button doneButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_exercise);

        // Khởi tạo các thành phần giao diện
        searchExerciseInput = findViewById(R.id.search_exercise_input);
        exerciseListRecyclerView = findViewById(R.id.exercise_list_recycler_view);
        doneButton = findViewById(R.id.done_button);

        // Nhận danh sách ID bài tập đã chọn từ AddSessionActivity
        ArrayList<String> selectedExerciseIds = getIntent().getStringArrayListExtra("selectedExerciseIds");

        // Thiết lập RecyclerView với ExerciseSelectionAdapter
        exerciseAdapter = new ExerciseSelectionAdapter(exerciseList, selectedExercises);
        exerciseListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        exerciseListRecyclerView.setAdapter(exerciseAdapter);

        // Tính năng tìm kiếm
        searchExerciseInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterExercises(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Fetch exercises from database
        fetchExercisesFromFirebase(selectedExerciseIds);

        // Done button click listener
        doneButton.setOnClickListener(view -> {
            // Tạo một danh sách ID bài tập đã chọn
            ArrayList<String> selectedExerciseIdsResult = new ArrayList<>();
            for (Exercise exercise : selectedExercises) {
                selectedExerciseIdsResult.add(exercise.getId()); // Lưu ID của bài tập
            }

            // Pass selected exercises IDs back to AddSessionActivity
            Intent resultIntent = new Intent();
            resultIntent.putStringArrayListExtra("selectedExerciseIds", selectedExerciseIdsResult);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private void fetchExercisesFromFirebase(ArrayList<String> selectedExerciseIds) {
        DatabaseReference exercisesRef = FirebaseDatabase.getInstance().getReference("exercises");
        exercisesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                exerciseList.clear();
                originalExerciseList.clear(); // Xóa danh sách cũ trước khi thêm dữ liệu mới
                for (DataSnapshot exerciseSnapshot : dataSnapshot.getChildren()) {
                    Exercise exercise = exerciseSnapshot.getValue(Exercise.class);
                    if (exercise != null) {
                        exerciseList.add(exercise);
                        originalExerciseList.add(exercise); // Thêm vào danh sách gốc
                        // Đánh dấu các bài tập đã chọn trước đó
                        if (selectedExerciseIds.contains(exercise.getId())) {
                            selectedExercises.add(exercise);
                        }
                    }
                }
                exerciseAdapter.notifyDataSetChanged(); // Cập nhật adapter
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void filterExercises(String query) {
        if (query.isEmpty()) {
            // Nếu ô tìm kiếm trống, hiển thị lại toàn bộ danh sách bài tập
            exerciseAdapter.updateList(originalExerciseList);
        } else {
            // Lọc các bài tập dựa trên nội dung của ô tìm kiếm
            List<Exercise> filteredList = new ArrayList<>();
            for (Exercise exercise : originalExerciseList) {
                if (exercise.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(exercise);
                }
            }
            // Cập nhật danh sách với kết quả tìm kiếm
            exerciseAdapter.updateList(filteredList);
        }
    }
}
