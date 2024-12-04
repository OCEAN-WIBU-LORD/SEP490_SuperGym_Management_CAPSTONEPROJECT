package com.supergym.sep490_supergymmanagement;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.supergym.sep490_supergymmanagement.apiadapter.ApiService.ApiService;
import com.supergym.sep490_supergymmanagement.apiadapter.RetrofitClient;
import com.supergym.sep490_supergymmanagement.models.MembershipCountResponse;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashBoardAdmin extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private int userCount = 0, ptCount = 0;
    private TextView userCountTextView, ptCountTextView, totalMembership;

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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dash_board_admin, container, false);

        // Initialize TextViews for displaying counts
        userCountTextView = view.findViewById(R.id.userCountTextView);
        ptCountTextView = view.findViewById(R.id.ptCountTextView);
        totalMembership = view.findViewById(R.id.totalMembership);

        // Call method to get total user count from Firebase
        getTotalUserCountWithRole("customer", "pt");

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Fetch membership counts from the API after the view is created
        fetchMembershipCounts();
    }

    private void getTotalUserCountWithRole(String targetRoleName, String targetRoleName2) {
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

    private void fetchMembershipCounts() {
        // Create Retrofit instance
        ApiService apiService = RetrofitClient.getApiService(getContext());

        // Make the API call
        Call<MembershipCountResponse> call = apiService.getMembershipCounts();
        call.enqueue(new Callback<MembershipCountResponse>() {
            @Override
            public void onResponse(Call<MembershipCountResponse> call, Response<MembershipCountResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        // Handle successful response
                        MembershipCountResponse membershipCount = response.body();
                        int gymCount = membershipCount.getTotalGymMemberships();
                        int boxingCount = membershipCount.getTotalBoxingMemberships();
                        int total = membershipCount.getTotalMemberships();

                        // Update UI
                        totalMembership.setText("Total Memberships: " + total);
                    } else {
                        // Response body is null, log error
                        Log.e("API Error", "Response body is null");
                        Toast.makeText(getContext(), "Failed to load membership counts", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Log the error code and body if the response is not successful
                    Log.e("API Error", "Response Code: " + response.code());
                    try {
                        Log.e("API Error", "Response Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getContext(), "Failed to load membership counts", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MembershipCountResponse> call, Throwable t) {
                // Log the failure reason
                Log.e("API Error", "Error: " + t.getMessage());
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
