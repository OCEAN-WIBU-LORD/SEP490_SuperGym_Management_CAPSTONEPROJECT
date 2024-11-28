package com.supergym.sep490_supergymmanagement;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.supergym.sep490_supergymmanagement.apiadapter.ApiService.ApiService;
import com.supergym.sep490_supergymmanagement.apiadapter.RetrofitClient;
import com.supergym.sep490_supergymmanagement.models.Post;
import com.supergym.sep490_supergymmanagement.models.PostCategory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdatePostActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etTitle, etContent;
    private Spinner spinnerCategory;
    private ImageView ivSelectedImage;
    private Button btnSelectImage, btnUpdatePost;
    private Uri imageUri;
    private List<PostCategory> categories = new ArrayList<>();
    private String postId;
    private String currentImageUrl;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_post);

        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        ivSelectedImage = findViewById(R.id.ivSelectedImage);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnUpdatePost = findViewById(R.id.btnUpdatePost);

        apiService = RetrofitClient.getApiService(this);
        postId = getIntent().getStringExtra("postId");

        loadCategories();
        loadPostDetails(postId);

        btnSelectImage.setOnClickListener(v -> openImageSelector());
        btnUpdatePost.setOnClickListener(v -> updatePost());
    }

    private void openImageSelector() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            ivSelectedImage.setImageURI(imageUri);
        }
    }

    private void loadCategories() {
        Call<List<PostCategory>> call = apiService.getCategories();
        call.enqueue(new Callback<List<PostCategory>>() {
            @Override
            public void onResponse(@NonNull Call<List<PostCategory>> call, @NonNull Response<List<PostCategory>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories = response.body();
                    List<String> categoryNames = new ArrayList<>();
                    for (PostCategory category : categories) {
                        categoryNames.add(category.getName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(UpdatePostActivity.this, android.R.layout.simple_spinner_item, categoryNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategory.setAdapter(adapter);
                } else {
                    Toast.makeText(UpdatePostActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<PostCategory>> call, @NonNull Throwable t) {
                Toast.makeText(UpdatePostActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPostDetails(String postId) {
        Call<Post> call = apiService.getPostById(postId);
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(@NonNull Call<Post> call, @NonNull Response<Post> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Post post = response.body();
                    etTitle.setText(post.getTitle());
                    etContent.setText(post.getContent());
                    currentImageUrl = post.getThumbnailUrl();
                    Glide.with(UpdatePostActivity.this).load(currentImageUrl).into(ivSelectedImage);
                } else {
                    Toast.makeText(UpdatePostActivity.this, "Failed to load post details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Post> call, @NonNull Throwable t) {
                Toast.makeText(UpdatePostActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePost() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();
        String categoryId = categories.get(spinnerCategory.getSelectedItemPosition()).getCategoryId();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy `userId` của người dùng hiện tại từ Firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = currentUser.getUid(); // Đảm bảo `userId` viết thường

        // Tạo đối tượng `updatedPost` với `userId`
        Post updatedPost = new Post(postId, title, content, categoryId, currentImageUrl, new Date(), userId);

        // Kiểm tra nếu người dùng chọn ảnh mới, tải lên Firebase trước
        if (imageUri != null) {
            uploadImageAndSavePost(updatedPost);
        } else {
            savePostToDatabase(updatedPost);
        }
    }

    private void uploadImageAndSavePost(Post post) {
        StorageReference fileReference = FirebaseStorage.getInstance().getReference("post_images").child(System.currentTimeMillis() + ".jpg");
        fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
            post.setThumbnailUrl(uri.toString());
            savePostToDatabase(post);
        })).addOnFailureListener(e -> Toast.makeText(UpdatePostActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void savePostToDatabase(Post post) {
        // Ghi log để kiểm tra dữ liệu `post` trước khi gọi API
        Log.d("UpdatePost", "Updating post with ID: " + post.getPostId());
        Log.d("UpdatePost", "Title: " + post.getTitle());
        Log.d("UpdatePost", "Content: " + post.getContent());
        Log.d("UpdatePost", "Category ID: " + post.getCategoryId());
        Log.d("UpdatePost", "Thumbnail URL: " + post.getThumbnailUrl());
        Log.d("UpdatePost", "User ID: " + post.getUserId()); // Đảm bảo `userId` viết thường

        Call<Void> call = apiService.updatePost(post.getPostId(), post);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UpdatePostActivity.this, "Post updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.e("UpdatePost", "Failed to update post: " + response.code() + " - " + response.message());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("UpdatePost", "Error body: " + response.errorBody().string());
                        } catch (IOException e) {
                            Log.e("UpdatePost", "Error reading error body", e);
                        }
                    }
                    Toast.makeText(UpdatePostActivity.this, "Failed to update post", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e("UpdatePost", "API call failed: " + t.getMessage(), t);
                Toast.makeText(UpdatePostActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
