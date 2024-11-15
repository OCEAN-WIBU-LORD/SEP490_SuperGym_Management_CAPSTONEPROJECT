package com.supergym.sep490_supergymmanagement;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.supergym.sep490_supergymmanagement.adapters.TransactionAdapter;
import com.supergym.sep490_supergymmanagement.models.Transaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class TransactionHistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactionList;
    private DatabaseReference transactionRef;
    private String userId;
    private Spinner spinnerFilter, spinnerSort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        // Lấy userId từ Firebase Auth
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Thiết lập RecyclerView
        recyclerView = findViewById(R.id.recyclerViewTransactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactionList = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(transactionList);
        recyclerView.setAdapter(transactionAdapter);

        // Thiết lập Spinner cho lọc và sắp xếp
        spinnerFilter = findViewById(R.id.spinnerFilter);
        spinnerSort = findViewById(R.id.spinnerSort);

        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(this,
                R.array.filter_options, android.R.layout.simple_spinner_item);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(filterAdapter);

        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(this,
                R.array.sort_options, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(sortAdapter);

        // Truy cập Firebase node "TransactionHistory/{userId}"
        if (userId != null) {
            transactionRef = FirebaseDatabase.getInstance().getReference("TransactionHistory").child(userId);
            fetchTransactionsFromFirebase();
        }

        // Lắng nghe sự kiện thay đổi trên Spinner để lọc và sắp xếp
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilterAndSort();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilterAndSort();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // Hàm lấy giao dịch từ Firebase và cập nhật RecyclerView chỉ với status "completed"
    private void fetchTransactionsFromFirebase() {
        transactionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                transactionList.clear();
                SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Transaction transaction = snapshot.getValue(Transaction.class);

                    if (transaction != null && "completed".equals(transaction.getStatus())) {
                        String dateString = snapshot.child("transactionDate").getValue(String.class);
                        if (dateString != null) {
                            transaction.setTransactionDate(dateString);
                        }
                        transactionList.add(transaction);
                    }
                }
                applyFilterAndSort();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("TransactionHistory", "Error loading transactions", databaseError.toException());
                Toast.makeText(TransactionHistoryActivity.this, "Failed to load transactions", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hàm áp dụng lọc và sắp xếp dựa trên Spinner
    private void applyFilterAndSort() {
        // Lấy lựa chọn từ Spinner Filter
        String selectedFilter = spinnerFilter.getSelectedItem().toString();
        List<Transaction> filteredList = new ArrayList<>();

        for (Transaction transaction : transactionList) {
            if ("All".equals(selectedFilter) || transaction.getDescription().contains(selectedFilter)) {
                filteredList.add(transaction);
            }
        }

        // Lấy lựa chọn từ Spinner Sort
        String selectedSort = spinnerSort.getSelectedItem().toString();
        if ("Date Ascending".equals(selectedSort)) {
            Collections.sort(filteredList, Comparator.comparing(Transaction::getTransactionDate));
        } else if ("Date Descending".equals(selectedSort)) {
            Collections.sort(filteredList, (t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate()));
        } else if ("Amount Ascending".equals(selectedSort)) {
            Collections.sort(filteredList, Comparator.comparing(Transaction::getAmount));
        } else if ("Amount Descending".equals(selectedSort)) {
            Collections.sort(filteredList, (t1, t2) -> Double.compare(t2.getAmount(), t1.getAmount()));
        }

        // Cập nhật Adapter với danh sách đã lọc và sắp xếp
        transactionAdapter.setTransactions(filteredList);
        transactionAdapter.notifyDataSetChanged();
    }
}
