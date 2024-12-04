package com.supergym.sep490_supergymmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.supergym.sep490_supergymmanagement.R;
import com.supergym.sep490_supergymmanagement.models.Transaction;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private List<Transaction> transactionList;

    public TransactionAdapter(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    // Phương thức cập nhật danh sách giao dịch
    public void setTransactions(List<Transaction> newTransactionList) {
        this.transactionList = newTransactionList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);

        // Gán giá trị vào giao diện
        holder.tvPackageName.setText(transaction.getPackageName());
        holder.tvAmount.setText(String.format(Locale.getDefault(), "%,.0f VND", transaction.getAmount()));
        holder.tvPaymentStatus.setText(transaction.getPaymentStatus());

        // Chuyển đổi định dạng ngày bắt đầu và kết thúc
        String formattedStartDate = formatDate(transaction.getStartDate());
        String formattedEndDate = formatDate(transaction.getEndDate());

        holder.tvStartDate.setText("Start Date: " + formattedStartDate);
        holder.tvEndDate.setText("End Date: " + formattedEndDate);
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvPackageName, tvAmount, tvPaymentStatus, tvStartDate, tvEndDate;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPackageName = itemView.findViewById(R.id.tvPackageName);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvPaymentStatus = itemView.findViewById(R.id.tvPaymentStatus);
            tvStartDate = itemView.findViewById(R.id.tvStartDate);
            tvEndDate = itemView.findViewById(R.id.tvEndDate);
        }
    }

    // Hàm chuyển đổi định dạng ngày từ API sang định dạng mong muốn
    private String formatDate(String dateStr) {
        // Định dạng từ API: "2025-03-02T09:39:51.1934404+07:00"
        SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSXXX", Locale.getDefault());
        // Định dạng mong muốn: "dd/MM/yyyy"
        SimpleDateFormat desiredFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        try {
            Date date = apiFormat.parse(dateStr);
            return desiredFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateStr; // Trả về nguyên chuỗi nếu lỗi
        }
    }
}
