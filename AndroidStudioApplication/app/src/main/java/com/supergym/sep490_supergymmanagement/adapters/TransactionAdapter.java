package com.supergym.sep490_supergymmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.supergym.sep490_supergymmanagement.R;
import com.supergym.sep490_supergymmanagement.models.Transaction;
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
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvPackageName, tvAmount, tvPaymentStatus;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPackageName = itemView.findViewById(R.id.tvPackageName);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvPaymentStatus = itemView.findViewById(R.id.tvPaymentStatus);
        }
    }
}
