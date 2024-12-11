package com.supergym.sep490_supergymmanagement;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.database.*;

import java.text.*;
import java.util.*;

public class ViewIncomeFragment extends Fragment {

    private BarChart incomeBarChart;
    private Button startDateButton, endDateButton;
    private TextView totalIncomeTextView;
    private Spinner campusFilterSpinner, filterTypeSpinner;
    private ImageView btnDropDownCampus, btnDropDownType;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private Calendar calendar = Calendar.getInstance();
    private DatabaseReference incomeRef;

    private Date startDate, endDate;
    private String selectedCampus = "All campuses";
    private String selectedType = "All types";
    private DatabaseReference paymentsRef;
    private long startDateMillis = 0; // Default: No filter
    private long endDateMillis = Long.MAX_VALUE; // Default: No filter
    ArrayList barArraylist = new ArrayList();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_view_income, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
// Initialize Firebase Reference
        paymentsRef = FirebaseDatabase.getInstance().getReference("Payments");
        initializeUI(view);
        setupSpinners();
        setupDatePickers();
    }

    private void initializeUI(View view) {
        incomeBarChart = view.findViewById(R.id.incomeBarChart);
        startDateButton = view.findViewById(R.id.startDateButton);
        endDateButton = view.findViewById(R.id.endDateButton);
        totalIncomeTextView = view.findViewById(R.id.totalIncome);
     //   campusFilterSpinner = view.findViewById(R.id.campusFilterSpinner);
        filterTypeSpinner = view.findViewById(R.id.FilterTypeSpinner);
        btnDropDownCampus = view.findViewById(R.id.btnDropDown);
       // btnDropDownType = view.findViewById(R.id.btnDropDown2);

        incomeRef = FirebaseDatabase.getInstance().getReference("Income");
        // Cấu hình XAxis của BarChart
        XAxis xAxis = incomeBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(5, true);  // Giới hạn số lượng nhãn trên trục X
        xAxis.setGranularity(1f);  // Đảm bảo các nhãn không bị trùng nhau

        // Cấu hình thêm các thuộc tính khác cho BarChart nếu cần
        incomeBarChart.setFitBars(true);  // Đảm bảo các cột bar không bị cắt
        incomeBarChart.getDescription().setEnabled(false);  // Tắt mô tả mặc định
     //   btnDropDownCampus.setOnClickListener(v -> campusFilterSpinner.performClick());
       // btnDropDownType.setOnClickListener(v -> filterTypeSpinner.performClick());
        //fetchAndDisplayData();
        fetchAndCalculateMonthlyIncome();
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> campusAdapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.campus_filter_options,
                android.R.layout.simple_spinner_item
        );
        campusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      //  campusFilterSpinner.setAdapter(campusAdapter);

        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.type_filter_options,
                R.layout.spinner_item_multiline
        );
        typeAdapter.setDropDownViewResource(R.layout.spinner_item_multiline);
        filterTypeSpinner.setAdapter(typeAdapter);

     //   campusFilterSpinner.setOnItemSelectedListener(createSpinnerListener());
        filterTypeSpinner.setOnItemSelectedListener(createSpinnerListener());
    }

    private AdapterView.OnItemSelectedListener createSpinnerListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               /* if (parent == campusFilterSpinner) {
                    selectedCampus = campusFilterSpinner.getSelectedItem().toString();
                } else */

                    if (parent == filterTypeSpinner) {
                    selectedType = filterTypeSpinner.getSelectedItem().toString();
                }
                if (startDate != null && endDate != null) {
                    fetchAndDisplayData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
    }

    private void setupDatePickers() {
        startDateButton.setOnClickListener(v -> showDatePickerDialog(true));
        endDateButton.setOnClickListener(v -> showDatePickerDialog(false));
    }

    private void showDatePickerDialog(boolean isStartDate) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    calendar.set(year, monthOfYear, dayOfMonth);
                    String selectedDate = dateFormat.format(calendar.getTime());

                    try {
                        if (isStartDate) {
                            startDateButton.setText("Start Date: " + selectedDate);
                            startDate = dateFormat.parse(selectedDate);
                        } else {
                            endDateButton.setText("End Date: " + selectedDate);
                            endDate = dateFormat.parse(selectedDate);
                        }
                        if (startDate != null && endDate != null) {
                       //     fetchAndDisplayIncomeData();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }



    private void fetchAndDisplayData() {
        paymentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<BarEntry> entries = new ArrayList<>();

                for (DataSnapshot paymentSnapshot : snapshot.getChildren()) {
                    try {
                        // Get payment details
                        String paymentDateStr = paymentSnapshot.child("paymentDate").getValue(String.class);
                        Long amount = paymentSnapshot.child("amount").getValue(Long.class);

                        // Parse date
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                        Date paymentDate = sdf.parse(paymentDateStr);
                        long paymentDateMillis = paymentDate.getTime();

                        // Apply date filtering
                        if (paymentDateMillis >= startDateMillis && paymentDateMillis <= endDateMillis && amount != null) {
                            entries.add(new BarEntry(paymentDateMillis, amount));
                        }
                    } catch (Exception e) {
                        Log.e("IncomeFragment", "Error parsing data: " + e.getMessage());
                    }
                }

                // Sort entries by date
                Collections.sort(entries, new Comparator<BarEntry>() {
                    @Override
                    public int compare(BarEntry o1, BarEntry o2) {
                        return Float.compare(o1.getX(), o2.getX());
                    }
                });
                getData();
                // Set data to BarChart
                BarDataSet dataSet = new BarDataSet(barArraylist, "Income Over Time");
                dataSet.setColor(getResources().getColor(android.R.color.holo_blue_light));
                dataSet.setValueTextColor(getResources().getColor(android.R.color.black));

                BarData barData = new BarData(dataSet);
                barData.setBarWidth(0.9f); // Set bar width

                BarChart incomeBarChart = (BarChart) getView().findViewById(R.id.incomeBarChart);
                incomeBarChart.setData(barData);

                // Customize BarChart
                XAxis xAxis = incomeBarChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setGranularity(1f);
                xAxis.setValueFormatter(new DateValueFormatter());

                incomeBarChart.getDescription().setText("Income Trend");
                incomeBarChart.setFitBars(true); // Make bars fit in the chart
                incomeBarChart.invalidate(); // Refresh chart
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("IncomeFragment", "Database error: " + error.getMessage());
            }
        });
    }
    private void getData (){
        barArraylist.add (new BarEntry ( 9f, 80));
        barArraylist.add (new BarEntry(2f,  10));
        barArraylist.add (new BarEntry (3f, 20));
        barArraylist.add (new BarEntry( 4f, 30));
        barArraylist.add (new BarEntry ( 5f,40));
        barArraylist.add (new BarEntry ( 6f, 50));
        barArraylist.add (new BarEntry( 7f, 60));
        barArraylist.add (new BarEntry ( 8f,70));
        barArraylist.add (new BarEntry ( 9f, 80));
        barArraylist.add (new BarEntry ( 10f,70));
        barArraylist.add (new BarEntry ( 11f, 80));
        barArraylist.add (new BarEntry ( 12f,70));

    }

    private interface DatePickerCallback {
        void onDateSelected(String date, long millis);
    }

    private void fetchAndCalculateMonthlyIncome() {
        paymentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Calculate totals and month names using only completed payments
                Pair<ArrayList<Double>, ArrayList<String>> result = calculateMonthlyTotalIncome(snapshot);
                ArrayList<Double> totalIncomeList = result.first;
                ArrayList<String> monthNamesList = result.second;

                // Display the data on the chart
                displayMonthlyIncomeOnChart(totalIncomeList, monthNamesList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("IncomeFragment", "Database error: " + error.getMessage());
            }
        });
    }


    private Pair<ArrayList<Double>, ArrayList<String>> calculateMonthlyTotalIncome(DataSnapshot snapshot) {
        // Map to store total income by month ("yyyy-MM" format)
        Map<String, Double> monthlyIncomeMap = new HashMap<>();
        Map<String, String> monthNamesMap = new HashMap<>(); // Store month names for X-axis

        for (DataSnapshot paymentSnapshot : snapshot.getChildren()) {
            try {
                // Get payment details
                String paymentDateStr = paymentSnapshot.child("paymentDate").getValue(String.class);
                Long amount = paymentSnapshot.child("amount").getValue(Long.class);
                String paymentStatus = paymentSnapshot.child("paymentStatus").getValue(String.class);

                // Only process payments with "Completed" status
                if (paymentStatus != null && paymentStatus.equals("Completed")) {
                    // Parse the payment date
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                    Date paymentDate = sdf.parse(paymentDateStr);

                    // Extract month and year as the key (e.g., "2024-12" for December 2024)
                    SimpleDateFormat monthYearFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
                    String monthYear = monthYearFormat.format(paymentDate);

                    // Store month name for the X-axis
                    SimpleDateFormat monthNameFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
                    String monthName = monthNameFormat.format(paymentDate);
                    monthNamesMap.put(monthYear, monthName);

                    // Accumulate the total income for the month
                    if (amount != null) {
                        if (monthlyIncomeMap.containsKey(monthYear)) {
                            double currentTotal = monthlyIncomeMap.get(monthYear);
                            monthlyIncomeMap.put(monthYear, currentTotal + amount);
                        } else {
                            monthlyIncomeMap.put(monthYear, amount.doubleValue());
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("IncomeFragment", "Error parsing data: " + e.getMessage());
            }
        }

        // Convert maps to ArrayLists for charting
        ArrayList<Double> totalIncomeList = new ArrayList<>(monthlyIncomeMap.values());
        ArrayList<String> monthNamesList = new ArrayList<>(monthNamesMap.values());

        // Log the monthly totals for debugging
        Log.d("Monthly Income", "Monthly Totals: " + totalIncomeList);
        Log.d("Month Names", "Month Names: " + monthNamesList);

        // Return both the totals and month names
        return new Pair<>(totalIncomeList, monthNamesList);
    }

    private void displayMonthlyIncomeOnChart(ArrayList<Double> totalIncomeList, ArrayList<String> monthNamesList) {
        // Create BarEntries for the BarChart
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < totalIncomeList.size(); i++) {
            entries.add(new BarEntry(i, totalIncomeList.get(i).floatValue())); // Use the index as the x-value
        }

        // Create a BarDataSet
        BarDataSet dataSet = new BarDataSet(entries, "Income Over Time");
        dataSet.setColor(getResources().getColor(android.R.color.holo_blue_light));
        dataSet.setValueTextColor(getResources().getColor(android.R.color.black));

        // Create BarData
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f); // Set bar width

        // Set data to BarChart
        incomeBarChart.setData(barData);

        // Customize X-Axis to show month names
        XAxis xAxis = incomeBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(monthNamesList)); // Set the formatted month names

        // Final chart customizations
        incomeBarChart.getDescription().setText("Monthly Income Trend");
        incomeBarChart.setFitBars(true); // Fit bars within chart
        incomeBarChart.invalidate(); // Refresh the chart
    }


    private ArrayList<String> getMonthsList(int size) {
        // You can replace this with actual month names if you have a custom list
        ArrayList<String> months = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            months.add("Month " + (i + 1));  // For simplicity, using "Month 1", "Month 2", etc.
        }
        return months;
    }

/*

    private void fetchAndDisplayIncomeData() {
        if (selectedCampus.equals("All campuses")) {
            fetchIncomeForAllCampuses();
        } else {
            fetchIncomeForSpecificCampus();
        }
    }

    private void fetchIncomeForAllCampuses() {
        incomeRef.addListenerForSingleValueEvent(createIncomeListener());
    }

    private void fetchIncomeForSpecificCampus() {
        incomeRef.child(selectedCampus).addListenerForSingleValueEvent(createIncomeListener());
    }

    private ValueEventListener createIncomeListener() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<IncomeData> incomeDataList = new ArrayList<>();
                float totalIncome = 0f;

                for (DataSnapshot campusSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot dateSnapshot : campusSnapshot.getChildren()) {
                        processIncomeSnapshot(dateSnapshot, incomeDataList);
                    }
                }

                if (incomeDataList.isEmpty()) {
                    Toast.makeText(getContext(), "No income data available for the selected filters.", Toast.LENGTH_SHORT).show();
                } else {
                    displayTotalIncome(totalIncome);
                    drawIncomeChart(incomeDataList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to retrieve income data.", Toast.LENGTH_SHORT).show();
            }

            private void processIncomeSnapshot(DataSnapshot dateSnapshot, List<IncomeData> incomeDataList) {
                String dateStr = dateSnapshot.getKey();
                Float income = dateSnapshot.child("totalIncome").getValue(Float.class);
                String type = dateSnapshot.child("typeIncome").getValue(String.class);

                if (income == null) income = 0f;
                if (type == null) type = "";

                try {
                    Date incomeDate = dateFormat.parse(dateStr);
                    if (incomeDate != null && !incomeDate.before(startDate) && !incomeDate.after(endDate) &&
                            (selectedType.equals("All types") || selectedType.equals(type))) {
                        incomeDataList.add(new IncomeData(incomeDate, income));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void drawIncomeChart(List<IncomeData> incomeDataList) {
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < incomeDataList.size(); i++) {
            IncomeData data = incomeDataList.get(i);
            entries.add(new BarEntry(i, data.getIncome()));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Income Over Time");
        dataSet.setColor(getResources().getColor(R.color.teal_200));
        dataSet.setValueTextColor(getResources().getColor(R.color.black));
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        incomeBarChart.setData(barData);
        barData.setBarWidth(1.5f); // Test with a larger value

        XAxis xAxis = incomeBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(getDateLabels(incomeDataList)));

        incomeBarChart.getAxisLeft().setGranularity(1f);
        incomeBarChart.getAxisRight().setEnabled(false);
        incomeBarChart.getDescription().setEnabled(false);
        incomeBarChart.setFitBars(true);
        incomeBarChart.invalidate();
    }

    private List<String> getDateLabels(List<IncomeData> incomeDataList) {
        List<String> labels = new ArrayList<>();
        for (IncomeData data : incomeDataList) {
            labels.add(dateFormat.format(data.getDate()));
        }
        return labels;
    }


    private void displayTotalIncome(float totalIncome) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        totalIncomeTextView.setText(currencyFormat.format(totalIncome));
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
    }*/

    private class DateValueFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            return dateFormat.format(new Date((long) value));
        }
    }
}
