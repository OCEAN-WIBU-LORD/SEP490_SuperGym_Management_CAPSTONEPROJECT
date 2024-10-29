package com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.sep490_supergymmanagement.R;


public class UserProfileFragment extends Fragment {

    CardView editCardView;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View rootView= inflater.inflate(R.layout.fragment_user_profile, container, false);
        editCardView = rootView.findViewById(R.id.cvProfile);


        editCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start LoginActivity
                Intent intent = new Intent(getActivity(), Fragment.class);

                // Start the LoginActivity
                startActivity(intent);
            }
        });
        return rootView;
    }
}