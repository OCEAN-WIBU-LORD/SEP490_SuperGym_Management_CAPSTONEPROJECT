package com.supergym.sep490_supergymmanagement.apiadapter.ApiService;

import android.content.Context;
import android.util.Log;

import com.supergym.sep490_supergymmanagement.models.CheckInRequest;
import com.supergym.sep490_supergymmanagement.models.CheckInResponse;
import com.supergym.sep490_supergymmanagement.apiadapter.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckInService {

    private final CheckInApi checkInApi;

    public CheckInService(Context context) {
        // Use RetrofitClient to get Retrofit instance
        this.checkInApi = RetrofitClient.getInstance(context).create(CheckInApi.class);
    }

    /**
     * Makes a check-in API call with the given request.
     *
     * @param request CheckInRequest containing user ID and timestamp.
     * @return Call object for enqueueing the request.
     */
    public Call<CheckInResponse> checkIn(CheckInRequest request) {
        return checkInApi.checkIn(request); // Delegate call to CheckInApi
    }

    /**
     * Makes a check-in API call and handles the response using callbacks.
     *
     * @param request   CheckInRequest containing user ID and timestamp.
     * @param onSuccess Callback for successful responses.
     * @param onError   Callback for failed responses or errors.
     */
    public void checkInAsync(CheckInRequest request,
                             OnSuccessCallback onSuccess,
                             OnErrorCallback onError) {
        Call<CheckInResponse> call = checkIn(request);
        call.enqueue(new Callback<CheckInResponse>() {
            @Override
            public void onResponse(Call<CheckInResponse> call, Response<CheckInResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    onSuccess.onSuccess(response.body());
                } else {
                    onError.onError("Failed with code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<CheckInResponse> call, Throwable t) {
                onError.onError("Network error: " + t.getMessage());
            }
        });
    }

    // Callback interfaces for response handling
    public interface OnSuccessCallback {
        void onSuccess(CheckInResponse response);
    }

    public interface OnErrorCallback {
        void onError(String errorMessage);
    }
}
