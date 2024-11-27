package com.supergym.sep490_supergymmanagement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoadingActivity extends Activity {
    private ProgressBar progressBar;
    private TextView loadingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        // Tìm các view cần thiết
        progressBar = findViewById(R.id.progress_bar);
        loadingText = findViewById(R.id.loading_text);

        // Set trạng thái mặc định cho LoadingActivity
        loadingText.setText("Processing... Please wait.");

        // Có thể chạy một thread để cập nhật tiến độ nếu cần thiết.
        // Ở đây chúng ta sẽ chỉ hiển thị vòng quay tải.
    }
}
