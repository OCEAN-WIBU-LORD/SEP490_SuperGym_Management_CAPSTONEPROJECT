package com.example.sep490_supergymmanagement.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sep490_supergymmanagement.R;

import java.util.List;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.VH>{
    private List<String> data;
    private Context context;
    private int selectedItem = RecyclerView.NO_POSITION;
    private OnStatusClickListener onStatusClickListener;


    public StatusAdapter(List<String> data, Context context,OnStatusClickListener onStatusClickListener){
        this.data = data;
        this.context=context;
        this.onStatusClickListener = onStatusClickListener;


    }
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_disease,parent,false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        String p = data.get(position);
        holder.bind(p, position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class VH extends RecyclerView.ViewHolder {
        private Button buttonStatus;

        public VH(@NonNull View itemView) {
            super(itemView);
            buttonStatus = itemView.findViewById(R.id.btnDisease);
            buttonStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Set selected item
                        selectedItem = position;
                        notifyDataSetChanged();
                        String status = data.get(position);
                       onStatusClickListener.onStatusClicked(status);
                    }
                }
            });
        }

        public void setData(String p) {
        }

        public void bind(String p, int position) {
            buttonStatus.setText(p);

            if (position == selectedItem) {
                buttonStatus.setBackgroundColor(context.getResources().getColor(R.color.black));
            } else {
                buttonStatus.setBackgroundColor(context.getResources().getColor(R.color.blue));
            }
            buttonStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // If the current position is already selected, deselect it
                    if (selectedItem == position) {
                        selectedItem = RecyclerView.NO_POSITION;
                    } else {
                        selectedItem = position;
                    }
                    notifyDataSetChanged();
                    onStatusClickListener.onStatusClicked(p);
                }
            });
        }
    }
    public interface OnStatusClickListener {
        void onStatusClicked(String status);
    }
}
