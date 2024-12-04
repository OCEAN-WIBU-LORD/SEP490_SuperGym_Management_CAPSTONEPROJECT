// TrainerAppointmentHandler.java
package com.supergym.sep490_supergymmanagement.views.appointment;

import android.util.Log;
import android.widget.LinearLayout;

import com.supergym.sep490_supergymmanagement.AppointmentHandler;
import com.supergym.sep490_supergymmanagement.ViewCalendarActivity;
import com.supergym.sep490_supergymmanagement.apiadapter.ApiService.ApiService;
import com.supergym.sep490_supergymmanagement.models.ScheduleForTrainer;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrainerAppointmentHandler implements AppointmentHandler {
    private static final String TAG = "TrainerAppointmentHandler";

    @Override
    public void fetchSchedules(ApiService apiService, String userId, int year, int month, ScheduleFetchCallback callback) {
        apiService.getTrainerSchedules(userId, year, month).enqueue(new Callback<List<ScheduleForTrainer>>() {
            @Override
            public void onResponse(Call<List<ScheduleForTrainer>> call, Response<List<ScheduleForTrainer>> response) {
                if (response.isSuccessful()) {
                    List<ScheduleForTrainer> schedules = response.body() != null ? response.body() : new ArrayList<>();
                    callback.onSuccess(schedules);
                } else if (response.code() == 404) {
                    // Treat 404 as no appointments found
                    Log.e(TAG, "No trainer schedules found for the specified month.");
                    callback.onSuccess(new ArrayList<>()); // Pass an empty list to indicate no data
                } else {
                    Log.e(TAG, "Failed to fetch trainer schedules. Response Code: " + response.code());
                    callback.onFailure(new Exception("Failed to fetch trainer schedules."));
                }
            }

            @Override
            public void onFailure(Call<List<ScheduleForTrainer>> call, Throwable t) {
                Log.e(TAG, "Error fetching trainer schedules: " + t.getMessage());
                callback.onFailure(t);
            }
        });
    }

    @Override
    public void displayAppointments(List<?> schedules, LinearLayout notesContainer) {
        List<ScheduleForTrainer> trainerSchedules = (List<ScheduleForTrainer>) schedules;
        notesContainer.removeAllViews();
        if (trainerSchedules.isEmpty()) {
            ViewCalendarActivity.addNoAppointmentsView(notesContainer, "No appointments for this month");
        } else {
            for (ScheduleForTrainer schedule : trainerSchedules) {
                ViewCalendarActivity.addAppointmentsToView(
                        schedule.getDate(),
                        schedule.getTimeSlot(),
                        null,
                        schedule.getCustomers(),
                        notesContainer
                );
            }
        }
    }
}
