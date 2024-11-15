package com.supergym.sep490_supergymmanagement.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.supergym.sep490_supergymmanagement.R;
import com.supergym.sep490_supergymmanagement.models.Schedule;

import java.util.List;

public class ClassForDayAdapter extends RecyclerView.Adapter<ClassForDayAdapter.ClassViewHolder> {
    private List<Schedule> classList;  // List of Schedule objects
    private Context context;

    public ClassForDayAdapter(List<Schedule> classList, Context context) {
        this.classList = classList;
        this.context = context;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_schedule, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        Schedule schedule = classList.get(position);

        // Populate class details
        holder.tvTime.setText(schedule.getTime());
        holder.tvClassTitle.setText(schedule.getClassTitle());
        holder.tvTrainerName.setText(schedule.getTrainerName());
        holder.tvSeatsAvailable.setText("Available seats: " + schedule.getSeatsAvailable());

        // Handle register/cancel button state
        if (schedule.isRegistered()) {
            holder.btnSignUp.setText("Cancel Register");
            holder.btnSignUp.setBackgroundColor(context.getResources().getColor(R.color.red)); // Optional: Change button color when registered
            holder.btnSignUp.setOnClickListener(v -> {
                // Call cancel registration method
                cancelRegistration(schedule.getScheduleId(), position);
            });
        } else {
            holder.btnSignUp.setText("Register");
            holder.btnSignUp.setBackgroundColor(context.getResources().getColor(R.color.green)); // Optional: Change button color for registration
            holder.btnSignUp.setOnClickListener(v -> {
                // Call register method
                registerForClass(schedule.getScheduleId(), position);
            });
        }
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    // ViewHolder class
    public static class ClassViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime, tvClassTitle, tvTrainerName, tvSeatsAvailable;
        Button btnSignUp;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvClassTitle = itemView.findViewById(R.id.tvClassTitle);
            tvTrainerName = itemView.findViewById(R.id.tvTrainerName);
            tvSeatsAvailable = itemView.findViewById(R.id.tvSeatsAvailable);
            btnSignUp = itemView.findViewById(R.id.btnSignUp);
        }
    }

    // Placeholder method to handle registration logic
    private void registerForClass(String scheduleId, int position) {
        // Add your Firebase Realtime Database registration logic here
        // Example: Push the scheduleId to the user's registered classes list in the database

        // Once registered, update the schedule object
        classList.get(position).setRegistered(true);
        notifyItemChanged(position);
    }

    // Placeholder method to handle unregistration logic
    private void cancelRegistration(String scheduleId, int position) {
        // Add your Firebase Realtime Database unregistration logic here
        // Example: Remove the scheduleId from the user's registered classes list in the database

        // Once unregistered, update the schedule object
        classList.get(position).setRegistered(false);
        notifyItemChanged(position);
    }
}
