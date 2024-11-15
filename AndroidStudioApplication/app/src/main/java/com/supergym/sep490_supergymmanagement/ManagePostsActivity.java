package com.supergym.sep490_supergymmanagement;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.supergym.sep490_supergymmanagement.adapters.ManagePostAdapter;
import com.supergym.sep490_supergymmanagement.apiadapter.RetrofitClient;
import com.supergym.sep490_supergymmanagement.models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManagePostsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPosts;
    private ManagePostAdapter managePostAdapter;
    private List<Post> userPosts = new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_posts);

        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo RecyclerView và adapter
        recyclerViewPosts = findViewById(R.id.recyclerViewPosts);
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(this));
        managePostAdapter = new ManagePostAdapter(userPosts, this);
        recyclerViewPosts.setAdapter(managePostAdapter);

        // Gọi API để tải bài viết của người dùng hiện tại
        loadUserPosts(currentUser.getUid());
    }

    private void loadUserPosts(String userId) {
        Call<List<Post>> call = RetrofitClient.getApiService().getPostsByUserId(userId);
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(@NonNull Call<List<Post>> call, @NonNull Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userPosts.clear();
                    userPosts.addAll(response.body());
                    managePostAdapter.notifyDataSetChanged();
                    Log.d("ManagePostsActivity", "Loaded user posts: " + userPosts.size());
                } else {
                    Log.e("ManagePostsActivity", "Failed to load user posts. Response Code: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("ManagePostsActivity", "Error body: " + response.errorBody().string());
                        } catch (IOException e) {
                            Log.e("ManagePostsActivity", "Error reading error body: " + e.getMessage());
                        }
                    }
                    Toast.makeText(ManagePostsActivity.this, "Failed to load user posts", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Post>> call, @NonNull Throwable t) {
                Toast.makeText(ManagePostsActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show();
                Log.e("ManagePostsActivity", "Error loading user posts: " + t.getMessage());
            }
        });
    }

}
