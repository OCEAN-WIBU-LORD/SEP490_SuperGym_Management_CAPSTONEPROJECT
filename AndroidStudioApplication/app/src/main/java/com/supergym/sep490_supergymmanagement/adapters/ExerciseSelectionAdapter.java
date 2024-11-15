package com.supergym.sep490_supergymmanagement.adapters;
//Adapter cho việc chọn bài tập trong session
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.supergym.sep490_supergymmanagement.R;
import com.supergym.sep490_supergymmanagement.models.Exercise;

import java.util.List;

public class ExerciseSelectionAdapter extends RecyclerView.Adapter<ExerciseSelectionAdapter.ExerciseViewHolder> {

    private List<Exercise> exercises;
    private List<Exercise> selectedExercises;

    public ExerciseSelectionAdapter(List<Exercise> exercises, List<Exercise> selectedExercises) {
        this.exercises = exercises;
        this.selectedExercises = selectedExercises;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise_selection, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        holder.exerciseName.setText(exercise.getName());

        // Đặt trạng thái cho checkbox dựa trên selectedExercises
        holder.exerciseCheckbox.setOnCheckedChangeListener(null); // Ngăn lỗi trạng thái trước khi đặt trạng thái mới
        holder.exerciseCheckbox.setChecked(selectedExercises.contains(exercise));

        // Thiết lập sự kiện cho checkbox
        holder.exerciseCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selectedExercises.contains(exercise)) {
                    selectedExercises.add(exercise);
                }
            } else {
                selectedExercises.remove(exercise);
            }
        });
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    // Phương thức cập nhật danh sách bài tập
    public void updateList(List<Exercise> newList) {
        exercises.clear();
        exercises.addAll(newList);
        notifyDataSetChanged();
    }


    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseName;
        CheckBox exerciseCheckbox; // Khai báo checkbox

        ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.exercise_name);
            exerciseCheckbox = itemView.findViewById(R.id.exercise_checkbox); // Khởi tạo checkbox
        }
    }
}
