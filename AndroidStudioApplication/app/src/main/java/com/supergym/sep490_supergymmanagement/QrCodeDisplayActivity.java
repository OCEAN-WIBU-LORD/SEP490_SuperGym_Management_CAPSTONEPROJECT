package com.supergym.sep490_supergymmanagement;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.supergym.sep490_supergymmanagement.fragments.HomeFragment;

import java.util.Locale;

public class QrCodeDisplayActivity extends AppCompatActivity {

    private ImageView qrImageView;
    private TextView timerTextView;
    private Button backToHomeButton;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 5 * 60 * 1000; // 5 minutes in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_display);

        qrImageView = findViewById(R.id.qrCodeImageView);
        timerTextView = findViewById(R.id.timerTextView);
        backToHomeButton = findViewById(R.id.backToHomeButton);

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
                // Gửi Intent quay lại Activity_Book_Trainer
                Intent intent = new Intent(QrCodeDisplayActivity.this, Activity_Book_Trainer.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Dùng flag này để clear Activity_Book_Trainer nếu nó đang ở trong back stack
                startActivity(intent);

                // Đóng QrCodeDisplayActivity
                finish(); // Đóng Activity QrCodeDisplay để không quay lại
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
                // Khi thời gian hết, chuyển về HomeFragment
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment())
                        .commit();
                finish(); // Đóng QrCodeDisplayActivity để không quay lại
            }
        }.start();
    }

    private void updateTimer() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timerTextView.setText(timeLeftFormatted);
    }

    private Bitmap decodeBase64(String base64String) {
        try {
            // Giải mã Base64
            byte[] decodedString = android.util.Base64.decode(base64String, android.util.Base64.DEFAULT);
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
        android.view.animation.AlphaAnimation fadeIn = new android.view.animation.AlphaAnimation(0, 1); // Từ độ mờ 0 đến 1
        fadeIn.setDuration(1000);  // Thời gian hiệu ứng 1 giây
        imageView.startAnimation(fadeIn);
    }
}
