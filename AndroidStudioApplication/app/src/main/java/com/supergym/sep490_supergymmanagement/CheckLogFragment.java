package com.supergym.sep490_supergymmanagement;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.supergym.sep490_supergymmanagement.FirebaseImageLoader.FirebaseImageLoader;
import com.supergym.sep490_supergymmanagement.adapters.LogAdapter;
import com.supergym.sep490_supergymmanagement.adapters.YourImageAdapter;
import com.supergym.sep490_supergymmanagement.models.LogEntry;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CheckLogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CheckLogFragment extends Fragment {

    private RecyclerView recyclerView;

    public CheckLogFragment() {
        // Required empty public constructor
    }

    public static CheckLogFragment newInstance(String param1, String param2) {
        CheckLogFragment fragment = new CheckLogFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Retrieve any passed parameters if needed
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_log, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewLogs);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseImageLoader firebaseImageLoader = new FirebaseImageLoader(recyclerView, getContext());
        firebaseImageLoader.loadNewestImages();

        return view;
    }








}

/*
 private void loadAllImagesFromFacesFolder() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference facesRef = storage.getReference().child("faces");

        facesRef.listAll()
                .addOnSuccessListener(listResult -> {
                    imageUrls.clear(); // Clear previous data if reloading

                    // Loop through all items in the "/faces" folder
                    for (StorageReference item : listResult.getItems()) {
                        item.getDownloadUrl().addOnSuccessListener(uri -> {
                            imageUrls.add(uri.toString());  // Add the URL to the list
                            imageAdapter.notifyDataSetChanged();  // Notify the adapter
                        }).addOnFailureListener(e -> Log.e("CheckLogFragment", "Failed to get URL", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("CheckLogFragment", "Failed to list items", e));
    }

    private void loadLogEntries() {
        // Initialize logEntries list if not already initialized
        if (logEntries == null) {
            logEntries = new ArrayList<>();
        }

        // Assuming you're fetching data from Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("logs")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Clear the existing list before adding new data
                        logEntries.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Get the Firestore document fields
                            String imageUrl = document.getString("imageUrl");  // Firebase URL for the image
                            String userName = document.getString("userName");
                            String role = document.getString("role");
                            String checkInTime = document.getString("checkInTime");
                            String checkOutTime = document.getString("checkOutTime");
                            String datetime = document.getString("datetime");

                            // Create a new LogEntry object
                            LogEntry logEntry = new LogEntry(imageUrl, userName, role, checkInTime, checkOutTime, datetime);

                            // Add the LogEntry to the list
                            logEntries.add(logEntry);
                        }

                        // Notify the adapter that data has been updated
                        if (logAdapter != null) {
                            logAdapter.notifyDataSetChanged();
                        }

                    } else {
                        // Handle error
                        Toast.makeText(getContext(), "Error loading logs: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
 */