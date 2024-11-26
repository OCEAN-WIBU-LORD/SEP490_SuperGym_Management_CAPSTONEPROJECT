package com.supergym.sep490_supergymmanagement.apiadapter.ApiService;

import com.supergym.sep490_supergymmanagement.models.CheckInRequest;
import com.supergym.sep490_supergymmanagement.models.CheckInResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CheckInApi {
    @POST("api/CheckIn")
    Call<CheckInResponse> checkIn(@Body CheckInRequest request);
}
