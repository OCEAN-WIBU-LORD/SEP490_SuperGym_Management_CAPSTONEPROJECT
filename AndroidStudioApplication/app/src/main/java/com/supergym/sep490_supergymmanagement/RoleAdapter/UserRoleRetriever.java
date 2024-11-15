package com.supergym.sep490_supergymmanagement.RoleAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.util.Log;

public class UserRoleRetriever {

    private static final String TAG = "UserRoleRetriever";
    private static String roleId, roleNameTxt, finalRoleTxt = null;

    // Function to retrieve the role name for a specific user
    public String  getUserRole(final String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        // Retrieve the user data to get the roleId
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot userSnapshot) {
                if (userSnapshot.exists()) {
                     roleId = userSnapshot.child("roleId").getValue(String.class);

                    if (roleId != null) {
                        // Use roleId to retrieve the role name
                        finalRoleTxt = getRoleName(roleId);
                    } else {
                        Log.e(TAG, "Role ID not found for user: " + userId);
                        finalRoleTxt= null;
                    }
                } else {
                    Log.e(TAG, "User not found with ID: " + userId);
                    finalRoleTxt = null;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error retrieving user data: " + databaseError.getMessage());
            }
        });
        return finalRoleTxt;
    }

    // Function to retrieve the RoleName using the roleId
    private String  getRoleName(String roleId) {
        DatabaseReference roleRef = FirebaseDatabase.getInstance().getReference("Roles").child(roleId);

        roleRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot roleSnapshot) {
                if (roleSnapshot.exists()) {
                    String roleName = roleSnapshot.child("RoleName").getValue(String.class);

                    if (roleName != null) {
                        roleNameTxt = roleName;
                        // You can update UI or handle the role name as needed here
                    } else {
                        roleNameTxt = null;
                    }
                } else {
                    Log.e(TAG, "Error retrieving role data: ");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error retrieving role data: " + databaseError.getMessage());
            }
        });
        return roleNameTxt;
    }
}
