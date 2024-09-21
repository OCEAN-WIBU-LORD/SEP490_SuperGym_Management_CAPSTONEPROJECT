package com.example.sep490_supergymmanagement.views.appointment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sep490_supergymmanagement.models.TrainingLession;
import com.example.sep490_supergymmanagement.R;

import java.util.List;

public class TrainingLessionAdapter extends RecyclerView.Adapter<TrainingLessionAdapter.DiseaseViewHolder> {

    private List<TrainingLession> diseaseList;

    public TrainingLessionAdapter(List<TrainingLession> diseaseList) {
        this.diseaseList = diseaseList;
    }

    @NonNull
    @Override
    public DiseaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.traininglession, parent, false);
        return new DiseaseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DiseaseViewHolder holder, int position) {
        TrainingLession disease = diseaseList.get(position);
        holder.tvDiseaseName.setText(disease.getName());
        holder.tvDiseaseDescription.setText(disease.getDescription());
    }

    @Override
    public int getItemCount() {
        return diseaseList.size();
    }

    public static class DiseaseViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDiseaseName;
        private TextView tvDiseaseDescription;

        public DiseaseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDiseaseName = itemView.findViewById(R.id.tvDiseaseName);
            tvDiseaseDescription = itemView.findViewById(R.id.tvDiseaseDescription);
        }
    }
}