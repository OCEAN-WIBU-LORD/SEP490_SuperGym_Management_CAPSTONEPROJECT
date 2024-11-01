package com.example.sep490_supergymmanagement;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class PostDetailActivity extends AppCompatActivity {

    private TextView tvTitleDetail, tvDateDetail, tvContentDetail, tvAuthorDetail;
    private ImageView ivThumbnailDetail;

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

        // Retrieve post data passed from intent
        String title = getIntent().getStringExtra("title");
        String date = getIntent().getStringExtra("date");
        String content = getIntent().getStringExtra("content");
        String thumbnailUrl = getIntent().getStringExtra("thumbnailUrl");
        String author = getIntent().getStringExtra("author");

        // Set data to views
        tvTitleDetail.setText(title);
        tvDateDetail.setText("Date: " + date);
        tvContentDetail.setText(content);
        tvAuthorDetail.setText(author);

        // Load image with Picasso
        Picasso.get().load(thumbnailUrl).error(R.drawable.avatar).into(ivThumbnailDetail);
    }
}
