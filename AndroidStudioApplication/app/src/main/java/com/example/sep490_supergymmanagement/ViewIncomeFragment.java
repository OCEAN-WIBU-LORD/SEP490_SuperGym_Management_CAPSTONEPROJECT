package com.example.sep490_supergymmanagement;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
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
    private ImageView btnDropDown, btnDropDown2;
    private Date startDate, endDate;
    private String selectedCampus = "All campuses";
    private String selectedType = "All types";

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

        // In your Activity or Fragment
        Spinner filterTypeSpinner = view.findViewById(R.id.FilterTypeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.type_filter_options, R.layout.spinner_item_multiline);
        adapter.setDropDownViewResource(R.layout.spinner_item_multiline);
        filterTypeSpinner.setAdapter(adapter);



        btnDropDown2 = view.findViewById(R.id.btnDropDown2);
        btnDropDown2.setOnClickListener(v -> filterTypeSpinner.performClick());

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getContext(), R.array.campus_filter_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campusFilterSpinner.setAdapter(adapter1);


        filterTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedType = filterTypeSpinner.getSelectedItem().toString();
                if (startDate != null && endDate != null) {
                    fetchAndDisplayIncomeData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });



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
        String selectedCampus = campusFilterSpinner.getSelectedItem().toString().trim();

        if (selectedCampus.equals("All campuses")) {
            // Retrieve income data for all campuses
            incomeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        Toast.makeText(getContext(), "No income data available.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<IncomeData> incomeDataList = new ArrayList<>();
                    float totalIncome = 0f;

                    for (DataSnapshot campusSnapshot : snapshot.getChildren()) {
                        for (DataSnapshot dateSnapshot : campusSnapshot.getChildren()) {
                            String dateStr = dateSnapshot.getKey();
                            Float income = dateSnapshot.child("totalIncome").getValue(Float.class);
                            String type = dateSnapshot.child("typeIncome").getValue(String.class);

                            if (income == null) income = 0f;
                            if (type == null) type = "";

                            try {
                                Date incomeDate = dateFormat.parse(dateStr);
                                if (incomeDate != null) {
                                    // Apply date and type filtering
                                    if (!incomeDate.before(startDate) && !incomeDate.after(endDate) &&
                                            (selectedType.equals("All types") || selectedType.equals(type))) {
                                        incomeDataList.add(new IncomeData(incomeDate, income));
                                        totalIncome += income;
                                    }
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (incomeDataList.isEmpty()) {
                        Toast.makeText(getContext(), "No income data available for the selected date range and type.", Toast.LENGTH_SHORT).show();
                    } else {
                        displayTotalIncome(totalIncome);
                        drawIncomeChart(incomeDataList);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Failed to retrieve income data.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Retrieve income data for a specific campus
            incomeRef.child(selectedCampus).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        Toast.makeText(getContext(), "No income data available for the selected campus.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<IncomeData> incomeDataList = new ArrayList<>();
                    float totalIncome = 0f;

                    for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                        String dateStr = dateSnapshot.getKey();
                        Float income = dateSnapshot.child("totalIncome").getValue(Float.class);
                        String type = dateSnapshot.child("typeIncome").getValue(String.class);

                        if (income == null) income = 0f;
                        if (type == null) type = "";

                        try {
                            Date incomeDate = dateFormat.parse(dateStr);
                            if (incomeDate != null) {
                                // Apply date and type filtering
                                if (!incomeDate.before(startDate) && !incomeDate.after(endDate) &&
                                        (selectedType.equals("All types") || selectedType.equals(type))) {
                                    incomeDataList.add(new IncomeData(incomeDate, income));
                                    totalIncome += income;
                                }
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    if (incomeDataList.isEmpty()) {
                        Toast.makeText(getContext(), "No income data available for the selected date range and type.", Toast.LENGTH_SHORT).show();
                    } else {
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
