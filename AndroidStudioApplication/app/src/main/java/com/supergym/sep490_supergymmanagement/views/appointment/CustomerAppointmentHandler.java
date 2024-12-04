// CustomerAppointmentHandler.java
package com.supergym.sep490_supergymmanagement.views.appointment;

import android.util.Log;
import android.widget.LinearLayout;

import com.supergym.sep490_supergymmanagement.AppointmentHandler;
import com.supergym.sep490_supergymmanagement.ViewCalendarActivity;
import com.supergym.sep490_supergymmanagement.apiadapter.ApiService.ApiService;
import com.supergym.sep490_supergymmanagement.models.Schedule2;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerAppointmentHandler implements AppointmentHandler {
    private static final String TAG = "CustomerAppointmentHandler";

    @Override
    public void fetchSchedules(ApiService apiService, String userId, int year, int month, ScheduleFetchCallback callback) {
        apiService.getCustomerSchedules(userId, year, month).enqueue(new Callback<List<Schedule2>>() {
            @Override
            public void onResponse(Call<List<Schedule2>> call, Response<List<Schedule2>> response) {
                if (response.isSuccessful()) {
                    List<Schedule2> schedules = response.body() != null ? response.body() : new ArrayList<>();
                    callback.onSuccess(schedules);
                } else if (response.code() == 404) {
                    // Treat 404 as no appointments found
                    Log.e(TAG, "No customer schedules found for the specified month.");
                    callback.onSuccess(new ArrayList<>()); // Pass an empty list to indicate no data
                } else {
                    Log.e(TAG, "Failed to fetch customer schedules. Response Code: " + response.code());
                    callback.onFailure(new Exception("Failed to fetch customer schedules."));
                }
            }

            @Override
            public void onFailure(Call<List<Schedule2>> call, Throwable t) {
                Log.e(TAG, "Error fetching customer schedules: " + t.getMessage());
                callback.onFailure(t);
            }
        });
    }

    @Override
    public void displayAppointments(List<?> schedules, LinearLayout notesContainer) {
        List<Schedule2> customerSchedules = (List<Schedule2>) schedules;
        notesContainer.removeAllViews();
        if (customerSchedules.isEmpty()) {
            ViewCalendarActivity.addNoAppointmentsView(notesContainer, "No appointments for this month");
        } else {
            for (Schedule2 schedule : customerSchedules) {
                ViewCalendarActivity.addAppointmentsToView(
                        schedule.getDate(),
                        schedule.getTimeSlot(),
                        schedule.getTrainerName(),
                        null,
                        notesContainer
                );
            }
        }
    }
}
