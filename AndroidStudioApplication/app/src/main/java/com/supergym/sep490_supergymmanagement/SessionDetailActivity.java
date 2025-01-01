package com.supergym.sep490_supergymmanagement;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.supergym.sep490_supergymmanagement.adapters.ExerciseAdapter;
import com.supergym.sep490_supergymmanagement.models.Exercise;
import com.supergym.sep490_supergymmanagement.models.Session;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SessionDetailActivity extends AppCompatActivity {

    private TextView sessionName, sessionDate, sessionTime, sessionMuscleGroups;
    private RecyclerView exerciseRecyclerView;
    private ExerciseAdapter exerciseAdapter;
    private List<Exercise> exerciseList;
    private String sessionId;
    private String userId;  // Thêm userId để xác định user
    private CardView returnBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_detail);

        // Nhận sessionId từ Intent
        sessionId = getIntent().getStringExtra("SESSION_ID");

        // Lấy userId từ Firebase Auth
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();  // Lấy userId của người dùng hiện tại
        }

        // Khởi tạo các thành phần giao diện
        sessionName = findViewById(R.id.session_name);
        sessionDate = findViewById(R.id.session_date);
        sessionTime = findViewById(R.id.session_time);
        sessionMuscleGroups = findViewById(R.id.session_muscle_groups);
        exerciseRecyclerView = findViewById(R.id.exercise_recycler_view);
        returnBtn = findViewById(R.id.returnBtn);
        returnBtn.setOnClickListener(v -> onBackPressed());
        exerciseList = new ArrayList<>();

        // Khởi tạo ExerciseAdapter với danh sách bài tập rỗng
        exerciseAdapter = new ExerciseAdapter(exerciseList);
        exerciseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        exerciseRecyclerView.setAdapter(exerciseAdapter);

        // Lấy thông tin buổi tập từ Firebase
        loadSessionDetails();
    }

    private void loadSessionDetails() {
        if (userId != null && sessionId != null) {
            // Truy vấn session từ node "Session" và lọc theo sessionId và userId
            DatabaseReference sessionsRef = FirebaseDatabase.getInstance()
                    .getReference("Session");

            sessionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot sessionSnapshot : dataSnapshot.getChildren()) {
                        Session session = sessionSnapshot.getValue(Session.class);

                        // Kiểm tra nếu session tồn tại và có userId cùng sessionId khớp
                        if (session != null && userId.equals(session.getUserId()) && sessionId.equals(sessionSnapshot.getKey())) {
                            sessionName.setText(session.getName());
                            // Hiển thị ngày tháng và thời gian
                            sessionDate.setText(session.getDay() + " " + session.getMonth() + " " + "2023"); // Thay "2023" bằng năm thực tế nếu cần
                            sessionTime.setText(session.getStartTime() + " - " + session.getEndTime());
                            sessionMuscleGroups.setText(session.getMuscleGroups());

                            // Lấy danh sách bài tập và cập nhật vào RecyclerView
                            if (session.getExercises() != null) {
                                exerciseList.clear();
                                exerciseList.addAll(session.getExercises());
                                exerciseAdapter.notifyDataSetChanged();
                            }
                            break;  // Thoát vòng lặp sau khi tìm thấy session phù hợp
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Xử lý khi có lỗi
                }
            });
        } else {
            // Xử lý khi userId hoặc sessionId bị null
            // Ví dụ: thông báo lỗi hoặc quay về màn hình trước
        }
    }

}
