package com.example.sep490_supergymmanagement.adapters;
//set adapter cho session detail
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sep490_supergymmanagement.R;
import com.example.sep490_supergymmanagement.models.Set;

import java.util.List;

public class SetAdapter extends RecyclerView.Adapter<SetAdapter.SetViewHolder> {

    private List<Set> setList;

    public SetAdapter(List<Set> setList) {
        this.setList = setList;
    }

    @NonNull
    @Override
    public SetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_set, parent, false);
        return new SetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SetViewHolder holder, int position) {
        Set set = setList.get(position);
        holder.setInfo.setText(set.getWeight() + "kg " + set.getRepetitions() + " reps");
    }

    @Override
    public int getItemCount() {
        return setList.size();
    }

    static class SetViewHolder extends RecyclerView.ViewHolder {
        TextView setInfo;

        public SetViewHolder(@NonNull View itemView) {
            super(itemView);
            setInfo = itemView.findViewById(R.id.set_info);
        }
    }
}
