package com.example.sep490_supergymmanagement;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import androidx.annotation.NonNull;

import com.example.sep490_supergymmanagement.models.User;
import com.example.sep490_supergymmanagement.repositories.UserResp;
import com.example.sep490_supergymmanagement.repositories.callbacks.Callback;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

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
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "Name is required", Toast.LENGTH_SHORT).show();
                    editTextName.setError("Name is required");
                    editTextName.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
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

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Password is required", Toast.LENGTH_SHORT).show();
                    editTextPassword.setError("Password is required");
                    editTextPassword.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password should be at least 6 characters long", Toast.LENGTH_SHORT).show();
                    editTextPassword.setError("Password should be at least 6 characters long");
                    editTextPassword.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (!password.equals(rePassword)) {
                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                    editTextRePassword.setError("Passwords do not match");
                    editTextRePassword.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    if (user == null) {
                                        Toast.makeText(RegisterActivity.this, "Register account failed. Please try again.",
                                                Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "User is null");
                                        return;
                                    }

                                    // Create user
                                    User newUser = new User(user.getUid(), name, email, null, null, null, null, null, null, "user", null);
                                    // Save user to database
                                    new UserResp().createUser(newUser, new Callback<User>() {
                                        @Override
                                        public void onCallback(List<User> objects) {
                                            if (objects != null && !objects.isEmpty()) {
                                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(RegisterActivity.this, "Account Created. Please verify your email.",
                                                                    Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        } else {
                                                            Toast.makeText(RegisterActivity.this, "Error sending verification email.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(RegisterActivity.this, "Error creating user in database.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                } else {
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                        showLoginPrompt();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            }
        });
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