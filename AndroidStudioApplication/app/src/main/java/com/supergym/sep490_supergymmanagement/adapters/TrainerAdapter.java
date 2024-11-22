package com.supergym.sep490_supergymmanagement.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.supergym.sep490_supergymmanagement.R;
import com.supergym.sep490_supergymmanagement.TrainerDetailActivity;
import com.supergym.sep490_supergymmanagement.models.Trainer;

import java.util.List;

public class TrainerAdapter extends RecyclerView.Adapter<TrainerAdapter.TrainerViewHolder> {
    private final List<Trainer> trainers;
    private final Context context;
    private final OnTrainerClickListener listener;

    public TrainerAdapter(List<Trainer> trainers, Context context, OnTrainerClickListener listener) {
        this.trainers = trainers;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TrainerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_trainer, parent, false);
        return new TrainerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainerViewHolder holder, int position) {
        Trainer trainer = trainers.get(position);

        // Set trainer name
        holder.trainerName.setText(trainer.getName());

        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTrainerClick(trainer);
            }
        });
    }

    @Override
    public int getItemCount() {
        return trainers.size();
    }

    // ViewHolder class for Trainer
    public static class TrainerViewHolder extends RecyclerView.ViewHolder {
        TextView trainerName;

        public TrainerViewHolder(@NonNull View itemView) {
            super(itemView);
            trainerName = itemView.findViewById(R.id.trainer_name);
        }
    }

    // Interface for handling trainer clicks
    public interface OnTrainerClickListener {
        void onTrainerClick(Trainer trainer);
    }
}
