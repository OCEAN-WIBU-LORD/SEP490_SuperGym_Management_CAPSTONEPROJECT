package com.supergym.sep490_supergymmanagement.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.supergym.sep490_supergymmanagement.R;
import com.supergym.sep490_supergymmanagement.UpdatePostActivity;
import com.supergym.sep490_supergymmanagement.apiadapter.ApiService.ApiService;
import com.supergym.sep490_supergymmanagement.apiadapter.RetrofitClient;
import com.supergym.sep490_supergymmanagement.models.Post;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManagePostAdapter extends RecyclerView.Adapter<ManagePostAdapter.ManagePostViewHolder> {

    private final List<Post> posts;
    private final Context context;
    private final ApiService apiService;

    public ManagePostAdapter(List<Post> posts, Context context) {
        this.posts = posts;
        this.context = context;
        this.apiService = RetrofitClient.getApiService(context); // Khởi tạo ApiService
    }

    @NonNull
    @Override
    public ManagePostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ManagePostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManagePostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.setData(post);

        holder.btnDeletePost.setOnClickListener(v -> showDeleteConfirmationDialog(post, position));

        holder.btnUpdatePost.setOnClickListener(v -> {
            Intent intent = new Intent(context, UpdatePostActivity.class);
            intent.putExtra("postId", post.getPostId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    private void showDeleteConfirmationDialog(Post post, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this post?")
                .setPositiveButton("Delete", (dialog, which) -> deletePost(post, position))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deletePost(Post post, int position) {
        apiService.deletePost(post.getPostId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    posts.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("ManagePostAdapter", "Delete failed: " + response.code() + " - " + response.message());
                    Toast.makeText(context, "Failed to delete post", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e("ManagePostAdapter", "API call failed: " + t.getMessage(), t);
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class ManagePostViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final TextView tvDate;
        private final Button btnDeletePost;
        private final Button btnUpdatePost;

        public ManagePostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnDeletePost = itemView.findViewById(R.id.btnDeletePost);
            btnUpdatePost = itemView.findViewById(R.id.btnUpdatePost);
        }

        public void setData(Post post) {
            tvTitle.setText(post.getTitle());
            Date parsedDate = post.getParsedDate();
            if (parsedDate != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String dateString = dateFormat.format(parsedDate);
                tvDate.setText("Ngày đăng: " + dateString);
            } else {
                tvDate.setText("Ngày đăng: N/A");
            }
        }
    }
}
