package com.supergym.sep490_supergymmanagement.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.supergym.sep490_supergymmanagement.MembershipActivity;
import com.supergym.sep490_supergymmanagement.R;
import com.supergym.sep490_supergymmanagement.models.MembershipPackage;

import java.util.List;

public class MembershipAdapter extends RecyclerView.Adapter<MembershipAdapter.MembershipViewHolder> {
    private Context context;
    private List<MembershipPackage> membershipPackages;

    public MembershipAdapter(Context context, List<MembershipPackage> membershipPackages) {
        this.context = context;
        this.membershipPackages = membershipPackages;
    }

    @NonNull
    @Override
    public MembershipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_view_layout, parent, false);
        return new MembershipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MembershipViewHolder holder, int position) {
        MembershipPackage membershipPackage = membershipPackages.get(position);
        holder.nameTextView.setText(membershipPackage.getName());

        // Format the price with commas and append "VND"
        double price = membershipPackage.getPrice();
        String formattedPrice = String.format("%,d VND", (long) price);
        holder.priceTextView.setText(formattedPrice);

        holder.durationTextView.setText("");

        // Set OnClickListener for the "Generate QR Code" button
        holder.generateQrCodeButton.setOnClickListener(v -> {
            // Get the gymMembershipId from the MembershipPackage
            String gymMembershipId = membershipPackage.getGymMembershipId(); // Assuming 'getId()' method exists
            // Pass the gymMembershipId to the MembershipActivity
            if (context instanceof MembershipActivity) {
                ((MembershipActivity) context).generateQrCodeDialog(gymMembershipId);
            }
        });
    }


    @Override
    public int getItemCount() {
        return membershipPackages.size();
    }

    public static class MembershipViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, priceTextView, durationTextView, sessionsTextView;
        MaterialButton generateQrCodeButton; // Button for generating QR code

        public MembershipViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            durationTextView = itemView.findViewById(R.id.basicPackageTextView);
            //sessionsTextView = itemView.findViewById(R.id.packageSessionsTextView);
            generateQrCodeButton = itemView.findViewById(R.id.selectButton); // Button initialization
        }
    }
}

