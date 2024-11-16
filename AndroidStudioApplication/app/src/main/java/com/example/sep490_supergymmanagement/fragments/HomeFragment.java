package com.example.sep490_supergymmanagement.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sep490_supergymmanagement.Activity_Book_Trainer;
import com.example.sep490_supergymmanagement.LoginActivity;
import com.example.sep490_supergymmanagement.PostListActivity; // Import your PostListActivity
import com.example.sep490_supergymmanagement.R;
import com.example.sep490_supergymmanagement.adapters.PostAdapter;
import com.example.sep490_supergymmanagement.apiadapter.RetrofitClient;
import com.example.sep490_supergymmanagement.models.Post;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    MaterialButton buttonLogOut;
    FirebaseAuth mAuth;
    TextView logOut, profileId, doctorName, dateInfo, timeInfo;
    ImageView doctorImage;
    FirebaseUser userDetails;
    RecyclerView recyclerView;
    PostAdapter postAdapter;
    List<Post> data;
    RelativeLayout relativeLayout;
    TextView seeMore; // Declare the seeMore TextView

    AppCompatButton btnBookTrainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home2, container, false);
        mAuth = FirebaseAuth.getInstance();
        btnBookTrainer = rootView.findViewById(R.id.btnBookTrainer);
        // If the user is not logged in, redirect to LoginActivity
        userDetails = mAuth.getCurrentUser();
        if (userDetails == null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }

        // Initialize RecyclerView for posts
        recyclerView = rootView.findViewById(R.id.postlist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        data = new ArrayList<>();
        postAdapter = new PostAdapter(data, getActivity());
        recyclerView.setAdapter(postAdapter);

        // Initialize the seeMore TextView and set click listener
        seeMore = rootView.findViewById(R.id.see_more);
        seeMore.setOnClickListener(view -> {
            userDetails = mAuth.getCurrentUser();
            if (userDetails != null) {
                Intent intent = new Intent(getActivity(), PostListActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        btnBookTrainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Activity_Book_Trainer.class);
                startActivity(intent);
            }
        });
        // Fetch the latest posts and update the RecyclerView
        fetchLatestPosts();

        return rootView;
    }

    private void fetchLatestPosts() {
        RetrofitClient.getApiService().getLatestPosts().enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(@NonNull Call<List<Post>> call, @NonNull Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.clear();                    // Clear old data
                    data.addAll(response.body());    // Add new data from API
                    postAdapter.notifyDataSetChanged(); // Update RecyclerView
                } else {
                    // Check for null context before showing Toast
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Unable to load latest posts", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Post>> call, @NonNull Throwable t) {
                // Check for null context before showing Toast
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
}
