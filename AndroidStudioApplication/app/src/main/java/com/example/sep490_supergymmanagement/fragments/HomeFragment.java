package com.example.sep490_supergymmanagement.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sep490_supergymmanagement.LoginActivity;
import com.example.sep490_supergymmanagement.R;
import com.example.sep490_supergymmanagement.adapters.PostAdapter;
import com.example.sep490_supergymmanagement.databinding.ActivityMainBinding;
import com.example.sep490_supergymmanagement.models.Appointment;
import com.example.sep490_supergymmanagement.models.Post;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class HomeFragment extends Fragment {

    MaterialButton buttonLogOut;
    FirebaseAuth mAuth;
    //ProgressBar progressBar;
    TextView logOut, profileId, doctorName,dateInfo,timeInfo;
    ImageView doctorImage;
    FirebaseUser userDetails;
    Button btnProfile;
    Button appointmentDetailsBtn;
    DatabaseReference database, appointmentDatabase, doctorDatabase;
    PostAdapter postAdapter;
    RecyclerView recyclerView;
    List<Post> data;
    RelativeLayout relativeLayout;

    ActivityMainBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home2, container, false);
        mAuth = FirebaseAuth.getInstance();
        logOut = rootView.findViewById
                (R.id.logOut);
        //profileId = findViewById(R.id.profileId);
        doctorImage = rootView.findViewById(R.id.doctor_image);
        doctorName = rootView.findViewById(R.id.doctor_name);
        dateInfo = rootView.findViewById(R.id.date_info);
        timeInfo = rootView.findViewById(R.id.time_info);
        relativeLayout=rootView.findViewById(R.id.relativeLayout);
       // progressBar =  getActivity().findViewById(R.id.progress_bar);

        userDetails = mAuth.getCurrentUser();
        if (userDetails == null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        } else {
            // profileId.setText(userDetails.getEmail());
        }

        recyclerView = rootView.findViewById(R.id.postlist);
        database = FirebaseDatabase.getInstance().getReference().child("posts");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        data = new ArrayList<>();
        postAdapter = new PostAdapter(data, getActivity());
        recyclerView.setAdapter(postAdapter);
       // progressBar.setVisibility(View.VISIBLE);
        appointmentDatabase = FirebaseDatabase.getInstance().getReference().child("appointments");
        appointmentDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Appointment nearestAppointment = null;
                    long now = new Date().getTime();

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String status = dataSnapshot.hasChild("status") && dataSnapshot.child("status").getValue() != null ? dataSnapshot.child("status").getValue().toString() : "";
                        String userId = dataSnapshot.hasChild("userId") && dataSnapshot.child("userId").getValue() != null ? dataSnapshot.child("userId").getValue().toString() : "";
                        if ("unconfirmed".equals(status) && userId.equals(userDetails.getUid())) {
                            Integer startTimeValue = dataSnapshot.child("startTime").child("seconds").getValue(Integer.class);
                            Integer endTimeValue = dataSnapshot.child("endTime").child("seconds").getValue(Integer.class);
                            if (startTimeValue != null && endTimeValue != null) {
                                com.google.firebase.Timestamp startTs = new com.google.firebase.Timestamp(startTimeValue, 0);
                                if (startTs.getSeconds() * 1000 > now) {
                                    com.google.firebase.Timestamp endTs = new com.google.firebase.Timestamp(endTimeValue, 0);
                                    if (nearestAppointment == null || nearestAppointment.getStartTime() == null || startTs.compareTo(nearestAppointment.getStartTime()) < 0) {
                                        String appointmentId = dataSnapshot.getKey() != null ? dataSnapshot.getKey() : "";
                                        String diseasedId = dataSnapshot.hasChild("diseaseId") && dataSnapshot.child("diseaseId").getValue() != null ? dataSnapshot.child("diseaseId").getValue().toString() : "";
                                        String doctorId = dataSnapshot.hasChild("doctorId") && dataSnapshot.child("doctorId").getValue() != null ? dataSnapshot.child("doctorId").getValue().toString() : "";
                                        String note = dataSnapshot.hasChild("note") && dataSnapshot.child("note").getValue() != null ? dataSnapshot.child("note").getValue().toString() : "";
                                        String recordId = "0"; // Assuming 'recordId' is required but not present in the snapshot, using a default value
                                        nearestAppointment = new Appointment(appointmentId, diseasedId, doctorId, endTs, note, startTs, status, userId, recordId);
                                    }
                                }
                            }
                        }
                    }
                    if (nearestAppointment != null) {
                        relativeLayout.setVisibility(View.VISIBLE);

                        DatabaseReference doctorDatabase = FirebaseDatabase.getInstance().getReference().child("doctors").child(nearestAppointment.getDoctorId());
                        doctorDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot doctorSnapshot) {
                                if (doctorSnapshot.exists()) {
                                    String doctorNameStr = doctorSnapshot.hasChild("name") && doctorSnapshot.child("name").getValue() != null ? doctorSnapshot.child("name").getValue().toString() : "Unknown Doctor";
                                    doctorName.setText(doctorNameStr);

                                    String doctorImageUrl = doctorSnapshot.hasChild("imageurl") && doctorSnapshot.child("imageurl").getValue() != null ? doctorSnapshot.child("imageurl").getValue().toString() : "";
                                    if (!doctorImageUrl.isEmpty()) {
                                        Picasso.get().load(doctorImageUrl).into(doctorImage);
                                    } else {
                                        // Set a default image or leave it blank
                                        doctorImage.setImageResource(R.drawable.avatar); // Assuming `default_doctor_image` is a placeholder in your drawable resources
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getActivity(), "Failed to read doctor data from Firebase.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        System.out.println("aa " + nearestAppointment.getStartTime() + " bb " + nearestAppointment.getEndTime());
                        Date startDate = nearestAppointment.getEndTime().toDate();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        dateInfo.setText(dateFormat.format(startDate));
                        timeInfo.setText(timeFormat.format(startDate));
                    }
                }
               // progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Failed to read data from Firebase.", Toast.LENGTH_SHORT).show();
            }
        });
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = new Post();
                    //String i =Objects.requireNonNull(dataSnapshot.getKey().toString());
                    post.setPost_id(Objects.requireNonNull(dataSnapshot.getKey()));
                    post.setTitle(Objects.requireNonNull(dataSnapshot.child("title").getValue().toString()));
                    post.setContent(Objects.requireNonNull(dataSnapshot.child("content").getValue().toString()));
                    post.setAuthor(Objects.requireNonNull(dataSnapshot.child("author").getValue().toString()));
                    //   post.setCategory(Objects.requireNonNull(dataSnapshot.child("category").getValue().toString()));
                    post.setCategory("abc");
                    String date = Objects.requireNonNull(dataSnapshot.child("date").getValue().toString());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date dateformat = null;
                    try {
                        dateformat = dateFormat.parse(date);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }

                    post.setDate(dateformat);
                    data.add(post);
                }
                postAdapter.notifyDataSetChanged();
               // progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Failed to read data from Firebase.", Toast.LENGTH_SHORT).show();
               // progressBar.setVisibility(View.GONE);
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                Toast.makeText(getActivity(), "Log Out successfully", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });




        return rootView;
    }
}