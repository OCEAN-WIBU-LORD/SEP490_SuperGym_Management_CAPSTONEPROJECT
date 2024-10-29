package com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.repositories;

import androidx.annotation.NonNull;

import com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.models.UpdatedUser;
import com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.models.User;
import com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.repositories.callbacks.Callback;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
                user.setPhone(snapshot.hasChild("gender") ? snapshot.child("gender").getValue().toString() : "");
                user.setAddress(snapshot.hasChild("address") ? snapshot.child("address").getValue().toString() : "");
                user.setRole(snapshot.hasChild("role") ? snapshot.child("role").getValue().toString() : "");
                user.setUserAvatar(snapshot.hasChild("userAvatar") ? snapshot.child("userAvatar").getValue().toString() : "");
                String healthCard = snapshot.hasChild("healthCard") && snapshot.child("healthCard").getValue() != null ? snapshot.child("healthCard").getValue().toString() : "null";
                if (!"null".equals(healthCard)) {
                    try {
                        user.setHealthCard(new JSONObject(healthCard));
                    } catch (JSONException e) {
                        user.setHealthCard(new JSONObject()); // Set to empty JSON object if parsing fails
                    }
                } else {
                    user.setHealthCard(new JSONObject());
                }

                String idCard = snapshot.hasChild("idCard") && snapshot.child("idCard").getValue() != null ? snapshot.child("idCard").getValue().toString() : "null";
                if (!"null".equals(idCard)) {
                    try {
                        user.setIdCard(new JSONObject(idCard));
                    } catch (JSONException e) {
                        user.setIdCard(new JSONObject()); // Set to empty JSON object if parsing fails
                    }
                } else {
                    user.setIdCard(new JSONObject());
                }

                // set dob as timestamp from snapshot.child("dob").child("time")
                if (snapshot.hasChild("dob")) {
                    Long timestamp = Objects.requireNonNull(snapshot.child("dob").child("time").getValue(Long.class));
                    // convert to timestamp
                    user.setDob(new java.sql.Timestamp(timestamp));
                }

//                String healthCard = Objects.requireNonNull(snapshot.child("healthCard").getValue()).toString();
//                if (!healthCard.equals("null")) {
//                    try {
//                        user.setHealthCard(new JSONObject(healthCard));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                String idCard = Objects.requireNonNull(snapshot.child("idCard").getValue()).toString();
//                if (!idCard.equals("null")) {
//                    try {
//                        user.setIdCard(new JSONObject(idCard));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
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
                    user.setRole(childSnapshot.hasChild("role") && childSnapshot.child("role").getValue() != null ? childSnapshot.child("role").getValue().toString() : "");

                    String healthCard = childSnapshot.hasChild("healthCard") && childSnapshot.child("healthCard").getValue() != null ? childSnapshot.child("healthCard").getValue().toString() : "null";
                    if (!healthCard.equals("null")) {
                        try {
                            user.setHealthCard(new JSONObject(healthCard));
                        } catch (JSONException e) {
                            user.setHealthCard(new JSONObject()); // Set to empty JSON object if parsing fails or "null"
                        }
                    } else {
                        user.setHealthCard(new JSONObject());
                    }

                    String idCard = childSnapshot.hasChild("idCard") && childSnapshot.child("idCard").getValue() != null ? childSnapshot.child("idCard").getValue().toString() : "null";
                    if (!idCard.equals("null")) {
                        try {
                            user.setIdCard(new JSONObject(idCard));
                        } catch (JSONException e) {
                            user.setIdCard(new JSONObject()); // Set to empty JSON object if parsing fails or "null"
                        }
                    } else {
                        user.setIdCard(new JSONObject());
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
        // convert user to the same user with no JSONObjects
        Map<String, Object> map = new HashMap<>(); // empty map
        Map<String, Object> idCard = new HashMap<>();
        // store user.getIdCard().optString("id") in idCard with key id
        idCard.put("id", user.getIdCard().optString("id"));
        UpdatedUser updatedUser = new UpdatedUser(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getGender(),
                user.getDob(),
                user.getAddress(),
                user.getPhone(),
                idCard,
                map,
                user.getRole(),
                user.getUserAvatar());
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
