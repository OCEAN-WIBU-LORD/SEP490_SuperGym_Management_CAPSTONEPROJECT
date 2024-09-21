package com.example.sep490_supergymmanagement.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sep490_supergymmanagement.AddAppointment;
import com.example.sep490_supergymmanagement.LoginActivity;
import com.example.sep490_supergymmanagement.R;
import com.example.sep490_supergymmanagement.adapters.TrainerAdapter;
import com.example.sep490_supergymmanagement.adapters.TrainingLessionAdapter;
import com.example.sep490_supergymmanagement.models.Trainer;
import com.example.sep490_supergymmanagement.models.TrainingLession;
import com.example.sep490_supergymmanagement.repositories.TrainingLessionResp;
import com.example.sep490_supergymmanagement.repositories.TrainerResp;
import com.example.sep490_supergymmanagement.repositories.callbacks.Callback;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements TrainingLessionAdapter.OnDiseaseClickListener, TrainerAdapter.OnDoctorClickListener  {
    RecyclerView recyclerView;
    RecyclerView doctorRecyclerView;
    EditText searchEditText;

    Button buttonDisease;
    FirebaseAuth mAuth;
    FirebaseUser userDetails;
    TrainingLessionAdapter diseaseAdapter;
    TrainerAdapter trainerAdapter;
    TrainingLessionResp diseaseResp;
    TrainerResp doctorResp;

    List<TrainingLession> data;
    List<Trainer> doctordata;

    private String currentTrainingLessionId = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        mAuth = FirebaseAuth.getInstance();
        userDetails = mAuth.getCurrentUser();
        recyclerView = rootView.findViewById(R.id.rcDiseased);
        doctorRecyclerView = rootView.findViewById(R.id.rcDoctors);
        searchEditText = rootView.findViewById(R.id.search_input);
        data = new ArrayList<>();
        doctordata= new ArrayList<>();
        diseaseResp = new TrainingLessionResp();
        doctorResp = new TrainerResp();

        if (userDetails == null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        } else {
            initDoctorRecyclerView();

            initDiseaseButton();
            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String searchQuery = s.toString();
                    if (currentTrainingLessionId != null) {
                        loadDoctorsByTrainingLessionIdAndName(currentTrainingLessionId, searchQuery);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
        return rootView;
    }

    private void initDoctorRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        doctorRecyclerView.setLayoutManager(layoutManager);
        trainerAdapter = new TrainerAdapter(doctordata,getActivity(), (TrainerAdapter.OnDoctorClickListener) this);
        doctorRecyclerView.setAdapter(trainerAdapter);
        loadDoctorsFromFirebase(new Callback<Trainer>() {
            @Override
            public void onCallback(List<Trainer> objects) {
                doctordata.clear();
                doctordata.addAll(objects);
                trainerAdapter.notifyDataSetChanged();
            }
        });

    }

    private void initDiseaseButton() {
        // Set the FlexboxLayoutManager for the RecyclerView
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getActivity());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);

        recyclerView.setLayoutManager(layoutManager);

        // Initialize the adapter with the data
        diseaseAdapter = new TrainingLessionAdapter(data, getActivity(),this);
        recyclerView.setAdapter(diseaseAdapter);

        loadDiseasesFromFirebase(new Callback<TrainingLession>() {
            @Override
            public void onCallback(List<TrainingLession> list) {
                data.clear();
                data.addAll(list);
                diseaseAdapter.notifyDataSetChanged();
            }
        });



    }

    private void loadDoctorsFromFirebase(Callback<Trainer> callback) {
        doctorResp.getDoctors(callback);

    }

    private void loadDiseasesFromFirebase(Callback<TrainingLession> callback) {
        diseaseResp.getDisease(callback);

    }

    @Override
    public void onDiseaseClicked(TrainingLession disease) {
        currentTrainingLessionId = disease.getTrainingLessionId();
        String searchQuery = searchEditText.getText().toString();
        loadDoctorsByTrainingLessionIdAndName(currentTrainingLessionId, searchQuery);
    }

    private void loadDoctorsByTrainingLessionIdAndName(String diseaseId, String searchQuery) {
        doctorResp.getDoctorsByTrainingLessionIdAndName(searchQuery, diseaseId, new Callback<Trainer>() {
            @Override
            public void onCallback(List<Trainer> objects) {
                doctordata.clear();
                doctordata.addAll(objects);
                trainerAdapter.notifyDataSetChanged();
            }
        });
    }
    @Override
    public void onDoctorClick(Trainer doctor) {
        Intent intent = new Intent(getActivity(), AddAppointment.class);
        intent.putExtra("selectedDoctor",doctor); // Ensure Doctor class implements Serializable or Parcelable
        Log.d("DOCTOR",doctor.getTrainerId()+doctor.getName());
        startActivity(intent);
    }
}