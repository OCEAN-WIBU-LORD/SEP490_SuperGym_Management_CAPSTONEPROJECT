package com.supergym.sep490_supergymmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import androidx.appcompat.widget.SearchView;

import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.supergym.sep490_supergymmanagement.adapters.PostAdapter;
import com.supergym.sep490_supergymmanagement.apiadapter.RetrofitClient;
import com.supergym.sep490_supergymmanagement.models.Post;
import com.supergym.sep490_supergymmanagement.models.PostCategory;
import com.supergym.sep490_supergymmanagement.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostListActivity extends AppCompatActivity {

    private SearchView searchView;
    private Spinner categorySpinner;
    private RecyclerView postRecyclerView;
    private PostAdapter postAdapter;
    private List<Post> allPosts = new ArrayList<>();
    private List<Post> filteredPosts = new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseUser userDetails;
    private Button managePostButton; // Button for managing posts
    private List<String> categoryIds = new ArrayList<>(); // Store category IDs for mapping

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);

        // Initialize back button and set up listener
        CardView btnReturn = findViewById(R.id.returnCardView);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Initialize the "Manage My Posts" button
        managePostButton = findViewById(R.id.managePostButton);
        managePostButton.setVisibility(View.GONE); // Initially hide the button
        managePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostListActivity.this, ManagePostsActivity.class);
                startActivity(intent);
            }
        });

        // Firebase authentication
        mAuth = FirebaseAuth.getInstance();
        userDetails = mAuth.getCurrentUser();

        // Check if the user is logged in
        if (userDetails == null) {
            Intent intent = new Intent(PostListActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return; // Prevent further execution of onCreate
        } else {
            // Check user role
            String userId = userDetails.getUid();
            checkUserRole(userId);
        }

        // Initialize other views and setup methods
        searchView = findViewById(R.id.search_view);
        categorySpinner = findViewById(R.id.category_spinner);
        postRecyclerView = findViewById(R.id.post_recycler_view);

        postRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter(filteredPosts, this);
        postRecyclerView.setAdapter(postAdapter);

        loadCategories();
        setupSearchFilter();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPosts();
    }

    private void checkUserRole(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null && user.getRoleId().equals("-O7sA4aJbpHG6gZeX13p")) { // Role "pt"
                    managePostButton.setVisibility(View.VISIBLE); // Show the "Manage My Posts" button
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PostListActivity", "Error retrieving user role: " + error.getMessage());
            }
        });
    }
    private void loadCategories() {
        Call<List<PostCategory>> call = RetrofitClient.getApiService(this).getCategories();
        call.enqueue(new Callback<List<PostCategory>>() {
            @Override
            public void onResponse(@NonNull Call<List<PostCategory>> call, @NonNull Response<List<PostCategory>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PostCategory> categories = response.body();
                    List<String> categoryNames = new ArrayList<>();
                    categoryNames.add("All Categories"); // Default option
                    categoryIds.add(null); // Default option ID

                    for (PostCategory category : categories) {
                        if (category != null && category.getName() != null) {
                            categoryNames.add(category.getName());
                            categoryIds.add(category.getCategoryId());
                            Log.d("Category", "Category Name: " + category.getName());
                        } else {
                            Log.d("Category", "Null category or category name.");
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(PostListActivity.this, android.R.layout.simple_spinner_item, categoryNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    categorySpinner.setAdapter(adapter);
                } else {
                    Log.d("Category", "Response not successful or body is null");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<PostCategory>> call, @NonNull Throwable t) {
                Toast.makeText(PostListActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
                Log.e("PostListActivity", "Error loading categories: " + t.getMessage());
            }
        });
    }

    private void loadPosts() {
        Call<List<Post>> call = RetrofitClient.getApiService(this).getAllPosts();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(@NonNull Call<List<Post>> call, @NonNull Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allPosts.clear();
                    allPosts.addAll(response.body());
                    applyFilters();
                } else {
                    Toast.makeText(PostListActivity.this, "Failed to load posts", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Post>> call, @NonNull Throwable t) {
                Toast.makeText(PostListActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPostsByCategory(String categoryId) {
        Log.d("loadPostsByCategory", "Category ID: " + categoryId);
        Call<List<Post>> call;

        // Check if "All Categories" is selected
        if (categoryId == null) {
            loadPosts();  // Load all posts if no specific category is selected
            return;
        }

        // API call to get posts by selected category
        call = RetrofitClient.getApiService(this).getPostsByCategory(categoryId);
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(@NonNull Call<List<Post>> call, @NonNull Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("loadPostsByCategory", "Response data: " + response.body().toString()); // Log full response
                    filteredPosts.clear();
                    filteredPosts.addAll(response.body());
                    postAdapter.notifyDataSetChanged();
                } else {
                    Log.d("loadPostsByCategory", "No posts found for category ID: " + categoryId);
                    Toast.makeText(PostListActivity.this, "Failed to load posts for category", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Post>> call, @NonNull Throwable t) {
                Log.e("loadPostsByCategory", "Network failure: " + t.getMessage());
                Toast.makeText(PostListActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void setupSearchFilter() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                applyFilters();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                applyFilters();
                return true;
            }
        });

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selectedCategoryId = categoryIds.get(position); // Get category ID from position
                loadPostsByCategory(selectedCategoryId);  // Load posts by category
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });
    }

    private void applyFilters() {
        String query = searchView.getQuery() != null ? searchView.getQuery().toString().toLowerCase() : "";
        filteredPosts.clear();
        for (Post post : allPosts) {
            boolean matchesQuery = TextUtils.isEmpty(query) ||
                    (post.getTitle() != null && post.getTitle().toLowerCase().contains(query));
            if (matchesQuery) {
                filteredPosts.add(post);
            }
        }
        postAdapter.notifyDataSetChanged();
    }
}
