package com.supergym.sep490_supergymmanagement.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.supergym.sep490_supergymmanagement.R;
import com.supergym.sep490_supergymmanagement.models.Review;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private final List<Review> reviewList;
    private final Context context;

    public ReviewAdapter(Context context, List<Review> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviewList.get(position);

        // Set review text
        holder.reviewText.setText(review.getReview());

        // Fetch user data from Firebase using the userId
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(review.getUserId());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Get user name and avatar Base64 string
                    String userName = snapshot.child("name").getValue(String.class);
                    String avatarBase64 = snapshot.child("userAvatar").getValue(String.class);

                    // Set user name
                    holder.userName.setText(userName);

                    // Decode Base64 avatar string and set it to ImageView
                    if (avatarBase64 != null) {
                        try {
                            Bitmap avatarBitmap = base64ToBitmap(avatarBase64);
                            holder.userAvatar.setImageBitmap(avatarBitmap);
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle potential database errors
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    // Utility method to decode Base64 string to Bitmap
    public Bitmap base64ToBitmap(String base64Str) throws IllegalArgumentException {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userAvatar;
        TextView userName, reviewText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Bind views
            userAvatar = itemView.findViewById(R.id.userAvatar); // Matches your review_item layout
            userName = itemView.findViewById(R.id.textView14);    // Matches your review_item layout
            reviewText = itemView.findViewById(R.id.reviewText); // Matches your review_item layout
        }
    }
}
