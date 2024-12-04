package com.supergym.sep490_supergymmanagement;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import androidx.annotation.NonNull;

import com.supergym.sep490_supergymmanagement.apiadapter.ApiService.ApiService;
import com.supergym.sep490_supergymmanagement.apiadapter.RetrofitClient;
import com.supergym.sep490_supergymmanagement.models.RegisterUserDto;
import com.supergym.sep490_supergymmanagement.models.User;
import com.supergym.sep490_supergymmanagement.repositories.UserResp;
import com.supergym.sep490_supergymmanagement.repositories.callbacks.Callback;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
public class RegisterActivity extends AppCompatActivity {

    EditText editTextName, editTextEmail, editTextPassword, editTextRePassword;
    MaterialButton buttonReg;
    TextView forgotPassTv;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    private boolean isPasswordVisible = false;
    private boolean isRePasswordVisible = false;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && mAuth.getCurrentUser().isEmailVerified()) {
            Intent intent = new Intent(getApplicationContext(), ViewMainContent.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextName = findViewById(R.id.name);
        editTextPassword = findViewById(R.id.password);
        editTextRePassword = findViewById(R.id.re_password);
        buttonReg = findViewById(R.id.registerbtn);
        progressBar = findViewById(R.id.progressBar);
        forgotPassTv = findViewById(R.id.forgotPassTv);

        forgotPassTv.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start ScheduleTrainerDetailsActivity
                Intent intent = new Intent(getApplicationContext(), ForgotPassword.class); // Assuming ScheduleTrainerDetails is now an Activity
                startActivity(intent);
            }
        });
        // Find the return button by its ID
        ImageButton returnButton = findViewById(R.id.btnReturn);

        // Set OnClickListener to the return button
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Define the action to be performed when the button is clicked
                onBackPressed(); // This will navigate back to the previous activity
            }
        });

        editTextPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.password_icon, 0);
        editTextRePassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_verified_24, 0);

        editTextPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (editTextPassword.getRight() - editTextPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        togglePasswordVisibility(editTextPassword, true);
                        return true;
                    }
                }
                return false;
            }
        });
        // Set up the listener for the re-password field
        editTextRePassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (editTextRePassword.getRight() - editTextRePassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        togglePasswordVisibility(editTextRePassword, false);
                        return true;
                    }
                }
                return false;
            }
        });

// Add this method to validate password


        // Set OnClickListener to the register button
        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the registerUser function
                progressBar.setVisibility(View.VISIBLE);
                String name = editTextName.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String rePassword = editTextRePassword.getText().toString().trim();
                // Validate the inputs
                // Validate the inputs
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), getString(R.string.name_required), Toast.LENGTH_SHORT).show();
                    editTextName.setError(getString(R.string.name_required));
                    editTextName.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), getString(R.string.email_required), Toast.LENGTH_SHORT).show();
                    editTextEmail.setError(getString(R.string.email_required));
                    editTextEmail.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
                    editTextEmail.setError(getString(R.string.invalid_email));
                    editTextEmail.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), getString(R.string.password_required), Toast.LENGTH_SHORT).show();
                    editTextPassword.setError(getString(R.string.password_required));
                    editTextPassword.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (!isValidPassword(password)) {
                    editTextPassword.setError(getString(R.string.invalid_password));
                    editTextPassword.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), getString(R.string.password_too_short), Toast.LENGTH_SHORT).show();
                    editTextPassword.setError(getString(R.string.password_too_short));
                    editTextPassword.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (!password.equals(rePassword)) {
                    Toast.makeText(getApplicationContext(), getString(R.string.passwords_do_not_match), Toast.LENGTH_SHORT).show();
                    editTextRePassword.setError(getString(R.string.passwords_do_not_match));
                    editTextRePassword.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

// Create a user DTO for API call
                RegisterUserDto registerUserDto = new RegisterUserDto(name, email, password);

// Call the API to register the user
                ApiService apiService = RetrofitClient.getApiService(v.getContext());
                Call<Void> call = apiService.registerUser(registerUserDto);
                call.enqueue(new retrofit2.Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Account Created. Please verify your email.",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (response.code() == 409) { // Check if the status code is 409 Conflict
                            showLoginPrompt(); // Show the login prompt if the email is already registered
                        } else {
                            Toast.makeText(RegisterActivity.this, "Error creating user: " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(RegisterActivity.this, "Failed to register: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private boolean isValidPassword(String password) {
        // Updated regex to require at least one digit
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=!]).{6,}$";
        return password.matches(passwordPattern);
    }


    private void showLoginPrompt() {
        new AlertDialog.Builder(this)
                .setTitle("Account Already Exists")
                .setMessage("An account with this email already exists. Would you like to log in instead?")
                .setPositiveButton("Log In", (dialog, which) -> {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void togglePasswordVisibility(EditText editText, boolean isPasswordField) {
        if (isPasswordField) {
            if (isPasswordVisible) {
                // Hide password
                editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.password_icon, 0);
            } else {
                // Show password
                editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hide_icon, 0);
            }
            isPasswordVisible = !isPasswordVisible;
        } else {
            if (isRePasswordVisible) {
                // Hide password
                editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.password_icon, 0);
            } else {
                // Show password
                editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hide_icon, 0);
            }
            isRePasswordVisible = !isRePasswordVisible;
        }
        editText.setSelection(editText.length()); // Move cursor to the end
    }

}