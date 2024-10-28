package com.example.sep490_supergymmanagement;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
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

    FloatingActionButton fab;
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;

    String fragmentType = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_main_content);
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        //authentication
        bottomNavigationView= findViewById(R.id.bottomNavigationView);
        fab =findViewById(R.id.fab);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);


        ActionBarDrawerToggle toggle  = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
        replaceFragment(new HomeFragment());



        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if(itemId == R.id.home){
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.shorts) {
                replaceFragment(new SearchFragment());
            } else if (itemId == R.id.subscriptions) {
                replaceFragment(new AppointmentFragment());
            } else if (itemId == R.id.library) {
                replaceFragment(new FragmentUserProfile());
            }

            return true;
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
        });


        homeFragment = new HomeFragment();
        userProfileFragment = new FragmentUserProfile();
        geminiFragment = new GeminiFragment();
        activeFragment = homeFragment;

        isUserATrainer(isTrainer -> {
            if (isTrainer) {
                bottomNavigationView.setBackground(null);
                bottomNavigationView.setOnItemSelectedListener(item -> {
                    int itemId = item.getItemId();
                    if(itemId == R.id.home){
                        replaceFragment(new HomeFragment());
                    } else if (itemId == R.id.shorts) {
                        replaceFragment(new SearchFragment());
                    } else if (itemId == R.id.subscriptions) {
                        replaceFragment(new ScheduleTrainer());
                    } else if (itemId == R.id.library) {
                        replaceFragment(new FragmentUserProfile());
                    }

                    return true;
                });
            } else {
                // User-specific setup
                bottomNavigationView.setBackground(null);
                bottomNavigationView.setOnItemSelectedListener(item -> {
                    int itemId = item.getItemId();
                    if(itemId == R.id.home){
                        replaceFragment(new HomeFragment());
                    } else if (itemId == R.id.shorts) {
                        replaceFragment(new SearchFragment());
                    } else if (itemId == R.id.subscriptions) {
                        replaceFragment(new ProgressActivity());
                    } else if (itemId == R.id.library) {
                        replaceFragment(new FragmentUserProfile());
                    }

                    return true;
                });
            }
        });







    }



    //Outside OnCreate.
    private  void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void showBottomDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);

        LinearLayout videoLayout = dialog.findViewById(R.id.layoutVideo);
        LinearLayout shortsLayout = dialog.findViewById(R.id.layoutShorts);
        LinearLayout liveLayout = dialog.findViewById(R.id.layoutLive);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        videoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new GeminiFragment());
                dialog.dismiss();
                Toast.makeText(ViewMainContent.this,"Upload a Video is clicked",Toast.LENGTH_SHORT).show();

            }
        });

        shortsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewMainContent.this, HomeActivity.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        liveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Toast.makeText(ViewMainContent.this,"Go live is Clicked",Toast.LENGTH_SHORT).show();

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

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
        void onChecked(boolean isTrainer);
    }
    private void isUserATrainer(DoctorCheckCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.onChecked(false);
            return;
        }
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("role");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && "trainer".equals(dataSnapshot.getValue(String.class))) {
                    callback.onChecked(true);
                } else {
                    callback.onChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("isUserATrainer", "Failed to read value.", databaseError.toException());
                callback.onChecked(false);
            }
        });
    }
}
