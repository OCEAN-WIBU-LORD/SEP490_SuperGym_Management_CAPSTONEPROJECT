// AppointmentHandler.java
package com.supergym.sep490_supergymmanagement;

import android.widget.LinearLayout;

import com.supergym.sep490_supergymmanagement.apiadapter.ApiService.ApiService;

import java.util.List;

public interface AppointmentHandler {
    void fetchSchedules(ApiService apiService, String userId, int year, int month, ScheduleFetchCallback callback);
    void displayAppointments(List<?> schedules, LinearLayout notesContainer);

    interface ScheduleFetchCallback {
        void onSuccess(List<?> schedules);
        void onFailure(Throwable t);
    }
}
