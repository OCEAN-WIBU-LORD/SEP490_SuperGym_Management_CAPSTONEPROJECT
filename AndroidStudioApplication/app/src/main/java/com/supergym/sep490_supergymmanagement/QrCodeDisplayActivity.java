package com.supergym.sep490_supergymmanagement;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.supergym.sep490_supergymmanagement.fragments.HomeFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class QrCodeDisplayActivity extends AppCompatActivity {

    private ImageView qrImageView;
    private TextView timerTextView;
    private Button backToHomeButton;
    private Button saveQrButton;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 5 * 60 * 1000; // 5 minutes in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_display);

        qrImageView = findViewById(R.id.qrCodeImageView);
        timerTextView = findViewById(R.id.timerTextView);
        backToHomeButton = findViewById(R.id.backToHomeButton);
        saveQrButton = findViewById(R.id.saveQrButton);

        // Nhận chuỗi base64 từ Intent
        Intent intent = getIntent();
        String qrBase64 = intent.getStringExtra("qr_base64");

        // Kiểm tra dữ liệu QR Base64
        if (qrBase64 != null && !qrBase64.isEmpty()) {
            Bitmap qrBitmap = decodeBase64(qrBase64);
            if (qrBitmap != null) {
                qrImageView.setImageBitmap(qrBitmap); // Hiển thị ảnh QR
                fadeInImage(qrImageView);  // Hiệu ứng fade-in khi hiển thị ảnh
            }
        } else {
            // Thông báo nếu không có dữ liệu QR code
            Toast.makeText(this, "QR code data is missing.", Toast.LENGTH_SHORT).show();
        }

        // Bắt đầu đếm ngược 5 phút
        startCountDown();

        // Sự kiện khi nhấn "Back to Home Page"
        backToHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay về HomeFragment hoặc MainActivity
                Intent intent = new Intent(QrCodeDisplayActivity.this, HomeFragment.class); // Hoặc HomeFragment
                startActivity(intent);
                finish(); // Đảm bảo không quay lại activity này
            }
        });

        // Sự kiện khi nhấn "Save QR"
        saveQrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveQrImage();
            }
        });
    }

    private void startCountDown() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) { // 1000ms = 1 second
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {
                // Khi thời gian hết, chuyển sang HomeFragment
                Intent intent = new Intent(QrCodeDisplayActivity.this, MainActivity.class); // Hoặc HomeFragment
                startActivity(intent);
                finish(); // Đảm bảo không quay lại activity này
            }
        }.start();
    }

    private void updateTimer() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
        timerTextView.setText(timeLeftFormatted);
    }

    private Bitmap decodeBase64(String base64String) {
        try {
            // Giải mã Base64
            byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            // Kiểm tra nếu Bitmap hợp lệ
            if (bitmap == null) {
                // Nếu Bitmap là null, báo lỗi và trả lại null
                return null;
            }
            return bitmap;
        } catch (IllegalArgumentException e) {
            // Lỗi nếu Base64 không hợp lệ
            Toast.makeText(this, "Invalid Base64 string.", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void fadeInImage(ImageView imageView) {
        // Tạo hiệu ứng fade-in
        AlphaAnimation fadeIn = new AlphaAnimation(0, 1); // Từ độ mờ 0 đến 1
        fadeIn.setDuration(1000);  // Thời gian hiệu ứng 1 giây
        imageView.startAnimation(fadeIn);
    }

    private void saveQrImage() {
        // Lưu ảnh QR code vào điện thoại (Ví dụ, lưu vào bộ nhớ trong)
        qrImageView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(qrImageView.getDrawingCache());
        qrImageView.setDrawingCacheEnabled(false);

        // Tiến hành lưu ảnh vào bộ nhớ điện thoại (cần cấp quyền WRITE_EXTERNAL_STORAGE)
        String savedImagePath = saveImageToExternalStorage(bitmap);
        if (savedImagePath != null) {
            Toast.makeText(this, "QR code saved at: " + savedImagePath, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to save QR code.", Toast.LENGTH_SHORT).show();
        }
    }

    private String saveImageToExternalStorage(Bitmap bitmap) {
        // Kiểm tra nếu bộ nhớ ngoài có sẵn hay không
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            FileOutputStream fos = null;
            try {
                // Tạo thư mục "Pictures" nếu chưa có
                File picturesDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "SuperGym");
                if (!picturesDir.exists()) {
                    picturesDir.mkdirs();
                }

                // Tạo tên file ảnh và đường dẫn
                String fileName = "QR_Code_" + System.currentTimeMillis() + ".png";
                File file = new File(picturesDir, fileName);

                // Ghi ảnh vào file
                fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos); // Lưu với định dạng PNG và chất lượng 100%

                // Đảm bảo dữ liệu được ghi vào tệp
                fos.flush();
                Toast.makeText(this, "QR code saved at: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                return file.getAbsolutePath();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to save QR code.", Toast.LENGTH_SHORT).show();
                return null;
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            // Nếu bộ nhớ ngoài không sẵn có
            Toast.makeText(this, "External storage is not available.", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    // Nếu Android 10 trở lên, sử dụng MediaStore để lưu ảnh vào thư mục Public Pictures mà không cần quyền WRITE_EXTERNAL_STORAGE.
    private String saveImageToExternalStorageAPI29(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Đối với Android 10 trở lên, sử dụng MediaStore để lưu ảnh vào thư mục Pictures mà không cần quyền WRITE_EXTERNAL_STORAGE
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "QR_Code_" + System.currentTimeMillis() + ".png");
                contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/SuperGym");

                // Tạo URI cho file ảnh
                OutputStream fos = getContentResolver().openOutputStream(
                        getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                );

                if (fos != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();
                }

                Toast.makeText(this, "QR code saved successfully!", Toast.LENGTH_SHORT).show();
                return "QR code saved successfully!";
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to save QR code.", Toast.LENGTH_SHORT).show();
                return null;
            }
        } else {
            return saveImageToExternalStorage(bitmap); // Dành cho các phiên bản trước Android 10
        }
    }
}
