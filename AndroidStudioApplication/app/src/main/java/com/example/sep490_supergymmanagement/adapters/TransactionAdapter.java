package com.example.sep490_supergymmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sep490_supergymmanagement.R;
import com.example.sep490_supergymmanagement.models.Transaction;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private List<Transaction> transactionList;

    public TransactionAdapter(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    // Cập nhật danh sách giao dịch khi có thay đổi từ hoạt động lọc hoặc sắp xếp
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

        // Định dạng ngày từ chuỗi
        Date date = transaction.getFormattedTransactionDate();
        if (date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy, HH:mm", Locale.getDefault());
            holder.tvTransactionDate.setText(dateFormat.format(date));
        } else {
            holder.tvTransactionDate.setText("N/A"); // Nếu ngày không hợp lệ
        }

        holder.tvTransactionDescription.setText(transaction.getDescription());
        holder.tvTransactionPaymentMethod.setText(transaction.getPaymentMethod());
        holder.tvTransactionAmount.setText(String.format(Locale.getDefault(), "%,.0f VND", transaction.getAmount()));
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvTransactionDate, tvTransactionDescription, tvTransactionPaymentMethod, tvTransactionAmount;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTransactionDate = itemView.findViewById(R.id.tvTransactionDate);
            tvTransactionDescription = itemView.findViewById(R.id.tvTransactionDescription);
            tvTransactionPaymentMethod = itemView.findViewById(R.id.tvTransactionPaymentMethod);
            tvTransactionAmount = itemView.findViewById(R.id.tvTransactionAmount);
        }
    }
}
