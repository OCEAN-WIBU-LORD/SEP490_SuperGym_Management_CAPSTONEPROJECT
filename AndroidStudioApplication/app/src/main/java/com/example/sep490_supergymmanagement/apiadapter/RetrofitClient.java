package com.example.sep490_supergymmanagement.apiadapter;

import com.example.sep490_supergymmanagement.apiadapter.ApiService.ApiService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
 //   private static final String BASE_URL = "http://10.0.2.2:5000"; // Local API URL for emulator
    private static final String BASE_URL = "http://192.168.59.107:5000"; // Use your network's IPv4 address
//    private static final String BASE_URL = "https://0a05-58-186-240-4.ngrok-free.app:5000"; // Use your laptop's IPv4 address

    private static Retrofit retrofit;

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getInstance().create(ApiService.class);
    }
}
