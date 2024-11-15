package com.supergym.sep490_supergymmanagement.adapters;

// LogAdapter.java
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.supergym.sep490_supergymmanagement.R;
import com.supergym.sep490_supergymmanagement.models.LogEntry;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {

    private List<LogEntry> logEntries;

    public LogAdapter(List<LogEntry> logEntries) {
        this.logEntries = logEntries;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_item, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        LogEntry logEntry = logEntries.get(position);

        // Set the data for the TextViews
        holder.userName.setText(logEntry.getUserName());
        holder.role.setText("Role: " + logEntry.getRole());
        holder.checkInTime.setText("Check-in Time: " + logEntry.getCheckInTime());
        holder.checkOutTime.setText("Check-out Time: " + logEntry.getCheckOutTime());

        // Use Glide to load the image from Firebase Storage into the ImageView
        Glide.with(holder.itemView.getContext())
                .load(logEntry.getImageUrl()) // Firebase Storage URL
                .placeholder(R.drawable.ic_placeholder) // Placeholder image while loading
                .into(holder.userImage); // ImageView where the image will be displayed
    }


    @Override
    public int getItemCount() {
        return logEntries.size();
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {
        ImageView userImage;  // Add ImageView to display the face
        TextView userName, role, checkInTime, checkOutTime;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.ivUserImage);  // ImageView for the user's face
            userName = itemView.findViewById(R.id.tvUserName);
            role = itemView.findViewById(R.id.tvRole);
            checkInTime = itemView.findViewById(R.id.tvCheckInTime);
            checkOutTime = itemView.findViewById(R.id.tvCheckOutTime);
        }
    }
}
