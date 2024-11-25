package com.supergym.sep490_supergymmanagement.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.supergym.sep490_supergymmanagement.Activity_Book_Trainer;
import com.supergym.sep490_supergymmanagement.AddAppointment;
import com.supergym.sep490_supergymmanagement.LoginActivity;
import com.supergym.sep490_supergymmanagement.R;
import com.supergym.sep490_supergymmanagement.TrainerDetailActivity;
import com.supergym.sep490_supergymmanagement.ViewTrainerDetails;
import com.supergym.sep490_supergymmanagement.adapters.TrainerAdapter;
import com.supergym.sep490_supergymmanagement.models.Trainer;
import com.supergym.sep490_supergymmanagement.repositories.TrainerResp;
import com.supergym.sep490_supergymmanagement.repositories.callbacks.Callback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class SearchTrainerFragment extends Fragment {
    private RecyclerView trainerRecyclerView;
    private EditText searchEditText;

    private FirebaseAuth mAuth;
    private FirebaseUser userDetails;
    private TrainerAdapter trainerAdapter;
    private TrainerResp trainerResp;

    private List<Trainer> trainerData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_trainer, container, false);

        mAuth = FirebaseAuth.getInstance();
        userDetails = mAuth.getCurrentUser();

        trainerRecyclerView = rootView.findViewById(R.id.recycler_view_trainers);
        searchEditText = rootView.findViewById(R.id.search_input_trainer);

        trainerData = new ArrayList<>();
        trainerResp = new TrainerResp();

        if (userDetails == null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        } else {
            initTrainerRecyclerView();

            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String searchQuery = s.toString();
                    loadTrainersByName(searchQuery);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }

        return rootView;
    }

    private void initTrainerRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        trainerRecyclerView.setLayoutManager(layoutManager);

        trainerAdapter = new TrainerAdapter(trainerData, getActivity(), trainer -> {
            if (trainer == null) {
                Toast.makeText(getContext(), "Trainer data is missing", Toast.LENGTH_SHORT).show();
                return;
            }

            if (trainer.getTrainerId() != null) {
                Intent intent = new Intent(getActivity(), ViewTrainerDetails.class);
                intent.putExtra("trainerId", trainer.getTrainerId());
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Invalid trainer data", Toast.LENGTH_SHORT).show();
            }
        });



        trainerRecyclerView.setAdapter(trainerAdapter);

        loadAllTrainers();
    }



    private void loadAllTrainers() {
        trainerResp.getAllTrainers(new Callback<Trainer>() {
            @Override
            public void onCallback(List<Trainer> trainers) {
                trainerData.clear();
                trainerData.addAll(trainers);
                trainerAdapter.notifyDataSetChanged();
            }
        });
    }

    private void loadTrainersByName(String searchQuery) {
        trainerResp.getTrainersByName(searchQuery, new Callback<Trainer>() {
            @Override
            public void onCallback(List<Trainer> trainers) {
                if (trainers != null) {
                    trainerData.clear();
                    trainerData.addAll(trainers);
                    trainerAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "No trainers found", Toast.LENGTH_SHORT).show();
                    trainerData.clear();
                    trainerAdapter.notifyDataSetChanged();
                }
            }
        });
    }


    /*@Override
    public void onTrainerClick(Trainer trainer) {
        Intent intent = new Intent(getActivity(), ViewTrainerDetails.class); // Update the target class
        intent.putExtra("trainerId", trainer.getUserId()); // Pass the trainerId
        Log.d("TRAINER", "Trainer ID: " + trainer.getUserId() + " | Name: " + trainer.getName());
        startActivity(intent);
    }*/

}
