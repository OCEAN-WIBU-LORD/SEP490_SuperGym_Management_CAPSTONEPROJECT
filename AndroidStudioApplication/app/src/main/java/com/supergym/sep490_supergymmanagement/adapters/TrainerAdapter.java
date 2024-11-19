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
    private List<Trainer> trainers;
    private Context context;
    private OnTrainerClickListener listener;

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

    public interface OnTrainerClickListener {
        void onTrainerClick(Trainer trainer); // Handle trainer item clicks
    }

    @Override
    public void onBindViewHolder(@NonNull TrainerViewHolder holder, int position) {
        Trainer trainer = trainers.get(position);

        holder.trainerName.setText(trainer.getName());

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TrainerDetailActivity.class);
            intent.putExtra("selectedTrainer", trainer);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return trainers.size();
    }

    public static class TrainerViewHolder extends RecyclerView.ViewHolder {
        TextView trainerName;

        public TrainerViewHolder(@NonNull View itemView) {
            super(itemView);
            trainerName = itemView.findViewById(R.id.trainer_name);
        }
    }
}
