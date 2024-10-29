package com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sep490_supergymmanagement.R;
import com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.models.Trainer;

import java.util.List;

public class TrainerAdapter extends RecyclerView.Adapter<TrainerAdapter.VH>{
    private List<Trainer> data;
    private Context context;
    private OnDoctorClickListener listener;


    public interface OnDoctorClickListener {
        void onDoctorClick(Trainer doctor);
    }
    public TrainerAdapter(List<Trainer> data, Context context, OnDoctorClickListener listener){
        this.data =data;
        this.context=context;
        this.listener = listener;

    }
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.post_card,parent,false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Trainer p = data.get(position);
        holder.setData(p);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDoctorClick(p);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class VH extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvInfo;

        public VH(@NonNull View itemView) {
            super(itemView);
            tvTitle =itemView.findViewById(R.id.tvTitle);
            tvInfo=itemView.findViewById(R.id.tvDate);
        }

        public void setData(Trainer p) {
            tvTitle.setText(p.getName());
            tvInfo.setText(p.getBio());
        }
    }
}
