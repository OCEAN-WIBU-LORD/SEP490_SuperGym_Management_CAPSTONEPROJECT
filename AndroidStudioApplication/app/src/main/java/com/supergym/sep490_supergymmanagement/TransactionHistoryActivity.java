package com.supergym.sep490_supergymmanagement;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.supergym.sep490_supergymmanagement.adapters.TransactionAdapter;
import com.supergym.sep490_supergymmanagement.apiadapter.ApiService.ApiService;
import com.supergym.sep490_supergymmanagement.apiadapter.RetrofitClient;
import com.supergym.sep490_supergymmanagement.models.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionHistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactionList;
    private Spinner spinnerFilter, spinnerSort;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        // Lấy userId từ Firebase Auth
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup Back Button
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerViewTransactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactionList = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(transactionList); // Pass initial list
        recyclerView.setAdapter(transactionAdapter);

        // Setup Filter and Sort Spinners
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

        // Fetch Transactions via API
        fetchTransactionsFromApi();

        // Listen to Filter and Sort Spinner Events
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

    // Fetch Transactions from API
    private void fetchTransactionsFromApi() {
        ApiService apiService = RetrofitClient.getApiService(this);

        apiService.getPaymentHistory(userId).enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    transactionList.clear();
                    transactionList.addAll(response.body());
                    transactionAdapter.setTransactions(transactionList); // Update adapter's list
                    applyFilterAndSort(); // Apply filter and sort after fetching data
                } else {
                    Log.e("TransactionHistory", "Failed to fetch transactions: " + response.message());
                    Toast.makeText(TransactionHistoryActivity.this, "Failed to load transactions", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                Log.e("TransactionHistory", "API Error: " + t.getMessage(), t);
                Toast.makeText(TransactionHistoryActivity.this, "Failed to load transactions", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Apply Filter and Sort
    private void applyFilterAndSort() {
        // Filter Transactions
        String selectedFilter = spinnerFilter.getSelectedItem().toString();
        List<Transaction> filteredList = new ArrayList<>();

        for (Transaction transaction : transactionList) {
            if ("All".equals(selectedFilter) || transaction.getPackageName().contains(selectedFilter)) {
                filteredList.add(transaction);
            }
        }

        // Sort Transactions
        String selectedSort = spinnerSort.getSelectedItem().toString();
        if ("Amount Ascending".equals(selectedSort)) {
            Collections.sort(filteredList, Comparator.comparingDouble(Transaction::getAmount));
        } else if ("Amount Descending".equals(selectedSort)) {
            Collections.sort(filteredList, (t1, t2) -> Double.compare(t2.getAmount(), t1.getAmount()));
        } else if ("Package Name".equals(selectedSort)) {
            Collections.sort(filteredList, Comparator.comparing(Transaction::getPackageName));
        }

        // Update Adapter
        transactionAdapter.setTransactions(filteredList); // Update filtered list
    }
}
