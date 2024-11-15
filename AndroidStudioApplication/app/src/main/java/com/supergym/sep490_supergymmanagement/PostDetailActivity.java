package com.supergym.sep490_supergymmanagement;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.supergym.sep490_supergymmanagement.apiadapter.ApiService.ApiService; // Import ApiService
import com.supergym.sep490_supergymmanagement.apiadapter.RetrofitClient; // Import RetrofitClient
import com.supergym.sep490_supergymmanagement.models.Post;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

        // Initialize back button and set up listener
        CardView btnReturn = findViewById(R.id.returnCardView);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

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

                    // Gán dữ liệu cho các view
                    tvTitleDetail.setText(post.getTitle());

                    // Chuyển đổi trường date
                    String formattedDate;
                    Date parsedDate = post.getParsedDate(); // Sử dụng phương thức getParsedDate() từ model Post

                    if (parsedDate != null) {
                        SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        formattedDate = outputDateFormat.format(parsedDate); // Định dạng lại ngày
                    } else {
                        formattedDate = "N/A"; // Giá trị mặc định nếu chuyển đổi thất bại
                    }

                    tvDateDetail.setText("Date: " + formattedDate);

                    tvContentDetail.setText(post.getContent());
                    tvAuthorDetail.setText(post.getAuthorName());

                    // Tải hình ảnh với Picasso
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
