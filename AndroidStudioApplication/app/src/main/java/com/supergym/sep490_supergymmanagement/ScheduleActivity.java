package com.supergym.sep490_supergymmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.supergym.sep490_supergymmanagement.adapters.AllAppointmentAdapter;
import com.supergym.sep490_supergymmanagement.adapters.AppointmentTodayAdapter;
import com.supergym.sep490_supergymmanagement.adapters.DayAdapter;
import com.supergym.sep490_supergymmanagement.models.Appointment;
import com.supergym.sep490_supergymmanagement.models.Day;
import com.supergym.sep490_supergymmanagement.models.Trainer;
import com.supergym.sep490_supergymmanagement.repositories.AppointmentResp;
import com.supergym.sep490_supergymmanagement.repositories.TrainerResp;
import com.supergym.sep490_supergymmanagement.repositories.callbacks.Callback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public  class ScheduleActivity extends AppCompatActivity implements DayAdapter.OnDayClickListener {

    private Spinner spinnerMonths;
    private RecyclerView rcv;
    private RecyclerView rcvAppointmentToday;
    private RecyclerView rcvAllAppointment;
    private List<Day> days;
    ArrayList<Appointment> appointments;
    private List<Trainer> doctors;
    private DayAdapter adapter;
    private AppointmentTodayAdapter atAdapter;
    private AllAppointmentAdapter allAppointmentAdapter;
    private TextView currentDate;
    private TextView noAppointmentsText;
    private ImageView imgBack;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        spinnerMonths = findViewById(R.id.spinnerMonth);
        rcv = findViewById(R.id.recyclerView);
        rcvAppointmentToday = findViewById(R.id.rcvAppointmentToday);
        rcvAllAppointment = findViewById(R.id.rcvAllAppointment);
        currentDate = findViewById(R.id.currentDate);
        noAppointmentsText = findViewById(R.id.no_appointments_text);
        imgBack = findViewById(R.id.imageView);
        days = new ArrayList<>();
        appointments = new ArrayList<>();
        setCurrentDate();
        initSpinner();
        initRecyclerView();
        initRecyclerViewAppointmentToday();
        initRecyclerViewAllAppointment();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScheduleActivity.this, ViewMainContent.class);
                startActivity(intent);
            }
        });
    }

    private void initSpinner() {
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonths.setAdapter(adapter);
        spinnerMonths.setSelection(0);

        spinnerMonths.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMonth = parent.getItemAtPosition(position).toString();
                updateDaysOfMonth(position);
                Toast.makeText(ScheduleActivity.this, "Selected: " + selectedMonth, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initRecyclerView() {
        adapter = new DayAdapter(this, days, this::onDayClick);
        rcv.setAdapter(adapter);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false);
        rcv.setLayoutManager(layoutManager);
    }

    private void initRecyclerViewAppointmentToday() {
        loadFakeData();
    }

    private void initRecyclerViewAllAppointment() {
        loadFakeData();
    }

    private void setCurrentDate() {
        Date currentDateTime = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        String formattedDate = dateFormat.format(currentDateTime);
        currentDate.setText(formattedDate);
    }

    private void updateDaysOfMonth(int selectedMonth) {
        days.clear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, selectedMonth);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        int numDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 0; i < numDaysInMonth; i++) {
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            String dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
            String shortDayOfWeek = dayOfWeek.substring(0, 2);
            days.add(new Day(String.valueOf(day), shortDayOfWeek));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(ScheduleActivity.this, "Adapter chưa được khởi tạo", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadFakeData() {
        AppointmentResp appointmentResp = new AppointmentResp();
        TrainerResp doctorResp = new TrainerResp();

        // Load doctors first
        /*doctorResp.getDoctors(new Callback<Trainer>() {
            @Override
            public void onCallback(List<Trainer> doctorList) {
                doctors = new ArrayList<>(doctorList);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                // Now load appointments
                appointmentResp.getAppointments(user.getUid(),new Callback<Appointment>() {
                    @Override
                    public void onCallback(List<Appointment> appointmentList) {
                        appointments = new ArrayList<>(appointmentList);

                        // Initialize the adapters with both appointments and doctors
                        atAdapter = new AppointmentTodayAdapter(ScheduleActivity.this, appointments, doctors);
                        rcvAppointmentToday.setAdapter(atAdapter);
                        GridLayoutManager layoutManagerToday = new GridLayoutManager(ScheduleActivity.this, 1, GridLayoutManager.VERTICAL, false);
                        rcvAppointmentToday.setLayoutManager(layoutManagerToday);


                        GridLayoutManager layoutManagerAll = new GridLayoutManager(ScheduleActivity.this, 1, GridLayoutManager.VERTICAL, false);
                        rcvAllAppointment.setLayoutManager(layoutManagerAll);
                    }
                });
            }
        });*/
    }

    @Override
    public void onDayClick(int position, Day day) {
        // Gọi hàm để lấy dữ liệu từ Firebase dựa trên ngày được chọn
        loadAppointmentsForSelectedDay(day);
    }

    // Phương thức để tải các cuộc hẹn cho ngày được chọn
    private void loadAppointmentsForSelectedDay(Day day) {
        int selectedDay = Integer.parseInt(day.getDay());
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, selectedDay);

        int selectedMonth = spinnerMonths.getSelectedItemPosition();
        calendar.set(Calendar.MONTH, selectedMonth);
        int selectedYear = calendar.get(Calendar.YEAR);

        // Format ngày để so sánh với dữ liệu Firebase
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String selectedDate = dateFormat.format(calendar.getTime());

        List<Appointment> filteredAppointments = new ArrayList<>();

        for (Appointment appointment : appointments) {
            String appointmentDate = dateFormat.format(appointment.getStartTime().toDate());
            if (selectedDate.equals(appointmentDate)) {
                filteredAppointments.add(appointment);
            }
        }

        updateRecyclerViewWithAppointments(filteredAppointments);
    }

    private void updateRecyclerViewWithAppointments(List<Appointment> filteredAppointments) {
        if (filteredAppointments.isEmpty()) {
            noAppointmentsText.setVisibility(View.VISIBLE);
            rcvAppointmentToday.setVisibility(View.GONE);
        } else {
            noAppointmentsText.setVisibility(View.GONE);
            rcvAppointmentToday.setVisibility(View.VISIBLE);
            atAdapter = new AppointmentTodayAdapter(ScheduleActivity.this, filteredAppointments, doctors);
            rcvAppointmentToday.setAdapter(atAdapter);
            atAdapter.notifyDataSetChanged();
        }
    }
}
