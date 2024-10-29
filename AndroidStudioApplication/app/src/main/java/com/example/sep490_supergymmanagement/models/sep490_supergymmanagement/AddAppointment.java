package com.example.sep490_supergymmanagement.models.sep490_supergymmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sep490_supergymmanagement.R;
import com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.adapters.DayAdapter;
import com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.adapters.TimeAdapter;
import com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.models.Appointment;
import com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.models.Day;
import com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.models.Trainer;
import com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.models.TrainingLession;
import com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.repositories.AppointmentResp;
import com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.repositories.TrainingLessionResp;
import com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.repositories.callbacks.Callback;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddAppointment extends AppCompatActivity implements DayAdapter.OnAddDayClickListener {

    private Spinner spinnerMonths;
    private RecyclerView rcv;
    private RecyclerView rcvTime;
    private ArrayList<Day> days;
    private List<String> times;
    private ArrayList<Appointment> appointments;
    private DayAdapter adapter;
    private TimeAdapter timeAdapter;
    private int selectedMonth;
    private Button btnBookAppointment;
    private TextView txtNote;
    private TextView doctorNameAppointment;
    private TextView doctorInforAppointment;
    private TextView txtDisease;
    private List<String> diseaseNames;
    private ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_appointment);

        spinnerMonths = findViewById(R.id.monthSpinner);
        rcv = findViewById(R.id.rcvDay);
        rcvTime = findViewById(R.id.rcvTime);
        btnBookAppointment = findViewById(R.id.btnBookAppointment);
        txtNote = findViewById(R.id.txtNote);
        doctorNameAppointment = findViewById(R.id.doctorNameAppointment);
        doctorInforAppointment = findViewById(R.id.doctorInforAppointment);
        txtDisease = findViewById(R.id.txtDisease);
        imgBack = findViewById(R.id.imageView);
        diseaseNames = new ArrayList<>();
        days = new ArrayList<>();
        assignData();
        initSpinner();
        initRecyclerView();
        initTimeRecyclerView();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Trainer selectedDoctor = (Trainer) getIntent().getSerializableExtra("selectedDoctor");
        if (selectedDoctor != null) {
            doctorNameAppointment.setText(selectedDoctor.getName());
            doctorInforAppointment.setText(selectedDoctor.getBio());
            TrainingLessionResp diseaseResp = new TrainingLessionResp();
            for (String diseaseId : selectedDoctor.getTrainingLessionIds()) {
                diseaseResp.getDiseaseById(diseaseId, new Callback<TrainingLession>() {
                    @Override
                    public void onCallback(List<TrainingLession> diseases) {
                        if (!diseases.isEmpty()) {
                            diseaseNames.add(diseases.get(0).getName());
                            updateDiseaseTextView();
                        }
                    }
                });
            }
        }

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddAppointment.this, ViewMainContent.class);
                startActivity(intent);
            }
        });

        btnBookAppointment.setOnClickListener(new View.OnClickListener() {
            AppointmentResp appointmentResp = new AppointmentResp();

            @Override
            public void onClick(View v) {
                // Lấy thông tin từ RecyclerView rcv
                int selectedDayPosition = adapter.getSelectedDayPosition(); // Giả sử bạn có method này trong DayAdapter
                if (selectedDayPosition != RecyclerView.NO_POSITION) {
                    Day selectedDay = days.get(selectedDayPosition);
                    String selectedDayText = selectedDay.getDay(); // Giả sử định dạng của ngày là "dd"

                    // Lấy thông tin từ RecyclerView rcvTime
                    int selectedTimePosition = timeAdapter.getSelectedTimePosition(); // Giả sử bạn có method này trong TimeAdapter
                    if (selectedTimePosition != RecyclerView.NO_POSITION) {
                        String selectedTime = times.get(selectedTimePosition); // Giả sử định dạng của giờ là "HH:mm"

                        // Kết hợp thông tin ngày và giờ thành một đối tượng Calendar
                        try {
                            int year = Calendar.getInstance().get(Calendar.YEAR);
                            int month = spinnerMonths.getSelectedItemPosition();
                            int dayOfMonth = Integer.parseInt(selectedDayText);

                            Calendar selectedCalendar = Calendar.getInstance();
                            selectedCalendar.set(Calendar.YEAR, year);
                            selectedCalendar.set(Calendar.MONTH, month);
                            selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                            // Tách giờ và phút từ selectedTime
                            String[] timeParts = selectedTime.split(":");
                            int hour = Integer.parseInt(timeParts[0]);
                            int minute = Integer.parseInt(timeParts[1]);

                            selectedCalendar.set(Calendar.HOUR_OF_DAY, hour);
                            selectedCalendar.set(Calendar.MINUTE, minute);
                            selectedCalendar.set(Calendar.SECOND, 0);
                            selectedCalendar.set(Calendar.MILLISECOND, 0);

                            Calendar currentCalendar = Calendar.getInstance();
                            if (selectedCalendar.before(currentCalendar)) {
                                Toast.makeText(AddAppointment.this, "Date not available", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            long startTimeSeconds = selectedCalendar.getTimeInMillis() / 1000;
                            Timestamp startTime = new Timestamp(startTimeSeconds, 0);

                            long endTimeSeconds = startTime.getSeconds() + 3600;
                            Timestamp endTime = new Timestamp(endTimeSeconds, 0);

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            Appointment appointment = new Appointment();
                            //appointment.setAppointmentId("appointment_id_3");
                            appointment.setTrainingLessionId("");
                            appointment.setNote(txtNote.getText().toString());
                            appointment.setRecordId("");
                            appointment.setDoctorId(selectedDoctor.getTrainerId());
                            appointment.setStartTime(startTime);
                            appointment.setEndTime(endTime);
                            appointment.setStatus("unconfirmed");
                            appointment.setUserId(user.getUid());

                            appointmentResp.createAppointment(appointment, new Callback<Appointment>() {
                                @Override
                                public void onCallback(List<Appointment> objects) {
                                    // Handle the callback if needed
                                }
                            });
                            Toast.makeText(AddAppointment.this, "Book appointment successfully", Toast.LENGTH_SHORT).show();
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            Toast.makeText(AddAppointment.this, "Error parsing date/time", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AddAppointment.this, "Please select a time", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddAppointment.this, "Please select a day", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void updateDiseaseTextView() {
        // Join disease names with 5 spaces and set to TextView
        String diseaseText = TextUtils.join("     ", diseaseNames);
        txtDisease.setText(diseaseText);
    }
    private void assignData() {
        times = new ArrayList<>(Arrays.asList("9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00"));
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
                selectedMonth = position;
                updateDaysOfMonth(selectedMonth);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initRecyclerView() {
        adapter = new DayAdapter(this, days, this);
        rcv.setAdapter(adapter);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false);
        rcv.setLayoutManager(layoutManager);
    }

    private void initTimeRecyclerView() {
        timeAdapter = new TimeAdapter(this, times);
        rcvTime.setAdapter(timeAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false);
        rcvTime.setLayoutManager(layoutManager);
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
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAddDayClick(int position, Day day) {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = spinnerMonths.getSelectedItemPosition();
        int dayOfMonth = Integer.parseInt(day.getDay());

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long timestamp = calendar.getTimeInMillis() / 1000;

        AppointmentResp appointmentResp = new AppointmentResp();
        appointmentResp.getAppointmentsByDoctorAndTime("doctor_id_1", timestamp, new Callback<Appointment>() {
            @Override
            public void onCallback(List<Appointment> appointmentList) {
                appointments = new ArrayList<>(appointmentList);

                // Tạo một danh sách mới để lưu trữ giờ không thể chọn
                List<String> disabledTimes = new ArrayList<>();

                // Lặp qua danh sách các cuộc hẹn
                for (Appointment appointment : appointments) {
                    // Lấy giờ bắt đầu từ startTime của cuộc hẹn
                    Calendar startCalendar = Calendar.getInstance();
                    startCalendar.setTimeInMillis(appointment.getStartTime().getSeconds() * 1000);
                    int startHour = startCalendar.get(Calendar.HOUR_OF_DAY);
                    int startMinute = startCalendar.get(Calendar.MINUTE);

                    // Chuyển giờ bắt đầu thành định dạng "HH:mm"
                    String startTime = String.format(Locale.getDefault(), "%02d:%02d", startHour, startMinute);

                    // Thêm vào danh sách giờ không thể chọn
                    disabledTimes.add(startTime);
                }

                // Cập nhật lại danh sách disabledTimes trong adapter
                timeAdapter.setDisabledTimes(disabledTimes);
            }
        });
    }
}
