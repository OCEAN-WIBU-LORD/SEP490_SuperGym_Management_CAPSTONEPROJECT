package com.supergym.sep490_supergymmanagement.views.appointment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.supergym.sep490_supergymmanagement.R;
import com.supergym.sep490_supergymmanagement.ScheduleActivity;
import com.supergym.sep490_supergymmanagement.adapters.DayAdapter;
import com.supergym.sep490_supergymmanagement.adapters.TimeAdapter;
import com.supergym.sep490_supergymmanagement.models.Appointment;
import com.supergym.sep490_supergymmanagement.models.Day;
import com.supergym.sep490_supergymmanagement.models.TrainingLession;
import com.supergym.sep490_supergymmanagement.models.Trainer;
import com.supergym.sep490_supergymmanagement.repositories.AppointmentResp;
import com.supergym.sep490_supergymmanagement.repositories.TrainingLessionResp;
import com.supergym.sep490_supergymmanagement.repositories.callbacks.Callback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class UpdateScheduleActivity extends AppCompatActivity implements DayAdapter.OnAddDayClickListener {

    private Spinner spinnerMonths;
    private RecyclerView rcv;
    private RecyclerView rcvTime;
    private ArrayList<Day> days = new ArrayList<>();
    private List<String> times = Arrays.asList("10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00"); // Thời gian mẫu
    private ArrayList<Appointment> appointments;
    private DayAdapter adapter;
    private TimeAdapter timeAdapter;
    private int selectedMonth;
    private Button btnUpdateAppointment;
    private TextView txtNote;
    private TextView txtDoctorName;
    private TextView txtDoctorInfo;
    private ImageView imageView;
    private TextView txtSpec;
    private List<String> diseaseNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_schedule);

        // Khởi tạo các view
        txtDoctorName = findViewById(R.id.doctorNameAppointment);
        txtDoctorInfo = findViewById(R.id.doctorInforAppointment);
        txtNote = findViewById(R.id.txtNote);
        spinnerMonths = findViewById(R.id.monthSpinner);
        rcv = findViewById(R.id.rcvDay);
        rcvTime = findViewById(R.id.rcvTime);
        btnUpdateAppointment = findViewById(R.id.btnUpdateAppointment);
        imageView = findViewById(R.id.imageView);
        txtSpec = findViewById(R.id.txtSpe);
        diseaseNames = new ArrayList<>();

        // Khởi tạo RecyclerView
        initRecyclerView();
        initTimeRecyclerView();

        // Lấy chi tiết cuộc hẹn từ Intent
        String doctorName = getIntent().getStringExtra("name");
        String doctorBio = getIntent().getStringExtra("bio");
        String doctorId = getIntent().getStringExtra("doctorId");
        String appointmentNote = getIntent().getStringExtra("note");
        long startTimeMillis = getIntent().getLongExtra("startTime", 0);
        String appointmentId = getIntent().getStringExtra("appointmentId");

        // Đặt giá trị cho các view
        txtDoctorName.setText(doctorName);
        txtDoctorInfo.setText(doctorBio);
        txtNote.setText(appointmentNote);

        // Xử lý startTime để lấy các thành phần ngày và giờ
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTimeMillis);

        // Đặt tháng trong spinner
        String[] months = new DateFormatSymbols().getMonths();
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonths.setAdapter(monthAdapter);
        int monthIndex = calendar.get(Calendar.MONTH);
        spinnerMonths.setSelection(monthIndex);

        // Tạo danh sách ngày của tháng và đặt adapter
        days = generateDaysOfMonth(calendar);
        adapter = new DayAdapter(this, days, this);
        rcv.setAdapter(adapter);

        // Thiết lập RecyclerView cho thời gian
        timeAdapter = new TimeAdapter(this, times);
        rcvTime.setAdapter(timeAdapter);

        Trainer selectedDoctor = (Trainer) getIntent().getSerializableExtra("selectedDoctor");
        if (selectedDoctor != null) {
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

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateScheduleActivity.this, ScheduleActivity.class);
                startActivity(intent);
            }
        });

        btnUpdateAppointment.setOnClickListener(new View.OnClickListener() {
            AppointmentResp appointmentResp = new AppointmentResp();

            @Override
            public void onClick(View v) {
                // Lấy ngày và giờ đã chọn
                int selectedDayPosition = adapter.getSelectedDayPosition();
                if (selectedDayPosition != RecyclerView.NO_POSITION) {
                    Day selectedDay = days.get(selectedDayPosition);
                    String selectedDayText = selectedDay.getDay();

                    int selectedTimePosition = timeAdapter.getSelectedTimePosition();
                    if (selectedTimePosition != RecyclerView.NO_POSITION) {
                        String selectedTime = times.get(selectedTimePosition);

                        // Kết hợp ngày và giờ thành đối tượng Calendar
                        try {
                            int year = Calendar.getInstance().get(Calendar.YEAR);
                            int month = spinnerMonths.getSelectedItemPosition();
                            int dayOfMonth = Integer.parseInt(selectedDayText);

                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.YEAR, year);
                            calendar.set(Calendar.MONTH, month);
                            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                            // Tách giờ và phút từ selectedTime
                            String[] timeParts = selectedTime.split(":");
                            int hour = Integer.parseInt(timeParts[0]);
                            int minute = Integer.parseInt(timeParts[1]);

                            calendar.set(Calendar.HOUR_OF_DAY, hour);
                            calendar.set(Calendar.MINUTE, minute);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);

                            long startTimeSeconds = calendar.getTimeInMillis() / 1000;
                            com.google.firebase.Timestamp startTime = new com.google.firebase.Timestamp(startTimeSeconds, 0);

                            long endTimeSeconds = startTime.getSeconds() + 3600;
                            com.google.firebase.Timestamp endTime = new com.google.firebase.Timestamp(endTimeSeconds, 0);

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            Appointment appointment = new Appointment();
                            appointment.setAppointmentId(appointmentId);
                            appointment.setTrainingLessionId("");
                            appointment.setNote(txtNote.getText().toString());
                            appointment.setRecordId("");
                            appointment.setDoctorId(doctorId);
                            appointment.setStartTime(startTime);
                            appointment.setEndTime(endTime);
                            appointment.setStatus("unconfirmed");
                            appointment.setUserId(user.getUid());

                            appointmentResp.updateAppointment(appointment, new Callback<Appointment>() {
                                @Override
                                public void onCallback(List<Appointment> objects) {
                                    // Xử lý callback nếu cần thiết
                                }
                            });

                            // Hiển thị thông báo thành công
                            Toast.makeText(UpdateScheduleActivity.this, "Appointment updated successfully", Toast.LENGTH_SHORT).show();

                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            Toast.makeText(UpdateScheduleActivity.this, "Error parsing date/time", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(UpdateScheduleActivity.this, "Please select a time", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UpdateScheduleActivity.this, "Please select a day", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateDiseaseTextView() {
        // Join disease names with 5 spaces and set to TextView
        String diseaseText = TextUtils.join("     ", diseaseNames);
        txtSpec.setText(diseaseText);
    }

    private ArrayList<Day> generateDaysOfMonth(Calendar calendar) {
        ArrayList<Day> days = new ArrayList<>();
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        int numDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 0; i < numDaysInMonth; i++) {
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            String dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
            String shortDayOfWeek = dayOfWeek.substring(0, 2);
            days.add(new Day(String.valueOf(day), shortDayOfWeek));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return days;
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
}
