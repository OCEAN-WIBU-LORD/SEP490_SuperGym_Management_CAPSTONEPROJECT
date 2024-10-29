package com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sep490_supergymmanagement.R;
import com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.models.TrainingLession;

import java.util.List;

public class TrainingLessionAdapter extends RecyclerView.Adapter<TrainingLessionAdapter.VH>{
    private List<TrainingLession> data;
    private Context context;
    private int selectedItem = RecyclerView.NO_POSITION;
    private OnDiseaseClickListener onDiseaseClickListener;
    public TrainingLessionAdapter(List<TrainingLession> data, Context context, OnDiseaseClickListener onDiseaseClickListener){
        this.data=data;
        this.context=context;
        this.onDiseaseClickListener = onDiseaseClickListener;
    }
    int count = 0;
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_disease,parent,false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        TrainingLession p = data.get(position);
        holder.bind(p, position);



    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class VH extends RecyclerView.ViewHolder {
        private Button buttonDisease;

        public VH(@NonNull View itemView) {
            super(itemView);
            buttonDisease = itemView.findViewById(R.id.btnDisease);
            buttonDisease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Set selected item
                        selectedItem = position;
                        notifyDataSetChanged(); // Refresh adapter to update colors
                        TrainingLession disease = data.get(position);
                        onDiseaseClickListener.onDiseaseClicked(disease);
                    }
                }
            });

        }




        public void bind(TrainingLession p, int position) {
            buttonDisease.setText(p.getName());

            if (position == selectedItem) {
                buttonDisease.setBackgroundColor(context.getResources().getColor(R.color.black));
            } else {
                buttonDisease.setBackgroundColor(context.getResources().getColor(R.color.blue));
            }
        }
    }
    public interface OnDiseaseClickListener {
        void onDiseaseClicked(TrainingLession disease);
    }
}
