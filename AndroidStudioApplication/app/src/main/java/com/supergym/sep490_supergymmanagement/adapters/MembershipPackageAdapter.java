package com.supergym.sep490_supergymmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.supergym.sep490_supergymmanagement.R;
import com.supergym.sep490_supergymmanagement.models.MembershipPackage;

import java.util.List;

public class MembershipPackageAdapter extends RecyclerView.Adapter<MembershipPackageAdapter.ViewHolder> {

    private List<MembershipPackage> membershipPackages;

    public MembershipPackageAdapter(List<MembershipPackage> membershipPackages) {
        this.membershipPackages = membershipPackages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_layout, parent, false); // Use your CardView layout XML
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MembershipPackage membership = membershipPackages.get(position);

        holder.nameTextView.setText(membership.getName());
        holder.priceTextView.setText("$" + membership.getPrice());

        // Display either duration or session count
        if (membership.getDurationMonths() != null && membership.getDurationMonths() > 0) {
            holder.detailsTextView.setText("Duration: " + membership.getDurationMonths() + " month(s)");
        } else if (membership.getSessionCount() != null && membership.getSessionCount() > 0) {
            holder.detailsTextView.setText("Sessions: " + membership.getSessionCount());
        } else {
            holder.detailsTextView.setText("No specific details available.");
        }
    }

    @Override
    public int getItemCount() {
        return membershipPackages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView priceTextView;
        TextView detailsTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView); // Replace with the actual ID in your layout
            priceTextView = itemView.findViewById(R.id.priceTextView); // Replace with the actual ID
            detailsTextView = itemView.findViewById(R.id.basicPackageTextView); // Replace with the actual ID
        }
    }
}
