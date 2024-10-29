package com.example.sep490_supergymmanagement;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sep490_supergymmanagement.adapters.PostAdapter;
import com.example.sep490_supergymmanagement.apiadapter.RetrofitClient;
import com.example.sep490_supergymmanagement.models.Post;
import com.example.sep490_supergymmanagement.models.PostCategory;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);

        searchView = findViewById(R.id.search_view);
        categorySpinner = findViewById(R.id.category_spinner);
        postRecyclerView = findViewById(R.id.post_recycler_view);

        postRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter(filteredPosts, this);
        postRecyclerView.setAdapter(postAdapter);

        loadCategories();
        loadPosts();

        setupSearchFilter();
    }

    private void loadCategories() {
        Call<List<PostCategory>> call = RetrofitClient.getApiService().getCategories();
        call.enqueue(new Callback<List<PostCategory>>() {
            @Override
            public void onResponse(@NonNull Call<List<PostCategory>> call, @NonNull Response<List<PostCategory>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PostCategory> categories = response.body();
                    List<String> categoryNames = new ArrayList<>();
                    categoryNames.add("All Categories"); // Default option
                    for (PostCategory category : categories) {
                        categoryNames.add(category.getCategoryName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(PostListActivity.this, android.R.layout.simple_spinner_item, categoryNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    categorySpinner.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<PostCategory>> call, @NonNull Throwable t) {
                Toast.makeText(PostListActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPosts() {
        Call<List<Post>> call = RetrofitClient.getApiService().getAllPosts();
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
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing or apply a default filter if necessary
            }
        });

    }

    private void applyFilters() {
        String query = searchView.getQuery().toString().toLowerCase();
        String selectedCategory = categorySpinner.getSelectedItem().toString();

        filteredPosts.clear();
        for (Post post : allPosts) {
            boolean matchesCategory = selectedCategory.equals("All Categories") || post.getCategoryId().equals(selectedCategory);
            boolean matchesQuery = TextUtils.isEmpty(query) || post.getTitle().toLowerCase().contains(query);

            if (matchesCategory && matchesQuery) {
                filteredPosts.add(post);
            }
        }
        postAdapter.notifyDataSetChanged();
    }
}
