package com.example.sep490_supergymmanagement.apiadapter;

import com.example.sep490_supergymmanagement.DateDeserializer;
import com.example.sep490_supergymmanagement.apiadapter.ApiService.ApiService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://10.0.2.2:5000"; // Local API URL for emulator
//    private static final String BASE_URL = "http://192.168.0.102:5000"; // Use your network's IPv4 address
//    private static final String BASE_URL = "https://0a05-58-186-240-4.ngrok-free.app:5000"; // Use your laptop's IPv4 address

    private static Retrofit retrofit;

    public static Retrofit getInstance() {
        if (retrofit == null) {
            // Khởi tạo Gson với DateDeserializer cho kiểu Date
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss") // Định dạng phù hợp với DateTime từ API
                    .registerTypeAdapter(Date.class, new DateDeserializer())
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getInstance().create(ApiService.class);
    }
}
