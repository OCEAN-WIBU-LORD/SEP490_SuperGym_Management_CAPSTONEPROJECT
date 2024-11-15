/*
package com.supergym.sep490_supergymmanagement.adapters;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.supergym.sep490_supergymmanagement.models.Course;

import java.util.List;

public class MembershipAdapter extends RecyclerView.Adapter<MembershipAdapter.ViewHolder> {

    private List<Course> memberships;
    private Context context;

    public MembershipAdapter(List<Course> memberships, Context context) {
        this.memberships = memberships;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_membership, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Course membership = memberships.get(position);
        holder.bind(membership);
    }

    @Override
    public int getItemCount() {
        return memberships.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView priceTextView;
        private TextView descriptionTextView;
        private TextView durationTextView;
        private CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.membershipNameTextView);
            priceTextView = itemView.findViewById(R.id.membershipPriceTextView);
            descriptionTextView = itemView.findViewById(R.id.membershipDescriptionTextView);
            durationTextView = itemView.findViewById(R.id.membershipDurationTextView);
            cardView = itemView.findViewById(R.id.membershipCardView);
        }

        public void bind(Course membership) {
            nameTextView.setText(membership.getCourseName());
            priceTextView.setText("$" + membership.getCoursePrice() + "/month");
            descriptionTextView.setText(membership.getCourseContent());
            durationTextView.setText(membership.getCourseDuration());
            // You can set a background color if needed
            // cardView.setCardBackgroundColor(Color.parseColor(membership.getBackgroundColor()));
        }
    }
}
*/
