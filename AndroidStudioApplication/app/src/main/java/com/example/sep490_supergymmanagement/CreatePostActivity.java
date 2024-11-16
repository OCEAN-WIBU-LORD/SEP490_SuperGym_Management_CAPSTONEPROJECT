package com.example.sep490_supergymmanagement;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sep490_supergymmanagement.apiadapter.ApiService.ApiService;
import com.example.sep490_supergymmanagement.apiadapter.RetrofitClient;
import com.example.sep490_supergymmanagement.models.Post;
import com.example.sep490_supergymmanagement.models.PostCategory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePostActivity extends AppCompatActivity {

    // Khởi tạo các biến
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText etTitle, etContent;
    private Spinner spinnerCategory;
    private Button btnSelectImage, btnSubmitPost;
    private Uri imageUri;
    private ImageView ivSelectedImage;
    private List<PostCategory> categories = new ArrayList<>();
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSubmitPost = findViewById(R.id.btnSubmitPost);
        ivSelectedImage = findViewById(R.id.ivSelectedImage);

        // Khởi tạo Firebase Storage và Database
        storageReference = FirebaseStorage.getInstance().getReference("post");
        databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        apiService = RetrofitClient.getApiService();

        // Load categories từ API
        loadCategories();

        // Chọn ảnh
        btnSelectImage.setOnClickListener(v -> openImageSelector());

        // Đăng bài viết
        btnSubmitPost.setOnClickListener(v -> submitPost());
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
            Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
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
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(CreatePostActivity.this, android.R.layout.simple_spinner_item, categoryNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategory.setAdapter(adapter);
                } else {
                    Toast.makeText(CreatePostActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<PostCategory>> call, @NonNull Throwable t) {
                Toast.makeText(CreatePostActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitPost() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();
        String categoryId = categories.get(spinnerCategory.getSelectedItemPosition()).getCategoryId();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content) || imageUri == null) {
            Toast.makeText(this, "Please complete all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }
        uploadImageAndSavePost(title, content, categoryId);
    }

    private void uploadImageAndSavePost(String title, String content, String categoryId) {
        StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");

        fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
            String imageUrl = uri.toString();
            savePostToDatabase(title, content, categoryId, imageUrl);
        })).addOnFailureListener(e -> Toast.makeText(CreatePostActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void savePostToDatabase(String title, String content, String categoryId, String imageUrl) {
        String postId = databaseReference.push().getKey();
        String userId = currentUser != null ? currentUser.getUid() : "anonymous";
        Date date = new Date();

        // Tạo đối tượng `Post` với các node viết hoa chữ cái đầu
        Post post = new Post(postId, title, content, categoryId, imageUrl, date, userId);

        databaseReference.child(postId).setValue(post).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(CreatePostActivity.this, "Post created successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(CreatePostActivity.this, "Failed to create post", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

