package com.example.sep490_supergymmanagement.apiadapter.ApiService;

import com.example.sep490_supergymmanagement.models.QrCodeRequest;
import com.example.sep490_supergymmanagement.models.QrCodeResponse;
import com.example.sep490_supergymmanagement.models.RegisterUserDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/api/Auth/register")
    Call<Void> registerUser(@Body RegisterUserDto registerUserDto);

    // New API call for generating QR codes
    @POST("/api/qr/generate")
    Call<QrCodeResponse> generateQrCodes(@Body QrCodeRequest request);
}