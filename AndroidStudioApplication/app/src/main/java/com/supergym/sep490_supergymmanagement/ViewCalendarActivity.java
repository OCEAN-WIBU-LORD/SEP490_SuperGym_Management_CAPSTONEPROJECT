// ViewCalendarActivity.java
package com.supergym.sep490_supergymmanagement;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.supergym.sep490_supergymmanagement.apiadapter.ApiService.ApiService;
import com.supergym.sep490_supergymmanagement.apiadapter.RetrofitClient;
import com.supergym.sep490_supergymmanagement.models.Schedule2;
import com.supergym.sep490_supergymmanagement.models.ScheduleForTrainer;
import com.supergym.sep490_supergymmanagement.models.TimeSlot;
import com.supergym.sep490_supergymmanagement.views.appointment.CustomerAppointmentHandler;
import com.supergym.sep490_supergymmanagement.views.appointment.TrainerAppointmentHandler;

import java.text.ParseException;
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

    private HashMap<String, String> timeSlotMap = new HashMap<>();
    private List<String> highlightedDates = new ArrayList<>();
    private Button btnPreviousMonth;
    private Button btnNextMonth;
    private TextView monthYearTextView;

    private AppointmentHandler appointmentHandler;

    private final SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    private final SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);

    private static final String TAG = "ViewCalendarActivity";

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

        setupListeners();

        checkUser();

        return view;
    }

    /**
     * Sets up UI listeners for buttons and the calendar view.
     */
    private void setupListeners() {
        btnPreviousMonth.setOnClickListener(v -> {
            compactCalendarView.scrollLeft();  // Scroll to previous month
            updateMonthYearDisplay();
        });

        btnNextMonth.setOnClickListener(v -> {
            compactCalendarView.scrollRight();  // Scroll to next month
            updateMonthYearDisplay();
        });

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                String selectedDate = apiDateFormat.format(dateClicked);
                String displayText = "Appointments for " + formatDateForDisplay(selectedDate);
                selectedDateInfo.setText(displayText);
                selectedDateInfo.setTypeface(null, Typeface.BOLD);
                loadAppointmentsForSelectedDate(selectedDate);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                updateMonthYearDisplay();
            }
        });
    }

    /**
     * Checks if the user is authenticated and determines their role.
     */
    private void checkUser() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            Log.d(TAG, "User logged in with userId: " + userId);

            FirebaseDatabase.getInstance().getReference("users")
                    .child(userId)
                    .child("roleId")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            String roleId = task.getResult().getValue(String.class);
                            Log.d(TAG, "Fetched roleId: " + roleId);

                            switch (roleId) {
                                case "-O7sA4aJbpHG6gZeX13p":
                                    role = "trainer";
                                    appointmentHandler = new TrainerAppointmentHandler();
                                    break;
                                case "-O7s8sU2ZMyRWjrImzCO":
                                    role = "customer";
                                    appointmentHandler = new CustomerAppointmentHandler();
                                    break;
                                default:
                                    role = "unknown";
                                    Toast.makeText(getActivity(), "Unknown role. Access denied.", Toast.LENGTH_SHORT).show();
                                    getActivity().finish();
                                    return;
                            }

                            fetchTimeSlots();
                        } else {
                            Log.e(TAG, "Failed to fetch roleId for userId: " + userId);
                            Toast.makeText(getActivity(), "Failed to determine role. Please try again.", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        }
                    });
        } else {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
    }

    /**
     * Fetches available time slots from the backend API.
     */
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

    /**
     * Fetches and highlights dates that have appointments.
     */
    private void fetchHighlightedDates() {
        Date currentDate = compactCalendarView.getFirstDayOfCurrentMonth();
        String yearMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(currentDate);
        String[] parts = yearMonth.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);

        if (appointmentHandler == null) return;

        appointmentHandler.fetchSchedules(apiService, userId, year, month, new AppointmentHandler.ScheduleFetchCallback() {
            @Override
            public void onSuccess(List<?> schedules) {
                if (schedules.isEmpty()) {
                    // No appointments for this month
                    notesContainer.removeAllViews();
                    addNoAppointmentsView(notesContainer, "No appointments for this month");
                } else {
                    for (Object schedule : schedules) {
                        String date = null;
                        if (schedule instanceof Schedule2) {
                            date = ((Schedule2) schedule).getDate();
                        } else if (schedule instanceof ScheduleForTrainer) {
                            date = ((ScheduleForTrainer) schedule).getDate();
                        }
                        if (date != null && !highlightedDates.contains(date)) {
                            highlightDate(date, Color.WHITE);
                            highlightedDates.add(date);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "Error fetching highlighted dates: " + t.getMessage());
                Toast.makeText(getActivity(), "Error fetching highlighted dates.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Updates the month and year display based on the currently viewed month.
     */
    private void updateMonthYearDisplay() {
        Date currentDate = compactCalendarView.getFirstDayOfCurrentMonth();
        String monthYear = monthYearFormat.format(currentDate);
        monthYearTextView.setText(monthYear);

        String selectedDate = apiDateFormat.format(currentDate);
        selectedDateInfo.setText("Appointments for " + formatDateForDisplay(selectedDate));
        selectedDateInfo.setTypeface(null, Typeface.BOLD);
        loadAppointmentsForSelectedDate(selectedDate);
    }

    /**
     * Loads and displays appointments for the selected date.
     *
     * @param selectedDate The selected date in "yyyy-MM-dd" format.
     */
    private void loadAppointmentsForSelectedDate(String selectedDate) {
        Log.d(TAG, "Loading appointments for userId: " + userId + " on date: " + selectedDate);

        Date currentDate = compactCalendarView.getFirstDayOfCurrentMonth();
        String yearMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(currentDate);
        String[] parts = yearMonth.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);

        if (selectedDate == null || selectedDate.isEmpty()) {
            Log.e(TAG, "Selected date is invalid: " + selectedDate);
            return;
        }

        notesContainer.removeAllViews();

        if (appointmentHandler == null) return;

        appointmentHandler.fetchSchedules(apiService, userId, year, month, new AppointmentHandler.ScheduleFetchCallback() {
            @Override
            public void onSuccess(List<?> schedules) {
                List<Object> filteredSchedules = new ArrayList<>();
                for (Object schedule : schedules) {
                    String date = null;
                    if (schedule instanceof Schedule2) {
                        date = ((Schedule2) schedule).getDate();
                    } else if (schedule instanceof ScheduleForTrainer) {
                        date = ((ScheduleForTrainer) schedule).getDate();
                    }
                    if (selectedDate.equals(date)) {
                        filteredSchedules.add(schedule);
                    }
                }

                if (filteredSchedules.isEmpty()) {
                    addNoAppointmentsView(notesContainer, "No appointments on this day");
                } else {
                    appointmentHandler.displayAppointments(filteredSchedules, notesContainer);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "Error fetching schedules: " + t.getMessage());
                Toast.makeText(getActivity(), "Error fetching appointments.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Highlights a specific date on the calendar.
     *
     * @param date  The date in "yyyy-MM-dd" format.
     * @param color The color to highlight the date.
     */
    private void highlightDate(String date, int color) {
        Date dateObj = parseDate(date);
        if (dateObj != null) {
            Event event = new Event(color, dateObj.getTime());
            compactCalendarView.addEvent(event);
        }
    }

    /**
     * Parses a date string into a Date object.
     *
     * @param dateStr The date string in "yyyy-MM-dd" format.
     * @return The parsed Date object or null if parsing fails.
     */
    private Date parseDate(String dateStr) {
        try {
            return apiDateFormat.parse(dateStr);
        } catch (ParseException e) {
            Log.e(TAG, "Date parsing error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Formats a date string for display purposes.
     *
     * @param date The date string in "yyyy-MM-dd" format.
     * @return The formatted date string in "dd-MM-yyyy" format.
     */
    private String formatDateForDisplay(String date) {
        try {
            Date parsedDate = apiDateFormat.parse(date);
            return parsedDate != null ? displayDateFormat.format(parsedDate) : date;
        } catch (ParseException e) {
            return date;
        }
    }

    /**
     * Static helper method to add appointment views to the container.
     *
     * @param date           The appointment date.
     * @param timeSlot       The time slot of the appointment.
     * @param trainerName    The trainer's name (if applicable).
     * @param customers      The list of customers (if applicable).
     * @param container      The LinearLayout container to add the view to.
     */
    public static void addAppointmentsToView(String date, String timeSlot, String trainerName, List<String> customers, LinearLayout container) {
        Context context = container.getContext();

        LinearLayout appointmentView = new LinearLayout(context);
        appointmentView.setOrientation(LinearLayout.VERTICAL);
        int padding = dpToPx(context, 8);
        appointmentView.setPadding(padding, padding, padding, padding);

        TextView timeView = new TextView(context);
        timeView.setText(timeSlot);
        timeView.setTextSize(16);
        timeView.setTypeface(null, Typeface.BOLD);
        timeView.setTextColor(Color.BLACK);
        appointmentView.addView(timeView);

        if (trainerName != null) {
            TextView trainerView = new TextView(context);
            trainerView.setText("Practice with trainer: " + trainerName);
            trainerView.setTypeface(null, Typeface.BOLD);
            trainerView.setTextColor(ContextCompat.getColor(context, android.R.color.holo_blue_dark));
            appointmentView.addView(trainerView);
        }

        if (customers != null && !customers.isEmpty()) {
            TextView customerView = new TextView(context);
            customerView.setText("Practice with customers: " + TextUtils.join(", ", customers));
            customerView.setTypeface(null, Typeface.BOLD);
            customerView.setTextColor(ContextCompat.getColor(context, android.R.color.holo_blue_dark));
            appointmentView.addView(customerView);
        }

        container.addView(appointmentView);
    }

    /**
     * Static helper method to add a "no appointments" message to the container.
     *
     * @param container The LinearLayout container to add the message to.
     * @param message   The message to display.
     */
    public static void addNoAppointmentsView(LinearLayout container, String message) {
        Context context = container.getContext();
        TextView noAppointmentsView = new TextView(context);
        noAppointmentsView.setText(message);
        noAppointmentsView.setTextSize(16);
        noAppointmentsView.setTextColor(Color.BLACK);
        container.addView(noAppointmentsView);
    }

    /**
     * Converts density-independent pixels (dp) to pixels (px).
     *
     * @param context The context to access resources.
     * @param dp      The value in dp to convert.
     * @return The converted value in px.
     */
    private static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}
