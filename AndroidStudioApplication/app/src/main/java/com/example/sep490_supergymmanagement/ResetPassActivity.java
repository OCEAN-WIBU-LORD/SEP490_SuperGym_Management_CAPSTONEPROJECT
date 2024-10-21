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
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import androidx.appcompat.app.AlertDialog;

import java.util.List;
public class ResetPassActivity extends AppCompatActivity {

    EditText editTextCurrentPass, editTextEmail, editTextPassword, editTextRePassword;
    MaterialButton buttonReg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView forgotPassTv;
    private boolean isPasswordVisible = false;
    private boolean isRePasswordVisible = false;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null && mAuth.getCurrentUser().isEmailVerified()) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password_screen);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextCurrentPass = findViewById(R.id.current_password);
        editTextPassword = findViewById(R.id.new_password);
        editTextRePassword = findViewById(R.id.confirm_re_password);
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

        // Set OnClickListener to the register button
        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the registerUser function
                progressBar.setVisibility(View.VISIBLE);
                String editTextEmailTxt = editTextEmail.getText().toString().trim();
                String editTextCurrentPassTxt = editTextCurrentPass.getText().toString().trim();
                String editTextPasswordTxt = editTextPassword.getText().toString().trim();
                String editTextRePasswordTxt = editTextRePassword.getText().toString().trim();
                // Validate the inputs
                if (TextUtils.isEmpty(editTextEmailTxt)) {
                    Toast.makeText(getApplicationContext(), "Gmail is required", Toast.LENGTH_SHORT).show();
                    editTextEmail.setError("Gmail is required");
                    editTextEmail.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (TextUtils.isEmpty(editTextCurrentPassTxt)) {
                    Toast.makeText(getApplicationContext(), "Current Password is required", Toast.LENGTH_SHORT).show();
                    editTextCurrentPass.setError("Current Password is required");
                    editTextCurrentPass.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(editTextEmailTxt).matches()) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid email", Toast.LENGTH_SHORT).show();
                    editTextEmail.setError("Please enter a valid email");
                    editTextEmail.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (TextUtils.isEmpty(editTextPasswordTxt)) {
                    Toast.makeText(getApplicationContext(), "Password is required", Toast.LENGTH_SHORT).show();
                    editTextPassword.setError("Password is required");
                    editTextPassword.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (editTextPasswordTxt.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password should be at least 6 characters long", Toast.LENGTH_SHORT).show();
                    editTextPassword.setError("Password should be at least 6 characters long");
                    editTextPassword.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (!editTextPasswordTxt.equals(editTextRePasswordTxt)) {
                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                    editTextRePassword.setError("Passwords do not match");
                    editTextRePassword.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                // Reauthenticate the user with their current password
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    AuthCredential credential = EmailAuthProvider.getCredential(editTextEmailTxt, editTextCurrentPassTxt);

                    user.reauthenticate(credential).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // If reauthentication is successful, update the password
                            user.updatePassword(editTextRePasswordTxt).addOnCompleteListener(updateTask -> {
                                if (updateTask.isSuccessful()) {
                                    sendConfirmationEmail(user);
                                } else {
                                    Toast.makeText(ResetPassActivity.this, "Password update failed: " + updateTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                progressBar.setVisibility(View.GONE);
                            });
                        } else {
                            Toast.makeText(ResetPassActivity.this, "Reauthentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                    }
            }
        });
    }

    private void showToastAndError(EditText editText, String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        editText.setError(message);
        editText.requestFocus();
        progressBar.setVisibility(View.GONE);
    }

    private void sendConfirmationEmail(FirebaseUser user) {
        user.sendEmailVerification().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ResetPassActivity.this, "Password reset successfully. Please verify your email for confirmation.", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.putExtra("password_reset_success", true);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(ResetPassActivity.this, "Failed to send confirmation email.", Toast.LENGTH_SHORT).show();
            }
        });
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