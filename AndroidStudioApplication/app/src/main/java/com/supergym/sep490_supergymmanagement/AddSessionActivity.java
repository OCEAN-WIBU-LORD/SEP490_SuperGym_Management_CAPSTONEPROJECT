package com.supergym.sep490_supergymmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.supergym.sep490_supergymmanagement.ExerciseDetailActivity;
import com.supergym.sep490_supergymmanagement.SelectExerciseActivity;
import com.supergym.sep490_supergymmanagement.SessionDataHolder;
import com.supergym.sep490_supergymmanagement.adapters.SelectedExerciseAdapter;
import com.supergym.sep490_supergymmanagement.models.Exercise;
import com.supergym.sep490_supergymmanagement.models.Session;
import com.supergym.sep490_supergymmanagement.models.Set;
import com.supergym.sep490_supergymmanagement.object.MuscleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddSessionActivity extends AppCompatActivity {

    private EditText sessionNameInput;
    private Button selectMuscleGroupsButton, addExerciseButton, startTimerButton, stopTimerButton, saveSessionButton;
    private TextView timerTextView;
    private RecyclerView exerciseRecyclerView;
    private SelectedExerciseAdapter selectedExerciseAdapter;
    private List<Exercise> selectedExercises;
    private String selectedMuscleGroups = "";
    private String userId;  // Thêm userId để lưu session cho đúng người dùng

    // Timer variables
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;
    private long startTime = 0L, elapsedTime = 0L, pauseTime = 0L;
    private boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_session);

        initializeFirebaseUser();
        initializeUIComponents();
        initializeRecyclerView();
        setButtonListeners();
    }

    /**
     * Khởi tạo người dùng hiện tại từ Firebase Auth.
     */
    private void initializeFirebaseUser() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();  // Lấy userId của người dùng hiện tại
        } else {
            Toast.makeText(this, "User not found. Please log in again.", Toast.LENGTH_LONG).show();
            finish();  // Đóng activity nếu không có người dùng
        }
    }

    /**
     * Khởi tạo các thành phần giao diện người dùng.
     */
    private void initializeUIComponents() {
        sessionNameInput = findViewById(R.id.session_name_input);
        selectMuscleGroupsButton = findViewById(R.id.select_muscle_groups_button);
        addExerciseButton = findViewById(R.id.add_exercise_button);
        startTimerButton = findViewById(R.id.start_timer_button);
        stopTimerButton = findViewById(R.id.stop_timer_button);
        timerTextView = findViewById(R.id.timer_text_view);
        saveSessionButton = findViewById(R.id.save_session_button);
        exerciseRecyclerView = findViewById(R.id.exercise_recycler_view);
    }

    /**
     * Khởi tạo RecyclerView và Adapter.
     */
    private void initializeRecyclerView() {
        selectedExercises = new ArrayList<>();
        selectedExerciseAdapter = new SelectedExerciseAdapter(selectedExercises, exercise -> {
            Intent intent = new Intent(AddSessionActivity.this, ExerciseDetailActivity.class);
            intent.putExtra("exercise_id", exercise.getId());
            if (SessionDataHolder.exerciseSetsMap.containsKey(exercise.getId())) {
                intent.putExtra("exercise_sets", exercise.getId());
            }
            startActivityForResult(intent, 2);
        });

        exerciseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        exerciseRecyclerView.setAdapter(selectedExerciseAdapter);
    }

    /**
     * Đặt các listener cho các nút bấm.
     */
    private void setButtonListeners() {
        startTimerButton.setOnClickListener(v -> startTimer());
        stopTimerButton.setOnClickListener(v -> pauseTimer());
        saveSessionButton.setOnClickListener(v -> saveSession());
        selectMuscleGroupsButton.setOnClickListener(v -> showMuscleGroupDialog());
        addExerciseButton.setOnClickListener(v -> openSelectExerciseActivity());
    }

    /**
     * Mở Activity để chọn bài tập.
     */
    private void openSelectExerciseActivity() {
        Intent intent = new Intent(AddSessionActivity.this, SelectExerciseActivity.class);
        ArrayList<String> selectedExerciseIds = new ArrayList<>();
        for (Exercise exercise : selectedExercises) {
            selectedExerciseIds.add(exercise.getId());
        }
        intent.putStringArrayListExtra("selectedExerciseIds", selectedExerciseIds);
        startActivityForResult(intent, 1);
    }

    /**
     * Bắt đầu đồng hồ đếm ngược.
     */
    private void startTimer() {
        if (!isRunning) {
            if (startTime == 0L) {
                startTime = System.currentTimeMillis();  // Ghi nhận thời gian bắt đầu
            } else {
                startTime += (System.currentTimeMillis() - pauseTime); // Điều chỉnh thời gian cho thời gian tạm dừng
            }

            // Khởi động runnable của đồng hồ
            timerRunnable = new Runnable() {
                @Override
                public void run() {
                    elapsedTime = System.currentTimeMillis() - startTime;
                    updateTimerUI(elapsedTime);
                    timerHandler.postDelayed(this, 1000); // Cập nhật mỗi giây
                }
            };
            timerHandler.post(timerRunnable);
            isRunning = true;
        }
    }

    /**
     * Tạm dừng đồng hồ.
     */
    private void pauseTimer() {
        if (isRunning) {
            timerHandler.removeCallbacks(timerRunnable);
            pauseTime = System.currentTimeMillis();  // Lưu thời gian tạm dừng
            isRunning = false;
        }
    }

    /**
     * Cập nhật giao diện hiển thị thời gian đã trôi qua.
     *
     * @param elapsedMillis thời gian đã trôi qua tính bằng mili giây
     */
    private void updateTimerUI(long elapsedMillis) {
        int seconds = (int) (elapsedMillis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        timerTextView.setText(String.format("%02d:%02d", minutes, seconds));
    }

    /**
     * Lưu phiên tập luyện vào Firebase.
     */
    private void saveSession() {
        if (isRunning) {
            timerHandler.removeCallbacks(timerRunnable); // Dừng đồng hồ nếu đang chạy
        }

        String sessionName = sessionNameInput.getText().toString().trim();
        if (sessionName.isEmpty()) {
            Toast.makeText(this, "Please enter a workout session name", Toast.LENGTH_SHORT).show();
            return;
        }

        long endTimeMillis = System.currentTimeMillis();  // Thời gian kết thúc là thời gian hiện tại
        long sessionDurationMillis = elapsedTime;        // Tổng thời gian tập luyện

        // Định dạng thời gian bắt đầu và kết thúc để hiển thị và lưu trữ
        String startTimeString = new SimpleDateFormat("HH:mm").format(new Date(startTime));
        String endTimeString = new SimpleDateFormat("HH:mm").format(new Date(endTimeMillis));

        // Lấy ngày hiện tại
        String dateString = new SimpleDateFormat("MMMM").format(new Date());  // Ví dụ: "January"
        String dayString = new SimpleDateFormat("dd").format(new Date());     // Ví dụ: "25"

        // Kiểm tra xem có bài tập nào đã được chọn chưa
        if (selectedExercises.isEmpty()) {
            Toast.makeText(this, "Please add at least one exercise", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo danh sách các bài tập thiếu set
        List<String> exercisesWithoutSets = new ArrayList<>();

        // Trước khi lưu phiên tập, đảm bảo các set được liên kết với bài tập
        for (Exercise exercise : selectedExercises) {
            if (SessionDataHolder.exerciseSetsMap.containsKey(exercise.getId())) {
                // Lấy các set cho bài tập này và liên kết chúng
                List<Set> setsForExercise = SessionDataHolder.exerciseSetsMap.get(exercise.getId());
                if (setsForExercise == null || setsForExercise.isEmpty()) {
                    exercisesWithoutSets.add(exercise.getName());
                } else {
                    exercise.setSets(setsForExercise);
                }
            } else {
                exercisesWithoutSets.add(exercise.getName());
            }
        }

        // Nếu có bài tập thiếu set, thông báo cho người dùng và ngăn lưu
        if (!exercisesWithoutSets.isEmpty()) {
            StringBuilder message = new StringBuilder("The following exercises do not have sets:\n");
            for (String name : exercisesWithoutSets) {
                message.append("- ").append(name).append("\n");
            }
            message.append("Please add sets for these exercises before saving.");
            new AlertDialog.Builder(this)
                    .setTitle("Missing Set")
                    .setMessage(message.toString())
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        // Tạo đối tượng session
        Session session = new Session();
        session.setId(String.valueOf(System.currentTimeMillis()));  // Tạo ID duy nhất
        session.setMonth(dateString);                               // Đặt tháng hiện tại
        session.setDay(dayString);                                  // Đặt ngày hiện tại
        session.setName(sessionName);                               // Đặt tên phiên tập
        session.setMuscleGroups(selectedMuscleGroups);              // Đặt nhóm cơ đã chọn
        session.setExercises(selectedExercises);                    // Đặt các bài tập cùng với set của chúng
        session.setStartTime(startTimeString);                      // Đặt thời gian bắt đầu
        session.setEndTime(endTimeString);                          // Đặt thời gian kết thúc
        session.setUserId(userId);                                  // Gán userId cho session

        // Lưu session vào Firebase dưới nút "sessions"
        if (userId != null) {
            DatabaseReference sessionsRef = FirebaseDatabase.getInstance().getReference("Session");

            // Kiểm tra xem session đã tồn tại chưa để tránh ghi đè
            sessionsRef.child(session.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Toast.makeText(AddSessionActivity.this, "Session with this ID already exists. Please try again.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Lưu dữ liệu session
                        sessionsRef.child(session.getId()).setValue(session)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(AddSessionActivity.this, "Session has been saved successfully!", Toast.LENGTH_SHORT).show();
                                    // Tùy chọn: Xóa SessionDataHolder sau khi lưu
                                    SessionDataHolder.exerciseSetsMap.clear();
                                    finish();  // Đóng activity
                                })
                                .addOnFailureListener(e -> Toast.makeText(AddSessionActivity.this, "Failed to save session:" + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(AddSessionActivity.this, "Error when checking session:" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Unable to get user ID. Please log in.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Hiển thị dialog để chọn nhóm cơ.
     */
    private void showMuscleGroupDialog() {
        final boolean[] selectedGroups = new boolean[MuscleGroup.getAllMuscleGroups().size()];
        final String[] muscleGroups = MuscleGroup.getAllMuscleGroups().toArray(new String[0]);

        String[] currentSelections = selectMuscleGroupsButton.getText().toString().split(", ");
        for (String selection : currentSelections) {
            for (int i = 0; i < muscleGroups.length; i++) {
                if (muscleGroups[i].equals(selection)) {
                    selectedGroups[i] = true;
                    break;
                }
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Muscle Group")
                .setMultiChoiceItems(muscleGroups, selectedGroups, (dialog, which, isChecked) -> {
                    selectedGroups[which] = isChecked;
                })
                .setPositiveButton("OK", (dialog, which) -> {
                    StringBuilder selected = new StringBuilder();
                    for (int i = 0; i < selectedGroups.length; i++) {
                        if (selectedGroups[i]) {
                            if (selected.length() > 0) {
                                selected.append(", ");
                            }
                            selected.append(muscleGroups[i]);
                        }
                    }
                    selectedMuscleGroups = selected.toString();
                    selectMuscleGroupsButton.setText(selectedMuscleGroups);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Kiểm tra kết quả từ SelectExerciseActivity
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            ArrayList<String> selectedExerciseIds = data.getStringArrayListExtra("selectedExerciseIds");
            if (selectedExerciseIds != null) {
                fetchSelectedExercises(selectedExerciseIds);
            }
        }

        // Xử lý kết quả từ ExerciseDetailActivity nếu cần
        // Bạn có thể thêm logic ở đây nếu muốn cập nhật bài tập sau khi chỉnh sửa
    }

    /**
     * Lấy dữ liệu các bài tập đã chọn từ Firebase.
     *
     * @param selectedExerciseIds danh sách ID của các bài tập đã chọn
     */
    private void fetchSelectedExercises(List<String> selectedExerciseIds) {
        DatabaseReference exercisesRef = FirebaseDatabase.getInstance().getReference("exercises");

        exercisesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<Exercise> exercisesToUpdate = new ArrayList<>();
                List<String> invalidExerciseIds = new ArrayList<>();

                for (String id : selectedExerciseIds) {
                    DataSnapshot exerciseSnapshot = dataSnapshot.child(id);
                    if (exerciseSnapshot.exists()) {
                        Exercise exercise = exerciseSnapshot.getValue(Exercise.class);
                        if (exercise != null && exercise.isValid()) { // Giả sử bạn có phương thức isValid() trong Exercise
                            // Kiểm tra trùng lặp
                            if (!isExerciseAlreadySelected(exercise.getId())) {
                                exercisesToUpdate.add(exercise);
                            } else {
                                Toast.makeText(AddSessionActivity.this,  exercise.getName() + " has been added.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            invalidExerciseIds.add(id);
                        }
                    } else {
                        invalidExerciseIds.add(id);
                    }
                }

                if (!invalidExerciseIds.isEmpty()) {
                    Toast.makeText(AddSessionActivity.this, "Some invalid exercises cannot be added.", Toast.LENGTH_SHORT).show();
                }

                if (!exercisesToUpdate.isEmpty()) {
                    selectedExercises.addAll(exercisesToUpdate);
                    selectedExerciseAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddSessionActivity.this, "Error when retrieving exercise data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Kiểm tra xem bài tập đã được thêm vào danh sách hay chưa.
     *
     * @param exerciseId ID của bài tập
     * @return true nếu đã có, ngược lại false
     */
    private boolean isExerciseAlreadySelected(String exerciseId) {
        for (Exercise exercise : selectedExercises) {
            if (exercise.getId().equals(exerciseId)) {
                return true;
            }
        }
        return false;
    }
}
