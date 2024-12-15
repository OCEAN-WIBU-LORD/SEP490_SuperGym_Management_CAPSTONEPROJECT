package com.supergym.sep490_supergymmanagement;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.supergym.sep490_supergymmanagement.FirebaseImageLoader.FirebaseHelper;
import com.supergym.sep490_supergymmanagement.apiadapter.ApiService.ApiService;
import com.supergym.sep490_supergymmanagement.apiadapter.RetrofitClient;
import com.supergym.sep490_supergymmanagement.models.MembershipCountResponse;
import com.supergym.sep490_supergymmanagement.models.RegistrationGrowthResponse;

import java.io.IOException;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashBoardAdmin extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private int userCount = 0, ptCount = 0;
    private TextView userCountTextView, ptCountTextView, totalMembership, nearBirthDate, tvExpiredUsersCount, paidCourseThisMonth;


    private  ProgressBar progressBar;
    public DashBoardAdmin() {
        // Required empty public constructor
    }

    public static DashBoardAdmin newInstance(String param1, String param2) {
        DashBoardAdmin fragment = new DashBoardAdmin();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dash_board_admin, container, false);

        progressBar = view.findViewById(R.id.progressBarOverlay);

        // Show progress bar before data loads
        progressBar.setVisibility(View.VISIBLE);
        // Initialize TextViews for displaying counts
        userCountTextView = view.findViewById(R.id.userCountTextView);
        ptCountTextView = view.findViewById(R.id.ptCountTextView);
        totalMembership = view.findViewById(R.id.totalMembership);
        nearBirthDate = view.findViewById(R.id.nearBirthDate);
        paidCourseThisMonth = view.findViewById(R.id.paidCourseThisMonth);
        tvExpiredUsersCount = view.findViewById(R.id.tvExpiredUsersCount);

        // Initialize the ProgressBar


        // Fetch data and hide progress bar after loading
        fetchData(progressBar);

        return view;
    }

    private void fetchData(ProgressBar progressBar) {
        // Initialize data fetching processes
        getTotalUserCountWithRole("customer", "pt");
        fetchRegistrationGrowthData();
        countUsersWithDOBInCurrentOrNextMonth();

        // Simulate FirebaseHelper counting expired users (you can chain your methods here)
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.countExpiredUsers(new FirebaseHelper.CountExpiredUsersCallback() {
            @Override
            public void onCountExpiredUsers(int count) {
                tvExpiredUsersCount.setText(String.valueOf(count));

                // Once all data is loaded, hide the progress bar
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Count users with birthdays in the current or next month
        countUsersWithDOBInCurrentOrNextMonth();
    }

    public void countUsersWithDOBInCurrentOrNextMonth() {
        // Get the current month and the next month
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // Months are 0-based
        int nextMonth = (currentMonth % 12) + 1;

        // Get reference to the Firebase Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Add a listener to count users
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot dobSnapshot = userSnapshot.child("dob");
                    if (dobSnapshot.exists()) {
                        int month = dobSnapshot.child("month").getValue(Integer.class);
                        if (month == currentMonth || month == nextMonth) {
                            count++;
                        }
                    }
                }
                nearBirthDate.setText(String.valueOf(count));
                // Update UI with the count of users with birthdays in the current or next month
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                Toast.makeText(getContext(), "Error fetching user data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getTotalUserCountWithRole(String targetRoleName, String targetRoleName2) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userCount = 0; // Reset count
                ptCount = 0;   // Reset PT count

                // Loop through each user and check their role
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String roleId = userSnapshot.child("roleId").getValue(String.class);

                    if (roleId != null) {
                        // Reference the "Roles" node to check the RoleName
                        DatabaseReference rolesReference = FirebaseDatabase.getInstance().getReference("Roles").child(roleId);
                        rolesReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot roleSnapshot) {
                                String roleName = roleSnapshot.child("RoleName").getValue(String.class);
                                if (targetRoleName.equals(roleName)) {
                                    userCount++;
                                } else if (targetRoleName2.equals(roleName)) {
                                    ptCount++;
                                }

                                // Update the count in the TextViews after iterating all roles
                                userCountTextView.setText(String.valueOf(userCount));
                                ptCountTextView.setText(String.valueOf(ptCount));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle potential errors here for roles retrieval
                                Toast.makeText(getContext(), "Error fetching roles: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors here for users retrieval
                Toast.makeText(getContext(), "Error fetching users: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchRegistrationGrowthData() {
        // Create Retrofit instance
        ApiService apiService = RetrofitClient.getApiService(getContext());

        // Make the API call to get registration growth data
        Call<RegistrationGrowthResponse> call = apiService.getRegistrationGrowth();
        call.enqueue(new Callback<RegistrationGrowthResponse>() {
            @Override
            public void onResponse(Call<RegistrationGrowthResponse> call, Response<RegistrationGrowthResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        // Handle successful response
                        RegistrationGrowthResponse registrationGrowthResponse = response.body();
                        int totalRegistrations = registrationGrowthResponse.getTotalRegistrations();
                        paidCourseThisMonth.setText(String.valueOf(registrationGrowthResponse.getRegistrationsThisMonth())) ;
                      //  growthPercentage.setText(String.valueOf(registrationGrowthResponse.getGrowthPercentage()));
                        // Update the totalMembership TextView with the total registrations count
                        totalMembership.setText(String.valueOf(totalRegistrations));
                    } else {
                        // Response body is null, log error
                        Log.e("API Error", "Response body is null");
                        Toast.makeText(getContext(), "Failed to load registration data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Log the error code and body if the response is not successful
                    Log.e("API Error", "Response Code: " + response.code());
                    try {
                        Log.e("API Error", "Response Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getContext(), "Failed to load registration data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegistrationGrowthResponse> call, Throwable t) {
                // Log the failure reason
                Log.e("API Error", "Error: " + t.getMessage());
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
