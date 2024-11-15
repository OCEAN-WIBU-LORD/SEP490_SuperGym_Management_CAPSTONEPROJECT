package com.supergym.sep490_supergymmanagement;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Timer;
import java.util.TimerTask;

public class LoginActivity extends AppCompatActivity {


    EditText editTextEmail, editTextPassword;
    MaterialButton buttonLogin;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    private boolean isPasswordVisible = false;
    private Timer loginTimer;
    TextView textView, forgotPassTv;

    private long lastUserInteractionTime; // Store the last interaction timestamp
    private static final long INACTIVITY_TIMEOUT = 600000000; // 10 minutes in milliseconds
    private Handler handler; // Handler object for periodic checks

    @Override
    public void onStart(){
        super.onStart();
        //check if user is signed in (non-null) and Update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null && mAuth.getCurrentUser().isEmailVerified()){
            Intent intent = new Intent(getApplicationContext(),ViewMainContent.class);
            startActivity(intent);
            finish();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("password_reset_success", false)) {
            Toast.makeText(LoginActivity.this, "Password reset successful. Please log in with your new password.", Toast.LENGTH_LONG).show();
        }
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        handler = new Handler();
        startInactivityCheck(); // Start periodic checks
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
                finish(); // This will finish the current activity and go back to the previous one
            }
        });

        editTextPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.password_icon, 0);

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

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(LoginActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Start the timer with a 5-minute delay (300 seconds)
                loginTimer = new Timer();
                loginTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // Perform timeout action (e.g., display message, reset UI)
                        Toast.makeText(LoginActivity.this, "Login timed out!", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        // Optional: reset UI elements like buttons
                        buttonLogin.setEnabled(true);  // Enable button again
                    }
                }, 30000000); // 300 seconds = 5 minutes

                // ... rest of the login code ...
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if(task.isSuccessful()){
                                    if (loginTimer != null) {
                                        loginTimer.cancel();
                                    }
                                    if(mAuth.getCurrentUser().isEmailVerified()){
                                        Toast.makeText(getApplicationContext(),"Login Successful",
                                                Toast.LENGTH_SHORT).show();
                                        loadDashboardBasedOnRole();
                                    }else{
                                        Toast.makeText(LoginActivity.this, "Please Verify Email before login.",
                                                Toast.LENGTH_SHORT).show();
                                    }

                                }else{
                                    Toast.makeText(LoginActivity.this, "Authentication Failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });



    }
    //getRole Name
// Function to check role and load appropriate dashboard
    private void loadDashboardBasedOnRole() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

            // Retrieve the role of the user from the database
            userRef.child("role").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String role = dataSnapshot.getValue(String.class);
                    if ("admin".equals(role)) {
                        // Load Admin Dashboard
                        Intent intent = new Intent(LoginActivity.this, ViewMainContent.class);
                        intent.putExtra("load_fragment", "AdminDashBoard");
                        startActivity(intent);
                        finish();
                    } else {
                        // Load Default Dashboard for other roles
                        Intent intent = new Intent(LoginActivity.this, ViewMainContent.class);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(LoginActivity.this, "Failed to load user role.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }







    // Function to open Facebook app
    // Method to open Facebook app
    public void openFacebookApp(View view) {
        try {
            // Facebook URL
            String facebookUrl = "https://www.fb.com/";

            // Create Intent with Facebook URL
            Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
            facebookIntent.setData(Uri.parse(facebookUrl));

            // Check if Facebook app is installed, if not, open in browser
            if (facebookIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(facebookIntent);
            } else {
                // If Facebook app is not installed, open in browser
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Function to open Google app
    // Method to open Google app
    public void openGoogleApp(View view) {
        try {
            // Facebook URL
            String googleUrl = "https://www.google.com/";

            // Create Intent with Facebook URL
            Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
            facebookIntent.setData(Uri.parse(googleUrl));

            // Check if Facebook app is installed, if not, open in browser
            if (facebookIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(facebookIntent);
            } else {
                // If Facebook app is not installed, open in browser
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(googleUrl)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startInactivityCheck() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkInactivity();
                handler.postDelayed(this, 6000000); // Check again in a minute
            }
        }, 6000000); // Initial delay of 1 minute
    }

    private void checkInactivity() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastUserInteractionTime;

        if (elapsedTime > INACTIVITY_TIMEOUT) {
            mAuth.signOut();
            // Inform user about logout (e.g., Toast message, navigate to login)
            Toast.makeText(LoginActivity.this, "Logged out due to inactivity.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
            finish();
            // Optional: navigate to login screen
        } else {
            // User is still active, reset the timer
            lastUserInteractionTime = currentTime;
        }
    }

    // ... other activity methods ...

    @Override
    public void onPause() {
        super.onPause();
        // Update user interaction time when app goes to background
        lastUserInteractionTime = System.currentTimeMillis();
    }
    // Function to open Google app
    // Method to open Google app
    public void openGithubApp(View view) {
        try {
            // Facebook URL
            String googleUrl = "https://www.github.com/";

            // Create Intent with Facebook URL
            Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
            facebookIntent.setData(Uri.parse(googleUrl));

            // Check if Facebook app is installed, if not, open in browser
            if (facebookIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(facebookIntent);
            } else {
                // If Facebook app is not installed, open in browser
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(googleUrl)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Function to open Google app
    // Method to open Google app
    public void openTwitterApp(View view) {
        try {
            // Facebook URL
            String googleUrl = "https://www.x.com/";

            // Create Intent with Facebook URL
            Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
            facebookIntent.setData(Uri.parse(googleUrl));

            // Check if Facebook app is installed, if not, open in browser
            if (facebookIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(facebookIntent);
            } else {
                // If Facebook app is not installed, open in browser
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(googleUrl)));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        }
        editText.setSelection(editText.length()); // Move cursor to the end
    }



}