package com.supergym.sep490_supergymmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class LoadingActivity extends AppCompatActivity {

    private boolean isQrReady = false; // Biến để kiểm tra trạng thái QR code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        // Tiến hành gọi API hoặc tác vụ cần thiết từ đây
        processApi();
    }

    private void processApi() {
        // Giả lập một tác vụ dài (ví dụ: gọi API) hoặc thời gian chờ trước khi có QR code
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Mô phỏng thời gian delay 3 giây
                // Nếu chưa có QR code, chuyển sang màn hình hiển thị QR code mặc định
                if (!isQrReady) {
                    goToQrCodeDisplay();
                }
            }
        }, 5000);  // Giả lập thời gian gọi API (3 giây)

        // Giả lập việc có QR code sau một thời gian nữa
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Mô phỏng nhận được dữ liệu QR code sau 2 giây (hoặc thực tế khi API trả về)
                String qrBase64 = "BASE64_STRING_HERE";  // Đây là dữ liệu Base64 thực tế từ API
                isQrReady = true; // Đánh dấu là QR code đã sẵn sàng

                // Chuyển sang màn hình QR code sau khi có dữ liệu
                goToQrCodeDisplay(qrBase64);
            }
        }, 2000);  // Giả lập API trả dữ liệu sau 2 giây
    }

    // Phương thức chuyển sang màn hình QR code
    private void goToQrCodeDisplay(String qrBase64) {
        Intent intent = new Intent(LoadingActivity.this, QrCodeDisplayActivity.class);
        intent.putExtra("qr_base64", qrBase64);  // Truyền QR code base64 string
        startActivity(intent);

        // Sử dụng fade-in effect để chuyển đổi mượt mà
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        finish();  // Đảm bảo LoadingActivity bị hủy sau khi chuyển màn hình
    }

    // Phương thức chuyển sang màn hình QR code khi QR code chưa sẵn sàng
    private void goToQrCodeDisplay() {
        String qrBase64 = "DEFAULT_BASE64_STRING";  // Trường hợp không có QR code (ví dụ, QR mặc định)
        goToQrCodeDisplay(qrBase64);
    }
}
