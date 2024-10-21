package com.example.sep490_supergymmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sep490_supergymmanagement.R;
import com.example.sep490_supergymmanagement.models.Set;

import java.util.List;

// ExerciseSetDetailAdapter.java
public class ExerciseSetDetailAdapter extends RecyclerView.Adapter<ExerciseSetDetailAdapter.ExerciseSetViewHolder> {

    private List<Set> setsList;

    public ExerciseSetDetailAdapter(List<Set> setsList) {
        this.setsList = setsList;
    }

    @NonNull
    @Override
    public ExerciseSetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_set_exercise, parent, false);
        return new ExerciseSetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseSetViewHolder holder, int position) {
        Set set = setsList.get(position);
        holder.repsTextView.setText("Reps: " + set.getRepetitions());
        holder.weightTextView.setText("Weight: " + set.getWeight() + " kg");
    }

    @Override
    public int getItemCount() {
        return setsList.size();
    }

    // ViewHolder for the RecyclerView
    class ExerciseSetViewHolder extends RecyclerView.ViewHolder {
        TextView repsTextView, weightTextView;

        public ExerciseSetViewHolder(@NonNull View itemView) {
            super(itemView);
            repsTextView = itemView.findViewById(R.id.text_view_reps);
            weightTextView = itemView.findViewById(R.id.text_view_weight);
        }
    }
}
