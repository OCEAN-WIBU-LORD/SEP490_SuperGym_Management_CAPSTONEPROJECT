package com.supergym.sep490_supergymmanagement.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        View view = LayoutInflater.from(context).inflate(R.layout.membership_item, parent, false);
        return new MembershipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MembershipViewHolder holder, int position) {
        MembershipPackage membershipPackage = membershipPackages.get(position);
        holder.nameTextView.setText(membershipPackage.getName());
        holder.priceTextView.setText(String.format("$%.2f", membershipPackage.getPrice()));
        holder.durationTextView.setText(membershipPackage.getDurationMonths() + " months");
        holder.sessionsTextView.setText(membershipPackage.getSessionCount() + " sessions");
    }

    @Override
    public int getItemCount() {
        return membershipPackages.size();
    }

    public static class MembershipViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, priceTextView, durationTextView, sessionsTextView;

        public MembershipViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.packageNameTextView);
            priceTextView = itemView.findViewById(R.id.packagePriceTextView);
            durationTextView = itemView.findViewById(R.id.packageDurationTextView);
            sessionsTextView = itemView.findViewById(R.id.packageSessionsTextView);
        }
    }
}
