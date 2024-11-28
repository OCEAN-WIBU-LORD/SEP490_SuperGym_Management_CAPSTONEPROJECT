package com.supergym.sep490_supergymmanagement.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.supergym.sep490_supergymmanagement.Activity_Book_Trainer;
import com.supergym.sep490_supergymmanagement.LoginActivity;
import com.supergym.sep490_supergymmanagement.MyApp;
import com.supergym.sep490_supergymmanagement.PostListActivity; // Import your PostListActivity
import com.supergym.sep490_supergymmanagement.R;
import com.supergym.sep490_supergymmanagement.ViewMainContent;
import com.supergym.sep490_supergymmanagement.ViewTrainerDetails;
import com.supergym.sep490_supergymmanagement.adapters.PostAdapter;
import com.supergym.sep490_supergymmanagement.apiadapter.ApiService.ApiService;
import com.supergym.sep490_supergymmanagement.apiadapter.RetrofitClient;
import com.supergym.sep490_supergymmanagement.models.Post;
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
    private String roleCheck;
    private CardView bookTrainer;
    private boolean isRegistered;

    private String userId42;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home2, container, false);
        isRegistered = false;
        mAuth = FirebaseAuth.getInstance();
        btnBookTrainer = rootView.findViewById(R.id.btnBookTrainer);
        // If the user is not logged in, redirect to LoginActivity
        userDetails = mAuth.getCurrentUser();
        if (userDetails == null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
        bookTrainer = rootView.findViewById(R.id.bookTrainer);

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

        btnBookTrainer.setOnClickListener(v -> {
            Log.d("HomeFragment", "Button clicked");
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            }else{
                userId42 = user.getUid();
                checkRegistration(userId42);
            }
            MyApp app = (MyApp) requireActivity().getApplicationContext();
            if (app != null) {
                String userRole = app.getUserRole();
                Log.d("HomeFragment", "User role: " + userRole);

                if (userRole == null || userRole.isEmpty()) {
                    Toast.makeText(requireContext(), "User role not found!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if ("pt".equals(userRole)) {
                    Toast.makeText(requireContext(), "You are a Trainer, booking is not allowed!", Toast.LENGTH_SHORT).show();
                    return;
                }else if ("customer".equals(userRole)) {
                    Toast.makeText(requireContext(), "You are a Customer, Okey", Toast.LENGTH_SHORT).show();
                    checkRegistration(userId42);
                    return;
                }
            }

            if (isRegistered) {
                Log.d("HomeFragment", "User is registered");
                Intent intent = new Intent(requireContext(), Activity_Book_Trainer.class);
                startActivity(intent);
            } else {
                Log.d("HomeFragment", "User is not registered");
                Toast.makeText(requireContext(), "You are not a Member. Please register for a package.", Toast.LENGTH_SHORT).show();
            }
        });

        // Fetch the latest posts and update the RecyclerView
        fetchLatestPosts();

        return rootView;
    }

    private void checkRegistration(String registrationId) {
        ApiService api = RetrofitClient.getInstance().create(ApiService.class);

        // Disable the button until the response is fetched
        btnBookTrainer.setEnabled(false);

        api.checkRegistration(registrationId).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                btnBookTrainer.setEnabled(true); // Re-enable the button
                if (response.isSuccessful() && response.body() != null) {
                    isRegistered = response.body();
                    if (isRegistered) {
                        Log.d("HomeFragment", "User is registered");
                        Intent intent = new Intent(requireContext(), Activity_Book_Trainer.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(requireContext(), "You are not a Member. Please register for a package.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to fetch registration status.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                btnBookTrainer.setEnabled(true); // Enable button even on failure
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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
