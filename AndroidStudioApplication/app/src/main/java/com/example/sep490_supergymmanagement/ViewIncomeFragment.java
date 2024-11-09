package com.example.sep490_supergymmanagement;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.database.*;

import java.text.*;
import java.util.*;

public class ViewIncomeFragment extends Fragment {

    private LineChart incomeLineChart;
    private Button startDateButton, endDateButton;
    private TextView totalIncomeTextView;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private Calendar calendar = Calendar.getInstance();
    private DatabaseReference incomeRef;
    private Spinner campusFilterSpinner;
    private ImageView btnDropDown;
    private Date startDate, endDate;
    private String selectedCampus = "All";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_view_income, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        incomeLineChart = view.findViewById(R.id.incomeLineChart);
        startDateButton = view.findViewById(R.id.startDateButton);
        endDateButton = view.findViewById(R.id.endDateButton);
        totalIncomeTextView = view.findViewById(R.id.totalIncome);
        campusFilterSpinner = view.findViewById(R.id.campusFilterSpinner);
        btnDropDown = view.findViewById(R.id.btnDropDown);

        incomeRef = FirebaseDatabase.getInstance().getReference("Income");

        startDateButton.setOnClickListener(v -> showDatePickerDialog(true));
        endDateButton.setOnClickListener(v -> showDatePickerDialog(false));
        btnDropDown.setOnClickListener(v -> campusFilterSpinner.performClick());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.campus_filter_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campusFilterSpinner.setAdapter(adapter);

        campusFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCampus = campusFilterSpinner.getSelectedItem().toString();
                if (startDate != null && endDate != null) {
                    fetchAndDisplayIncomeData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void showDatePickerDialog(boolean isStartDate) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(year, monthOfYear, dayOfMonth);
            String selectedDate = dateFormat.format(calendar.getTime());

            if (isStartDate) {
                startDateButton.setText("Start Date: " + selectedDate);
                try {
                    startDate = dateFormat.parse(selectedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                endDateButton.setText("End Date: " + selectedDate);
                try {
                    endDate = dateFormat.parse(selectedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            if (startDate != null && endDate != null) {
                fetchAndDisplayIncomeData();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void fetchAndDisplayIncomeData() {
        // Get selected campus from Spinner
        String selectedCampus = "campus_1";

        // Reference specific campus data in Firebase
        incomeRef.child(selectedCampus).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<IncomeData> incomeDataList = new ArrayList<>();
                float totalIncome = 0f;

                for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                    String dateStr = dateSnapshot.getKey();
                    Float income = dateSnapshot.child("totalIncome").getValue(Float.class);

                    try {
                        Date incomeDate = dateFormat.parse(dateStr);
                        if (incomeDate != null && !incomeDate.before(startDate) && !incomeDate.after(endDate)) {
                            incomeDataList.add(new IncomeData(incomeDate, income != null ? income : 0f));
                            totalIncome += income != null ? income : 0f;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                // Check if income data list is empty
                if (incomeDataList.isEmpty()) {
                    Toast.makeText(getContext(), "No income data available for the selected date range.", Toast.LENGTH_SHORT).show();
                } else {
                    // Update the total income TextView
                    displayTotalIncome(totalIncome);
                    drawIncomeChart(incomeDataList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to retrieve income data.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void drawIncomeChart(List<IncomeData> incomeDataList) {
        List<Entry> entries = new ArrayList<>();
        for (IncomeData data : incomeDataList) {
            float xValue = data.getDate().getTime();
            entries.add(new Entry(xValue, data.getIncome()));
        }

        if (entries.isEmpty()) {
            Toast.makeText(getContext(), "No income data available for the selected date range.", Toast.LENGTH_SHORT).show();
            return;
        }

        LineDataSet dataSet = new LineDataSet(entries, "Income Over Time");
        dataSet.setColor(getResources().getColor(R.color.teal_200));
        dataSet.setValueTextColor(getResources().getColor(R.color.black));
        dataSet.setLineWidth(2f);

        LineData lineData = new LineData(dataSet);
        incomeLineChart.setData(lineData);

        XAxis xAxis = incomeLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new DateValueFormatter());

        incomeLineChart.getDescription().setEnabled(false);
        incomeLineChart.invalidate();
    }

    private void displayTotalIncome(float totalIncome) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        String formattedIncome = currencyFormat.format(totalIncome);
        totalIncomeTextView.setText(formattedIncome);
    }

    private class DateValueFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            return dateFormat.format(new Date((long) value));
        }
    }

    private static class IncomeData {
        private final Date date;
        private final float income;

        public IncomeData(Date date, float income) {
            this.date = date;
            this.income = income;
        }

        public Date getDate() {
            return date;
        }

        public float getIncome() {
            return income;
        }
    }
}