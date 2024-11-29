package com.supergym.sep490_supergymmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.supergym.sep490_supergymmanagement.apiadapter.RetrofitClient;
import com.supergym.sep490_supergymmanagement.models.ForgotPasswordRequest;
import com.supergym.sep490_supergymmanagement.apiadapter.ApiService.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassword extends AppCompatActivity {

    EditText editTextEmail;
    MaterialButton buttonReg, backToLoginBtn;  // Thêm backToLoginBtn
    ProgressBar progressBar;
    ApiService apiService; // Declare the ApiService
    TextView successMessage; // TextView để hiển thị thông báo thành công
    TextView enter, enteremail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        // Initialize views
        editTextEmail = findViewById(R.id.email);
        buttonReg = findViewById(R.id.registerbtn);
        progressBar = findViewById(R.id.progressBar);
        successMessage = findViewById(R.id.message); // Khởi tạo TextView thông báo thành công
        enter = findViewById(R.id.enter);
        enteremail = findViewById(R.id.enteremail);
        backToLoginBtn = findViewById(R.id.backToLoginBtn);  // Khởi tạo nút Back to Login

        // Get ApiService instance from RetrofitClient
        apiService = RetrofitClient.getApiService(this);

        // Set OnClickListener for the register button
        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the resetPassword function
                progressBar.setVisibility(View.VISIBLE);
                enter.setVisibility(View.GONE);
                enteremail.setVisibility(View.GONE);
                String email = editTextEmail.getText().toString().trim();

                // Validate email input
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Email is required", Toast.LENGTH_SHORT).show();
                    editTextEmail.setError("Email is required");
                    editTextEmail.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid email", Toast.LENGTH_SHORT).show();
                    editTextEmail.setError("Please enter a valid email");
                    editTextEmail.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                resetPassword(email); // Call the reset password API method
            }
        });

        // Back button setup
        ImageButton returnButton = findViewById(R.id.btnReturn);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Navigate back to the previous activity
            }
        });

        // Set OnClickListener for "Back to Login" button
        backToLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start LoginActivity when the button is clicked
                Intent intent = new Intent(ForgotPassword.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Close ForgotPassword activity
            }
        });
    }

    // Method to reset password using API
    private void resetPassword(String email) {
        ForgotPasswordRequest request = new ForgotPasswordRequest(email);

        // Make the API call using Retrofit's ApiService
        apiService.sendPasswordResetEmail(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    // Notify user of successful email sending
                    Toast.makeText(ForgotPassword.this, "Password reset email sent.", Toast.LENGTH_SHORT).show();

                    // Ẩn các phần tử ban đầu và hiển thị thông báo thành công
                    editTextEmail.setVisibility(View.GONE);
                    buttonReg.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    successMessage.setVisibility(View.VISIBLE); // Hiển thị thông báo

                    // Hiển thị nút Back to Login
                    backToLoginBtn.setVisibility(View.VISIBLE);  // Hiển thị nút "Back to Login"
                } else {
                    // Handle failure (e.g., invalid email)
                    Toast.makeText(ForgotPassword.this, "Failed to send reset email.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                // Handle failure (e.g., network error)
                Toast.makeText(ForgotPassword.this, "API call failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
