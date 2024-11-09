package com.example.sep490_supergymmanagement;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sep490_supergymmanagement.apiadapter.ApiService.ApiService; // Import ApiService
import com.example.sep490_supergymmanagement.apiadapter.RetrofitClient; // Import RetrofitClient
import com.example.sep490_supergymmanagement.models.Post;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDetailActivity extends AppCompatActivity {

    private TextView tvTitleDetail, tvDateDetail, tvContentDetail, tvAuthorDetail;
    private ImageView ivThumbnailDetail;
    private ApiService apiService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        // Bind views
        tvTitleDetail = findViewById(R.id.tvTitleDetail);
        tvDateDetail = findViewById(R.id.tvDateDetail);
        tvContentDetail = findViewById(R.id.tvContentDetail);
        ivThumbnailDetail = findViewById(R.id.ivThumbnailDetail);
        tvAuthorDetail = findViewById(R.id.tvAuthorDetail);

        // Initialize ApiService
        apiService = RetrofitClient.getInstance().create(ApiService.class);

        // Get postId from intent
        String postId = getIntent().getStringExtra("postId");
        if (postId != null) {
            fetchPostDetails(postId); // Fetch post details using postId
        } else {
            Toast.makeText(this, "Post ID is missing!", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchPostDetails(String postId) {
        apiService.getPostById(postId).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Post post = response.body();

                    // Bind data to views
                    tvTitleDetail.setText(post.getTitle());

                    // Format the date to show only day, month, and year
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    String formattedDate = dateFormat.format(post.getDate());
                    tvDateDetail.setText("Date: " + formattedDate);

                    tvContentDetail.setText(post.getContent());
                    tvAuthorDetail.setText(post.getAuthorName()); // Hiển thị tên tác giả

                    // Load hình ảnh với Picasso
                    Picasso.get().load(post.getThumbnailUrl()).error(R.drawable.avatar).into(ivThumbnailDetail);
                } else {
                    Toast.makeText(PostDetailActivity.this, "Failed to fetch post details.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Log.e("PostDetailActivity", "Error fetching post details", t);
                Toast.makeText(PostDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
