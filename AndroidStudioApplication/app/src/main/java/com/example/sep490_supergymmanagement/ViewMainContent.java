package com.example.sep490_supergymmanagement;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
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

import com.example.sep490_supergymmanagement.RoleAdapter.UserRoleRetriever;
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
import java.util.Calendar;
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
    private ProgressBar progressBar;
    String fragmentType = "";

    FirebaseAuth mAuth;
    private String roleIdTxt, roleNameTxt;
    private View loadingOverlay;

    private String userId, userRole, roleId = null;
    @Override
    public void onStart(){
        super.onStart();
        //check if user is signed in (non-null) and Update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null ){
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            Toast.makeText(ViewMainContent.this, "User Un-Authenticated, please login!", Toast.LENGTH_LONG).show();
            startActivity(intent);
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_main_content);
        loadingOverlay = findViewById(R.id.loading_overlay);
        // Check if we should load the AdminDashBoard fragment
        String fragmentToLoad = getIntent().getStringExtra("load_fragment");
        if ("AdminDashBoard".equals(fragmentToLoad)) {
            // Load the AdminDashBoard fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new DashBoardAdmin())
                    .commit();
        } else {
            // Load the default Home fragment or any other fragment you want
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new HomeFragment())
                    .commit();
        }


        progressBar = findViewById(R.id.progressBar); // Initialize the ProgressBar
        if (savedInstanceState == null) {

            loadFragment(new HomeFragment());
        }
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(ViewMainContent.this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        userId = user.getUid();
        showLoadingOverlay();
        // Authentication check and UI setup
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fab = findViewById(R.id.fab);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        /*if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
        replaceFragment(new HomeFragment());*/

        loadUserDetails();
        if(roleNameTxt != null){
            checkAdmin(roleNameTxt.toString().trim());
        }


        fab.setOnClickListener(view -> showBottomDialog());
    }

    private void showLoadingOverlay() {
        loadingOverlay.setVisibility(View.VISIBLE);
    }

    private void hideLoadingOverlay() {
        loadingOverlay.setVisibility(View.GONE);

    }

    private void checkAdmin(String roleName){
        if(roleName.equals("admin")){
            replaceFragment(new DashBoardAdmin());
            Toast.makeText(ViewMainContent.this, "You Logged In As Admin!", Toast.LENGTH_SHORT).show();
        }

    }


    // Updated getRoleName method
    private void getRoleName(String roleId) {
        DatabaseReference roleRef = FirebaseDatabase.getInstance().getReference("Roles").child(roleId);
        roleRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot roleSnapshot) {
                if (roleSnapshot.exists()) {
                    roleNameTxt = roleSnapshot.child("RoleName").getValue(String.class);
                    if (roleNameTxt != null) {
                        setupBottomNavigation(roleNameTxt); // Set up UI based on the retrieved role
                        invalidateOptionsMenu(); // Refresh the menu once the role is loaded
                        hideLoadingOverlay();
                        checkAdmin(roleNameTxt.toString().trim());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideLoadingOverlay();
                Toast.makeText(ViewMainContent.this, "Failed to retrieve role name.", Toast.LENGTH_SHORT).show();
            }
        });
    }







    private void updateMenuBasedOnRole(Menu menu) {
        if ("admin".equals(roleNameTxt)) {
            menu.findItem(R.id.subscriptions).setVisible(false); // Hide "Appointment Schedule"
            menu.add(0, R.id.income, Menu.NONE, "Income")
                    .setIcon(R.drawable.ic_income)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
    }



    private void setupBottomNavigation(String roleName) {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fab = findViewById(R.id.fab);
// Clear existing items and set the menu dynamically
        bottomNavigationView.getMenu().clear();


        if ("admin".equals(roleName)) {
            // Add "Income" for admin role
            bottomNavigationView.getMenu().add(Menu.NONE, R.id.admindashboard, Menu.NONE, "Dash Board")
                    .setIcon(R.drawable.ic_home);
        } else {
            // Add "Appointment Schedule" for non-admin roles
            bottomNavigationView.getMenu().add(Menu.NONE, R.id.home, Menu.NONE, "Home")
                    .setIcon(R.drawable.ic_home);
        }



        if ("admin".equals(roleName)) {
            // Add "Income" for admin role
            bottomNavigationView.getMenu().add(Menu.NONE, R.id.checklog, Menu.NONE, "Check Log")
                    .setIcon(R.drawable.check_log_ic);
        } else {
            // Add "Appointment Schedule" for non-admin roles
            bottomNavigationView.getMenu().add(Menu.NONE, R.id.shorts, Menu.NONE, "Search")
                    .setIcon(R.drawable.ic_search);
        }

        bottomNavigationView.getMenu().add(Menu.NONE, R.id.shorts, Menu.NONE, "")
                .setIcon(null);
        if ("admin".equals(roleName)) {
            // Add "Income" for admin role
            bottomNavigationView.getMenu().add(Menu.NONE, R.id.income, Menu.NONE, "Income")
                    .setIcon(R.drawable.income_ic_2);
        } else {
            // Add "Appointment Schedule" for non-admin roles
            bottomNavigationView.getMenu().add(Menu.NONE, R.id.subscriptions, Menu.NONE, "Appointment Schedule")
                    .setIcon(R.drawable.ic_appointment);
        }
        bottomNavigationView.getMenu().add(Menu.NONE, R.id.library, Menu.NONE, "Profile")
                .setIcon(R.drawable.ic_profile);

        // Configure bottom navigation based on role
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.shorts) {
                replaceFragment(new SearchFragment());
            } else if (itemId == R.id.subscriptions) {
                if (roleName.equals("pt")) {
                    replaceFragment(new ScheduleTrainer());
                } else if (roleName.equals("customer")) {
                    replaceFragment(new ProgressActivity());
                }
            } else if (itemId == R.id.library) {
                replaceFragment(new FragmentUserProfile());
            } else if (itemId == R.id.income) {
                replaceFragment(new ViewIncomeFragment());
            }else if (itemId == R.id.checklog) {
                replaceFragment(new CheckLogFragment());
            }else if (itemId == R.id.admindashboard) {
                replaceFragment(new DashBoardAdmin());
            }

            return true;
        });

        fab.setOnClickListener(view -> showBottomDialog());
    }

    private void loadUserDetails() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Retrieve basic user info
                        String avatarBase64 = dataSnapshot.child("userAvatar").getValue(String.class);
                        String name = dataSnapshot.child("name").getValue(String.class);
                        String roleId = dataSnapshot.child("roleId").getValue(String.class);

                        // Retrieve the user's role name based on roleId
                        if (roleId != null) {
                            getRoleName(roleId);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    hideLoadingOverlay();
                    Toast.makeText(ViewMainContent.this, "Failed to load user details.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    // Call this in `getRoleName` after setting `roleNameTxt`




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
        LinearLayout layoutFaceID = dialog.findViewById(R.id.layoutFaceID);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);
        layoutFaceID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewMainContent.this, RegisterFaceActivity.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        videoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new GeminiFragment());
                dialog.dismiss();
                Toast.makeText(ViewMainContent.this,"Gemini AI Opened" + roleNameTxt,Toast.LENGTH_SHORT).show();

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
