package com.supergym.sep490_supergymmanagement.repositories;

import androidx.annotation.NonNull;
import com.supergym.sep490_supergymmanagement.models.UpdatedUser;
import com.supergym.sep490_supergymmanagement.models.User;
import com.supergym.sep490_supergymmanagement.repositories.callbacks.Callback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserResp {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    // Get user by userId
    public void getUser(String userId, Callback<User> callback) {
        Query query = databaseReference.child("users").child(userId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = new User();
                user.setUserId(userId);
                user.setName(snapshot.hasChild("name") ? snapshot.child("name").getValue().toString() : "");
                user.setEmail(snapshot.hasChild("email") ? snapshot.child("email").getValue().toString() : "");
                user.setPhone(snapshot.hasChild("phone") ? snapshot.child("phone").getValue().toString() : "");
                user.setGender(snapshot.hasChild("gender") ? snapshot.child("gender").getValue().toString() : "");
                user.setAddress(snapshot.hasChild("address") ? snapshot.child("address").getValue().toString() : "");
                user.setRoleId(snapshot.hasChild("roleId") ? snapshot.child("roleId").getValue().toString() : "");
                user.setUserAvatar(snapshot.hasChild("userAvatar") ? snapshot.child("userAvatar").getValue().toString() : "");

                user.setAddress(snapshot.hasChild("idCard") ? snapshot.child("idCard").getValue().toString() : "");


                // Convert dob from timestamp to Date
                if (snapshot.hasChild("dob") && snapshot.child("dob").child("time").exists()) {
                    Long timestamp = snapshot.child("dob").child("time").getValue(Long.class);
                    if (timestamp != null) {
                        user.setDob(new Date(timestamp));
                    }
                }

                List<User> users = new ArrayList<>();
                users.add(user);
                callback.onCallback(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Error: " + error.getMessage());
            }
        });
    }

    public void getUserList(Callback<User> callback) {
        Query query = databaseReference.child("users");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> users = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    User user = new User();
                    user.setUserId(childSnapshot.getKey() != null ? childSnapshot.getKey() : "");
                    user.setName(childSnapshot.hasChild("name") && childSnapshot.child("name").getValue() != null ? childSnapshot.child("name").getValue().toString() : "");
                    user.setEmail(childSnapshot.hasChild("email") && childSnapshot.child("email").getValue() != null ? childSnapshot.child("email").getValue().toString() : "");
                    user.setPhone(childSnapshot.hasChild("phone") && childSnapshot.child("phone").getValue() != null ? childSnapshot.child("phone").getValue().toString() : "");
                    user.setAddress(childSnapshot.hasChild("address") && childSnapshot.child("address").getValue() != null ? childSnapshot.child("address").getValue().toString() : "");
                    user.setRoleId(childSnapshot.hasChild("roleId") && childSnapshot.child("roleId").getValue() != null ? childSnapshot.child("role").getValue().toString() : "");

                    user.setEmail(childSnapshot.hasChild("idCard") && childSnapshot.child("idCard").getValue() != null ? childSnapshot.child("email").getValue().toString() : "");

                    // Convert dob from timestamp to Date
                    if (childSnapshot.hasChild("dob") && childSnapshot.child("dob").child("time").exists()) {
                        Long timestamp = childSnapshot.child("dob").child("time").getValue(Long.class);
                        if (timestamp != null) {
                            user.setDob(new Date(timestamp));
                        }
                    }

                    users.add(user);
                }
                callback.onCallback(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Error: " + error.getMessage());
            }
        });
    }

    public void createUser(User user, Callback<User> callback) {
        databaseReference.child("users").child(user.getUserId()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    List<User> users = new ArrayList<>();
                    users.add(user);
                    callback.onCallback(users);
                } else {
                    System.out.println("Error: " + task.getException());
                }
            }
        });
    }

    public void updateUser(User user, Callback<User> callback) {
        // Create IdCard and HealthCard instances

        // Create UpdatedUser object
        UpdatedUser updatedUser = new UpdatedUser(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getGender(),
                user.getDob(),
                user.getAddress(),
                user.getPhone(),
                user.getIdCard(),
                user.getRoleId(),
                user.getUserAvatar()
        );

        databaseReference.child("users").child(user.getUserId()).setValue(updatedUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    List<User> users = new ArrayList<>();
                    users.add(user);
                    callback.onCallback(users);
                } else {
                    System.out.println("Error: " + task.getException());
                }
            }
        });
    }
}
