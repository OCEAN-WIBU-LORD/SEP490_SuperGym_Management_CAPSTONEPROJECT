package com.supergym.sep490_supergymmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.supergym.sep490_supergymmanagement.R;
import com.supergym.sep490_supergymmanagement.models.Set;

import java.util.List;

public class ExerciseSetDetailAdapter extends RecyclerView.Adapter<ExerciseSetDetailAdapter.ExerciseSetViewHolder> {

    private List<Set> sets;

    public ExerciseSetDetailAdapter(List<Set> sets) {
        this.sets = sets;
    }

    @NonNull
    @Override
    public ExerciseSetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_set_exercise, parent, false);
        return new ExerciseSetViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseSetViewHolder holder, int position) {
        Set set = sets.get(position);
        holder.repsTextView.setText("Reps: " + set.getRepetitions());
        holder.weightTextView.setText("Weight: " + set.getWeight() + " kg");

        // Set click listener cho nút xoá
        holder.deleteButton.setOnClickListener(v -> {
            // Xóa set khỏi danh sách
            sets.remove(position);
            // Cập nhật RecyclerView
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, sets.size());
        });
    }

    @Override
    public int getItemCount() {
        return sets != null ? sets.size() : 0;
    }

    static class ExerciseSetViewHolder extends RecyclerView.ViewHolder {
        TextView repsTextView, weightTextView;
        ImageButton deleteButton;

        ExerciseSetViewHolder(@NonNull View itemView) {
            super(itemView);
            repsTextView = itemView.findViewById(R.id.text_view_reps);
            weightTextView = itemView.findViewById(R.id.text_view_weight);
            deleteButton = itemView.findViewById(R.id.button_delete_set);
        }
    }
}
