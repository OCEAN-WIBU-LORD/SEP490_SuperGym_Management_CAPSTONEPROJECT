package com.supergym.sep490_supergymmanagement.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.supergym.sep490_supergymmanagement.R;
import com.supergym.sep490_supergymmanagement.models.Trainer;

import java.util.List;

public class TrainerAdapter extends RecyclerView.Adapter<TrainerAdapter.TrainerViewHolder> {

    private List<Trainer> trainerList;
    private Context context;
    private OnTrainerClickListener listener;

    // Interface for handling click events
    public interface OnTrainerClickListener {
        void onTrainerClick(Trainer trainer);
    }

    // Constructor
    public TrainerAdapter(List<Trainer> trainerList, Context context, OnTrainerClickListener listener) {
        this.trainerList = trainerList;
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
        Trainer trainer = trainerList.get(position);

        holder.nameTextView.setText(trainer.getName());
        holder.boxingTextView.setVisibility(trainer.isTrainerBoxing() ? View.VISIBLE : View.GONE);
        holder.gymTextView.setVisibility(trainer.isTrainerGym() ? View.VISIBLE : View.GONE);

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTrainerClick(trainer);
            }
        });
    }

    @Override
    public int getItemCount() {
        return trainerList.size();
    }

    // ViewHolder class
    static class TrainerViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView boxingTextView;
        TextView gymTextView;

        public TrainerViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.trainer_name);
            boxingTextView = itemView.findViewById(R.id.trainer_boxing);
            gymTextView = itemView.findViewById(R.id.trainer_gym);
        }
    }
}
