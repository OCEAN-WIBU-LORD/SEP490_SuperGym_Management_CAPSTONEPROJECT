package com.supergym.sep490_supergymmanagement;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.supergym.sep490_supergymmanagement.FirebaseImageLoader.FirebaseImageLoader;
import com.supergym.sep490_supergymmanagement.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CheckLogFragment extends Fragment {

    private RecyclerView recyclerView;
    private Button btnSelectDate;
    private FirebaseImageLoader firebaseImageLoader;
    private String selectedDate;

    public CheckLogFragment() {
        // Required empty public constructor
    }

    public static CheckLogFragment newInstance() {
        return new CheckLogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("CheckLogFragment", "Fragment initialized.");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_log, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewLogs);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        btnSelectDate = view.findViewById(R.id.btnSelectDate);

        firebaseImageLoader = new FirebaseImageLoader(recyclerView, getContext());
        selectedDate = getCurrentDate();
        firebaseImageLoader.loadImages(true, selectedDate); // Load images for today by default

        btnSelectDate.setOnClickListener(v -> showDatePicker());

        addScrollListener();

        return view;
    }

    private void showDatePicker() {

        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedDate = formatDate(year, month, dayOfMonth);
                    btnSelectDate.setText(selectedDate);
                    Log.d("DateSelected", "Selected Date: " + selectedDate);
                    firebaseImageLoader.resetPagination();
                    // Reload images based on the selected date
                    firebaseImageLoader.loadImages(true, selectedDate);
                    Toast.makeText(getContext(), "Filtered by date: " + selectedDate, Toast.LENGTH_SHORT).show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void addScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && layoutManager.findLastVisibleItemPosition() == firebaseImageLoader.getImageList().size() - 1) {
                    // Trigger pagination (load more images) when reaching the last item
                    firebaseImageLoader.loadImages(false, selectedDate);
                }
            }
        });
    }

    private String formatDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormatter.format(calendar.getTime());
    }

    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormatter.format(calendar.getTime());
    }
}
