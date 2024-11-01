package com.example.sep490_supergymmanagement;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import java.util.HashMap;
import java.util.List;

public class RegisterFaceActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private DatabaseReference databaseReference;
    private TextView tvResult;
    private String userId = "user_id"; // Replace with actual user ID or retrieve from FirebaseAuth

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_face);

        Button btnCapture = findViewById(R.id.btn_capture);
        Button btnCheckFace = findViewById(R.id.btn_check_face);
        Button checkRealTime = findViewById(R.id.checkRealTime);


        checkRealTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterFaceActivity.this, FaceCaptureActivity.class);
                startActivity(intent);
            }
        });
        tvResult = findViewById(R.id.tv_result);

        databaseReference = FirebaseDatabase.getInstance().getReference("faceData");

        btnCapture.setOnClickListener(v -> captureFace());

        btnCheckFace.setOnClickListener(v -> checkFace(userId));
    }

    private void captureFace() {
        // Launch the camera activity to capture face
        Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            detectFace(photo);
        }
    }

    private void detectFace(Bitmap bitmap) {
        // Configure ML Kit Face Detector options
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .build();

        FaceDetector detector = FaceDetection.getClient(options);
        InputImage image = InputImage.fromBitmap(bitmap, 0);

        // Detect face in the image
        detector.process(image)
                .addOnSuccessListener(faces -> {
                    if (!faces.isEmpty()) {
                        // If a face is detected, store the face ID (for demo purposes using detected face bounds)
                        String faceData = faces.get(0).getBoundingBox().flattenToString();
                        registerFace(userId, faceData);
                    } else {
                        tvResult.setText("No face detected.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FaceDetection", "Face detection failed", e);
                    tvResult.setText("Face detection failed.");
                });
    }

    private void registerFace(String userId, String faceData) {
        HashMap<String, String> faceMap = new HashMap<>();
        faceMap.put("faceData", faceData);

        databaseReference.child(userId).setValue(faceMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        tvResult.setText("Face ID Registered Successfully!");
                    } else {
                        tvResult.setText("Registration Failed.");
                    }
                });
    }

    private void checkFace(String userId) {
        databaseReference.child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String storedFaceData = task.getResult().child("faceData").getValue(String.class);
                if (storedFaceData != null) {
                    tvResult.setText("Face ID found in the database.");
                } else {
                    tvResult.setText("Face ID not found.");
                }
            } else {
                tvResult.setText("Error checking face ID.");
            }
        });
    }
}
