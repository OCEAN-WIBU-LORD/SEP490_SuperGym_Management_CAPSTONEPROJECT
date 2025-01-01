package com.supergym.sep490_supergymmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.supergym.sep490_supergymmanagement.adapters.SessionAdapter;
import com.supergym.sep490_supergymmanagement.models.Session;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.supergym.sep490_supergymmanagement.models.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private SessionAdapter sessionAdapter;
    private List<Session> sessionList;
    private DatabaseReference sessionsRef;
    private CardView returnBtn;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseUser userDetails;
    private RecyclerView sessionRecyclerView;
    private String userId;  // Lấy userId từ Firebase Auth
    private Button addExercise;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Lấy userId từ Firebase Auth
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();  // Lấy userId của người dùng hiện tại
        }

        // Khởi tạo RecyclerView và Adapter
        sessionRecyclerView = findViewById(R.id.session_recycler_view);
        sessionList = new ArrayList<>();
        sessionAdapter = new SessionAdapter(this);

        // Thiết lập LayoutManager cho RecyclerView
        sessionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sessionRecyclerView.setAdapter(sessionAdapter);

        // Truy cập đến node "users/{userId}/sessions" trong Firebase Realtime Database
        if (userId != null) {
            sessionsRef = FirebaseDatabase.getInstance().getReference("users")
                    .child(userId)
                    .child("sessions");  // Thay đổi thành truy cập node "sessions" của userId
        }

        // Lấy dữ liệu từ Firebase
        fetchSessionsFromFirebase();

        // Nút FloatingActionButton để thêm buổi tập mới
        FloatingActionButton addSessionFab = findViewById(R.id.add_session_fab);
        addSessionFab.setOnClickListener(view -> {
            // Chuyển đến màn hình thêm buổi tập (Session)
            Intent intent = new Intent(HomeActivity.this, AddSessionActivity.class);
            startActivity(intent);
        });
        returnBtn = findViewById(R.id.returnBtn);
        returnBtn.setOnClickListener(v -> onBackPressed());
        // Nút thêm bài tập mới
        addExercise = findViewById(R.id.add_exercise_button);
        addExercise.setVisibility(View.GONE);
        addExercise.setOnClickListener(view -> {
            // Chuyển đến màn hình thêm Exercise
            Intent intent = new Intent(HomeActivity.this, AddExerciseActivity.class);
            startActivity(intent);
        });
        mAuth = FirebaseAuth.getInstance();
        userDetails = mAuth.getCurrentUser();

        // Check if the user is logged in
        if (userDetails == null) {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return; // Prevent further execution of onCreate
        } else {
            // Check user role
            String userId = userDetails.getUid();
            checkUserRole(userId);
        }
    }
    private void checkUserRole(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null && user.getRoleId().equals("-O7sA4aJbpHG6gZeX13p")) { // Role "pt"
                    addExercise.setVisibility(View.VISIBLE); // Show the "Manage My Posts" button
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PostListActivity", "Error retrieving user role: " + error.getMessage());
            }
        });
    }
    // Lấy danh sách các buổi tập từ Firebase và cập nhật RecyclerView
    private void fetchSessionsFromFirebase() {
        DatabaseReference sessionsRef = FirebaseDatabase.getInstance().getReference("Session");  // Truy cập trực tiếp vào node "sessions"

        if (sessionsRef != null) {
            sessionsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Xóa danh sách cũ
                    sessionList.clear();

                    // Duyệt qua tất cả các session trong Firebase
                    for (DataSnapshot sessionSnapshot : dataSnapshot.getChildren()) {
                        // Lấy dữ liệu của từng session
                        Session session = sessionSnapshot.getValue(Session.class);

                        // Thêm session vào danh sách nếu session có userId khớp với user hiện tại
                        if (session != null && userId != null && userId.equals(session.getUserId())) {
                            sessionList.add(session);
                        }
                    }

                    // Sắp xếp danh sách session theo ngày mới nhất đến cũ nhất
                    Collections.sort(sessionList, new Comparator<Session>() {
                        @Override
                        public int compare(Session s1, Session s2) {
                            String date1 = s1.getMonth() + s1.getDay();
                            String date2 = s2.getMonth() + s2.getDay();
                            return date2.compareTo(date1); // Sắp xếp ngày giảm dần
                        }
                    });

                    // Cập nhật danh sách vào Adapter
                    sessionAdapter.setSessions(sessionList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Xử lý khi có lỗi truy vấn Firebase
                    Toast.makeText(HomeActivity.this, "Failed to load sessions: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
