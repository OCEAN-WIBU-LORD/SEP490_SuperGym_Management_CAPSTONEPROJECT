package com.example.sep490_supergymmanagement;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sep490_supergymmanagement.adapters.PostAdapter;
import com.example.sep490_supergymmanagement.databinding.ActivityMainBinding;
import com.example.sep490_supergymmanagement.databinding.ActivityViewMainContentBinding;
import com.example.sep490_supergymmanagement.fragments.AppointmentFragment;
import com.example.sep490_supergymmanagement.fragments.AppointmentListDoctorFragment;
import com.example.sep490_supergymmanagement.fragments.HomeFragment;
import  com.example.sep490_supergymmanagement.R;
import com.example.sep490_supergymmanagement.fragments.SearchFragment;
import com.example.sep490_supergymmanagement.fragments.UserProfileFragment;
import com.example.sep490_supergymmanagement.models.Appointment;
import com.example.sep490_supergymmanagement.models.Post;
import com.example.sep490_supergymmanagement.views.appointment.AppointmentDetailsActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ViewMainContent extends AppCompatActivity {

    ActivityViewMainContentBinding binding;
    Fragment homeFragment, userProfileFragment,
            appointmentFragment, searchFragment,
            activeFragment, geminiFragment,
            appointmentListDoctorFragment;
    String fragmentType = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewMainContentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        homeFragment = new HomeFragment();
        userProfileFragment = new FragmentUserProfile();
        geminiFragment = new GeminiFragment();
        activeFragment = homeFragment;

        isUserADoctor(isDoctor -> {
            if (isDoctor) {
                appointmentListDoctorFragment = new AppointmentListDoctorFragment();
                fragmentType = "Doctor";

                activeFragment = homeFragment;
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.frame_layout, appointmentListDoctorFragment, "6").hide(appointmentListDoctorFragment)
                        .add(R.id.frame_layout, geminiFragment, "4").hide(geminiFragment)
                        .add(R.id.frame_layout, userProfileFragment, "2").hide(userProfileFragment)
                        .add(R.id.frame_layout, homeFragment, "1")
                        .commit();
            } else {
                // User-specific setup
                appointmentFragment = new ProgressActivity();
                searchFragment = new SearchFragment();
                fragmentType = "User";

                activeFragment = homeFragment;
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.frame_layout, appointmentFragment, "6").hide(appointmentFragment)
                        .add(R.id.frame_layout, searchFragment, "5").hide(searchFragment)
                        .add(R.id.frame_layout, geminiFragment, "4").hide(geminiFragment)
                        .add(R.id.frame_layout, userProfileFragment, "2").hide(userProfileFragment)
                        .add(R.id.frame_layout, homeFragment, "1")
                        .commit();
            }
        });


        binding.bottomnavigation.setOnItemSelectedListener(item -> {
            switch (item.getTitle().toString()) {
                case "Home":
                    showFragment(homeFragment);
                    break;
                case "Profile":

                    showFragment(userProfileFragment);

                    break;
                case "Gemini Analysis":
                    showFragment(geminiFragment);
                    break;

                case "Appointment":
                    if(fragmentType.equals("Doctor")) {
                        showFragment(appointmentListDoctorFragment);
                    }else{
                        showFragment(appointmentFragment);
                    }
                    break;
                case "Search":
                    showFragment(searchFragment);
                    break;
                default:
                    break;
            }
            return true;
        });

    }
    private void showFragment(Fragment fragment) {
        if (fragment != null && fragment.isAdded()) {
            getSupportFragmentManager().beginTransaction()
                    .hide(activeFragment)
                    .show(fragment)
                    .commitAllowingStateLoss(); // Use commit() if you want to handle state loss manually.
            activeFragment = fragment;
        } else {
            // Handle the case where fragment is null or not added.
            Log.e("FragmentTransaction", "Fragment is null or not added.");
        }
    }
    public interface DoctorCheckCallback {
        void onChecked(boolean isDoctor);
    }
    private void isUserADoctor(DoctorCheckCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.onChecked(false);
            return;
        }
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("role");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && "doctor".equals(dataSnapshot.getValue(String.class))) {
                    callback.onChecked(true);
                } else {
                    callback.onChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("isUserADoctor", "Failed to read value.", databaseError.toException());
                callback.onChecked(false);
            }
        });
    }
}
