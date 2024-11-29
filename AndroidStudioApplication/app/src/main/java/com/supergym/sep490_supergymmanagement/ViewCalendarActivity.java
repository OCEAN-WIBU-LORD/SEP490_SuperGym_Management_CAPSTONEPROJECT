package com.supergym.sep490_supergymmanagement;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.supergym.sep490_supergymmanagement.apiadapter.ApiService.ApiService;
import com.supergym.sep490_supergymmanagement.apiadapter.RetrofitClient;
import com.supergym.sep490_supergymmanagement.models.CheckInDatesResponse;
import com.supergym.sep490_supergymmanagement.models.Schedule2;
import com.supergym.sep490_supergymmanagement.models.ScheduleForTrainer;
import com.supergym.sep490_supergymmanagement.models.TimeSlot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewCalendarActivity extends Fragment {

    private CompactCalendarView compactCalendarView;
    private TextView selectedDateInfo;
    private LinearLayout notesContainer;
    private String userId;
    private String role;

    private ApiService apiService;

    // Lưu danh sách TimeSlot vào Map để tối ưu hóa
    private HashMap<String, String> timeSlotMap = new HashMap<>();
    private List<String> highlightedDates = new ArrayList<>();
    private Button btnPreviousMonth;
    private Button btnNextMonth;
    private TextView monthYearTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_view_calendar, container, false);

        compactCalendarView = view.findViewById(R.id.compactcalendar_view);
        compactCalendarView.setFirstDayOfWeek(java.util.Calendar.MONDAY);
        selectedDateInfo = view.findViewById(R.id.selectedDateInfo);
        notesContainer = view.findViewById(R.id.notesContainer);

        btnPreviousMonth = view.findViewById(R.id.btnPreviousMonth);
        btnNextMonth = view.findViewById(R.id.btnNextMonth);
        monthYearTextView = view.findViewById(R.id.monthYearTextView);

        apiService = RetrofitClient.getApiService(requireContext());

        // Kiểm tra người dùng đăng nhập
        checkUser();

        // Thiết lập tiêu đề tháng năm ban đầu
        updateMonthYearDisplay();

        // Xử lý sự kiện nút "Tháng trước"
        btnPreviousMonth.setOnClickListener(v -> {
            compactCalendarView.scrollLeft();
        });

        // Xử lý sự kiện nút "Tháng sau"
        btnNextMonth.setOnClickListener(v -> {
            compactCalendarView.scrollRight();
        });

        // Xử lý chọn ngày trên lịch và cập nhật tháng năm khi cuộn
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                String selectedDate = new SimpleDateFormat("yyyy-MM-dd").format(dateClicked);
                String displayText = "Appointments for " + formatDateForDisplay(selectedDate);
                selectedDateInfo.setText(displayText); // Hiển thị "Appointments for [date]"
                selectedDateInfo.setTypeface(null, Typeface.BOLD); // In đậm văn bản
                loadAppointmentsForSelectedDate(selectedDate);
            }


            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                updateMonthYearDisplay();
            }
        });

        return view;
    }

    private void updateMonthYearDisplay() {
        Date currentDate = compactCalendarView.getFirstDayOfCurrentMonth();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", new Locale("en"));
        String monthYear = dateFormat.format(currentDate);
        monthYearTextView.setText(monthYear);
    }

    private void checkUser() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            Log.d("ViewCalendarActivity", "User logged in with userId: " + userId);

            FirebaseDatabase.getInstance().getReference("users")
                    .child(userId)
                    .child("roleId")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            String roleId = task.getResult().getValue(String.class);
                            Log.d("ViewCalendarActivity", "Fetched roleId: " + roleId);

                            if ("-O7sA4aJbpHG6gZeX13p".equals(roleId)) {
                                role = "trainer";
                            } else if ("-O7s8sU2ZMyRWjrImzCO".equals(roleId)) {
                                role = "customer";
                            } else {
                                role = "unknown";
                                Toast.makeText(getActivity(), "Unknown role. Access denied.", Toast.LENGTH_SHORT).show();
                                getActivity().finish();
                            }

                            fetchTimeSlots();
                        } else {
                            Log.e("ViewCalendarActivity", "Failed to fetch roleId for userId: " + userId);
                            Toast.makeText(getActivity(), "Failed to determine role. Please try again.", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        }
                    });
        } else {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
    }

    private void fetchTimeSlots() {
        apiService.getTimeSlots().enqueue(new Callback<List<TimeSlot>>() {
            @Override
            public void onResponse(Call<List<TimeSlot>> call, Response<List<TimeSlot>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (TimeSlot timeSlot : response.body()) {
                        timeSlotMap.put(timeSlot.getTime(), timeSlot.getTimeSlotId());
                    }
                    fetchHighlightedDates();
                } else {
                    Toast.makeText(getActivity(), "Failed to fetch TimeSlots", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TimeSlot>> call, Throwable t) {
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchHighlightedDates() {
        new Thread(() -> {
            try {
                // Lấy lịch tập của customer hoặc trainer
                if ("customer".equals(role)) {
                    Response<List<Schedule2>> response = apiService.getCustomerSchedules(userId).execute();
                    if (response.isSuccessful() && response.body() != null) {
                        for (Schedule2 schedule : response.body()) {
                            // Highlight màu trắng cho các ngày có lịch tập
                            highlightDate(schedule.getDate(), Color.WHITE);
                        }
                    }
                } else if ("trainer".equals(role)) {
                    Response<List<ScheduleForTrainer>> response = apiService.getTrainerSchedules(userId).execute();
                    if (response.isSuccessful() && response.body() != null) {
                        for (ScheduleForTrainer schedule : response.body()) {
                            // Highlight màu trắng cho các ngày có lịch tập
                            highlightDate(schedule.getDate(), Color.WHITE);
                        }
                    }
                }

                // Sau khi đã highlight lịch tập, tiếp tục gọi hàm để lấy check-in dates cho khách phổ thông
                fetchCheckInDates();

            } catch (Exception e) {
                Log.e("ViewCalendarActivity", "Error fetching highlighted dates: " + e.getMessage());
                getActivity().runOnUiThread(() ->
                        Toast.makeText(getActivity(), "Error fetching highlighted dates.", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    private void fetchCheckInDates() {
        new Thread(() -> {
            try {
                // Gọi API để lấy ngày check-in
                Response<CheckInDatesResponse> response = apiService.getCheckInDates(userId).execute();
                if (response.isSuccessful() && response.body() != null) {
                    List<String> checkInDates = response.body().getCheckInDates(); // Lấy danh sách ngày check-in
                    if (checkInDates != null && !checkInDates.isEmpty()) {
                        // Log ra các ngày check-in
                        for (String date : checkInDates) {
                            Log.d("CheckInDates", "Check-in date: " + date);
                            // Highlight màu xanh cho các ngày check-in
                            highlightDate(date, Color.GREEN);
                        }
                    } else {
                        Log.d("CheckInDates", "No check-in dates found.");
                    }
                } else {
                    Log.e("CheckInDates", "Failed to fetch check-in dates. Response body is null.");
                }
            } catch (Exception e) {
                Log.e("ViewCalendarActivity", "Error fetching check-in dates: " + e.getMessage());
                getActivity().runOnUiThread(() ->
                        Toast.makeText(getActivity(), "Error fetching check-in dates.", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    private void highlightDate(String date, int color) {
        // Hàm highlight cho ngày
        try {
            Date eventDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            Event event = new Event(color, eventDate.getTime(), "Highlighted");
            compactCalendarView.addEvent(event);
        } catch (Exception e) {
            Log.e("ViewCalendarActivity", "Error parsing date for highlight: " + e.getMessage());
        }
    }

    private void highlightCheckInDatesOnCalendar() {
        // Chỉ cần gọi hàm này sau khi đã highlight các ngày check-in
        compactCalendarView.invalidate();
    }



    private void loadAppointmentsForSelectedDate(String selectedDate) {
        Log.d("ViewCalendarActivity", "Loading appointments for userId: " + userId + " on date: " + selectedDate);

        if ("customer".equals(role)) {
            apiService.getCustomerSchedules(userId).enqueue(new Callback<List<Schedule2>>() {
                @Override
                public void onResponse(Call<List<Schedule2>> call, Response<List<Schedule2>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        notesContainer.removeAllViews();
                        boolean hasAppointments = false; // Thêm biến kiểm tra

                        for (Schedule2 schedule : response.body()) {
                            if (selectedDate.equals(schedule.getDate())) {
                                displayCustomerAppointment(schedule);
                                hasAppointments = true; // Có lịch tập
                            }
                        }

                        if (!hasAppointments) {
                            // Không có lịch tập vào ngày này
                            TextView noAppointmentsView = new TextView(getActivity());
                            noAppointmentsView.setText("No appointments on this day");
                            noAppointmentsView.setTextSize(16);
                            noAppointmentsView.setTextColor(Color.BLACK);
                            notesContainer.addView(noAppointmentsView);
                        }

                    } else {
                        Toast.makeText(getActivity(), "Failed to fetch appointments for date.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Schedule2>> call, Throwable t) {
                    Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else if ("trainer".equals(role)) {
            apiService.getTrainerSchedules(userId).enqueue(new Callback<List<ScheduleForTrainer>>() {
                @Override
                public void onResponse(Call<List<ScheduleForTrainer>> call, Response<List<ScheduleForTrainer>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        notesContainer.removeAllViews();
                        boolean hasAppointments = false; // Thêm biến kiểm tra

                        for (ScheduleForTrainer schedule : response.body()) {
                            if (selectedDate.equals(schedule.getDate())) {
                                displayTrainerAppointment(schedule);
                                hasAppointments = true; // Có lịch tập
                            }
                        }

                        if (!hasAppointments) {
                            // Không có lịch tập vào ngày này
                            TextView noAppointmentsView = new TextView(getActivity());
                            noAppointmentsView.setText("No appointments on this day");
                            noAppointmentsView.setTextSize(16);
                            noAppointmentsView.setTextColor(Color.BLACK);
                            notesContainer.addView(noAppointmentsView);
                        }

                    } else {
                        Toast.makeText(getActivity(), "Failed to fetch appointments for date.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<ScheduleForTrainer>> call, Throwable t) {
                    Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void displayCustomerAppointment(Schedule2 schedule) {
        addAppointmentsToView(schedule.getDate(), schedule.getTimeSlot(), schedule.getTrainerName(), null);
    }

    private void displayTrainerAppointment(ScheduleForTrainer schedule) {
        addAppointmentsToView(schedule.getDate(), schedule.getTimeSlot(), null, schedule.getCustomers());
    }

    private void addAppointmentsToView(String date, String timeSlot, String trainerName, List<String> customers) {
        LinearLayout appointmentView = new LinearLayout(getActivity());
        appointmentView.setOrientation(LinearLayout.VERTICAL);
        appointmentView.setPadding(8, 8, 8, 8);

        TextView timeView = new TextView(getActivity());
        timeView.setText(timeSlot);
        timeView.setTextSize(16);
        timeView.setTypeface(null, android.graphics.Typeface.BOLD);
        timeView.setTextColor(getResources().getColor(android.R.color.black));
        appointmentView.addView(timeView);

        if (trainerName != null) {
            TextView trainerView = new TextView(getActivity());
            trainerView.setText("Practice with trainer: " + trainerName);
            trainerView.setTypeface(null, android.graphics.Typeface.BOLD);
            trainerView.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
            appointmentView.addView(trainerView);
        }

        if (customers != null && !customers.isEmpty()) {
            TextView customerView = new TextView(getActivity());
            customerView.setText("Practice with customers: " + String.join(", ", customers));
            customerView.setTypeface(null, android.graphics.Typeface.BOLD);
            customerView.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
            appointmentView.addView(customerView);
        }

        notesContainer.addView(appointmentView);
    }


    private String formatDateForDisplay(String date) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date parsedDate = inputFormat.parse(date);
            return outputFormat.format(parsedDate);
        } catch (Exception e) {
            return date;
        }
    }
}
