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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import androidx.appcompat.app.AlertDialog;

import java.util.List;
public class ForgotPassword extends AppCompatActivity {

    EditText editTextCurrentPass, editTextEmail, editTextPassword, editTextRePassword;
    MaterialButton buttonReg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    private boolean isPasswordVisible = false;
    private boolean isRePasswordVisible = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);

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


        // Set OnClickListener to the register button
        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the registerUser function
                progressBar.setVisibility(View.VISIBLE);
                String editTextEmailTxt = editTextEmail.getText().toString().trim();

                // Validate the inputs
                if (TextUtils.isEmpty(editTextEmailTxt)) {
                    Toast.makeText(getApplicationContext(), "Gmail is required", Toast.LENGTH_SHORT).show();
                    editTextEmail.setError("Gmail is required");
                    editTextEmail.requestFocus();
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
                resetPassword(editTextEmailTxt);
            }
        });
    }

    private void resetPassword(String email) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Notify the user that the email was sent
                            Toast.makeText(ForgotPassword.this, "Password reset email sent.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        } else {
                            // Handle errors (e.g., user does not exist, invalid email)
                            Toast.makeText(ForgotPassword.this, "Failed to send reset email.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }




}