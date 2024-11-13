package com.example.sep490_supergymmanagement.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sep490_supergymmanagement.PostDetailActivity; // Import the new activity
import com.example.sep490_supergymmanagement.R;
import com.example.sep490_supergymmanagement.models.Post;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.VH> {
    private List<Post> data;
    private Context context;

    public PostAdapter(List<Post> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.post_card, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Post p = data.get(position);
        holder.setData(p);

        // Set onClickListener to open PostDetailActivity with post data
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra("postId", p.getPostId()); // Truyền postId
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class VH extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvDate;
        private ImageView ivAvatar;

        private void bindingView() {
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
        }

        public VH(@NonNull View itemView) {
            super(itemView);
            bindingView();
        }

        public void setData(Post p) {
            tvTitle.setText(p.getTitle());

            // Format post date using getParsedDate()
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date parsedDate = p.getParsedDate(); // Parse the date from the string
            if (parsedDate != null) {
                String dateString = dateFormat.format(parsedDate);
                tvDate.setText("Ngày đăng: " + dateString);
            } else {
                tvDate.setText("Ngày đăng: N/A"); // Handle null date case
            }

            // Load image with Picasso
            Picasso.get()
                    .load(p.getThumbnailUrl())
                    .error(R.drawable.avatar)       // Error image
                    .into(ivAvatar);
        }
    }

}
