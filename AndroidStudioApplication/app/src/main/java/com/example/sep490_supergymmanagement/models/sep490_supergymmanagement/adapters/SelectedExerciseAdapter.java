package com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sep490_supergymmanagement.R;
import com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.models.Exercise;

import java.util.List;

public class SelectedExerciseAdapter extends RecyclerView.Adapter<SelectedExerciseAdapter.SelectedExerciseViewHolder> {

    private List<Exercise> exercises;
    private OnItemClickListener onItemClickListener;

    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(Exercise exercise);
    }

    // Constructor that accepts the exercises list and listener
    public SelectedExerciseAdapter(List<Exercise> exercises, OnItemClickListener listener) {
        this.exercises = exercises;
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public SelectedExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_exercise, parent, false);
        return new SelectedExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedExerciseViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        holder.exerciseName.setText(exercise.getName());

        // Set click listener to open detail view
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(exercise);
            }
        });
    }

    @Override
    public int getItemCount() {
        return exercises != null ? exercises.size() : 0;
    }

    // Update exercise list and notify adapter
    public void updateExerciseList(List<Exercise> updatedExercises) {
        this.exercises = updatedExercises;
        notifyDataSetChanged();
    }

    static class SelectedExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseName;

        SelectedExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.exercise_name);
        }
    }
}
