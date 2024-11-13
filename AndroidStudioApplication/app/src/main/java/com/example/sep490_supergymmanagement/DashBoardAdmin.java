package com.example.sep490_supergymmanagement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashBoardAdmin#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashBoardAdmin extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private int userCount, ptCount;
    private TextView userCountTextView, ptCountTextView ; // TextView to display the user count

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

        // Initialize TextView for displaying user count
        userCountTextView = view.findViewById(R.id.userCountTextView);
        ptCountTextView = view.findViewById(R.id.ptCountTextView);

        // Call method to get total user count from Firebase
        getTotalUserCountWithRole("customer","pt");

        return view;
    }

    private void getTotalUserCountWithRole(String targetRoleName, String targetRoleName2) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 userCount = 0;

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
                                }else if (targetRoleName2.equals(roleName)) {
                                    ptCount++;
                                }
                                // Update the count in the TextView after iterating all roles
                                userCountTextView.setText(String.valueOf(userCount).trim());



                                // Update the count in the TextView after iterating all roles
                                ptCountTextView.setText(String.valueOf(ptCount).trim());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle potential errors here for roles retrieval
                                userCountTextView.setText("Error: " + databaseError.getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors here for users retrieval
                userCountTextView.setText("Error: " + databaseError.getMessage());
            }
        });
    }

}
