package com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.adapters;
//Adapter cho việc thêm Exercise
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sep490_supergymmanagement.R;
import com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.models.Exercise;
import com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.models.Set;

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private List<Exercise> exerciseList;

    public ExerciseAdapter(List<Exercise> exerciseList) {
        this.exerciseList = exerciseList;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Exercise exercise = exerciseList.get(position);
        holder.exerciseName.setText(exercise.getName());

        // Set up RecyclerView for the sets
        List<Set> sets = exercise.getSets();
        SetAdapter setAdapter = new SetAdapter(sets);
        holder.setRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.setRecyclerView.setAdapter(setAdapter);
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseName;
        RecyclerView setRecyclerView;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.exercise_name);
            setRecyclerView = itemView.findViewById(R.id.set_recycler_view);
        }
    }
}
