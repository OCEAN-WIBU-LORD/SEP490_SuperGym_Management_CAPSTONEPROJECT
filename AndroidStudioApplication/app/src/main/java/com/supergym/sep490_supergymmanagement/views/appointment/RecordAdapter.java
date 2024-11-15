package com.supergym.sep490_supergymmanagement.views.appointment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.supergym.sep490_supergymmanagement.models.Record;
import com.supergym.sep490_supergymmanagement.R;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {

    private List<Record> recordList;

    public RecordAdapter(List<Record> recordList) throws JSONException {
        this.recordList = recordList;
    }

    public void setRecords(List<Record> recordList) {
        this.recordList = recordList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.record, parent, false);
        return new RecordViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        Record record = recordList.get(position);
        holder.tvDiagnosis.setText(record.getDiagnosis());
        holder.tvTreatment.setText(record.getTreatment());

        Date date = record.getDate().toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy", Locale.US);
        String formattedDate = sdf.format(date);
        holder.tvDate.setText(formattedDate);
        holder.tvExtraData.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public static class RecordViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDiagnosis;
        private TextView tvTreatment;
        private TextView tvDate;
        private TextView tvExtraData;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDiagnosis = itemView.findViewById(R.id.tvDiagnosis);
            tvTreatment = itemView.findViewById(R.id.tvTreatment);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvExtraData = itemView.findViewById(R.id.tvExtraData);
        }
    }
}
