package com.supergym.sep490_supergymmanagement;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class QrCodeDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_display);

        ImageView qrImageView = findViewById(R.id.qrCodeImageView);

        Intent intent = getIntent();
        String qrBase64 = intent.getStringExtra("qr_base64");

        if (qrBase64 != null && !qrBase64.isEmpty()) {
            Bitmap qrBitmap = base64ToBitmap(qrBase64);
            if (qrBitmap != null) {
                qrImageView.setImageBitmap(qrBitmap); // Display the QR code
            } else {
                Toast.makeText(this, "Failed to decode QR code.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "QR code data is missing.", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap base64ToBitmap(String base64String) {
        try {
            byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } catch (IllegalArgumentException e) {
            Log.e("Base64Error", "Failed to decode Base64 string: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


}