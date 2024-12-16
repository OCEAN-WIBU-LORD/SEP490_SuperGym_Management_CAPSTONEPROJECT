package com.supergym.sep490_supergymmanagement.apiadapter;

import android.content.Context;

import com.supergym.sep490_supergymmanagement.DateDeserializer;
import com.supergym.sep490_supergymmanagement.apiadapter.ApiService.ApiService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
      private static final String BASE_URL = "http://10.0.2.2:5000"; // Local API URL for emulator
  //    private static final String BASE_URL = "http://192.168.59.107:5000"; // Use your network's IPv4 address
 //     private static final String BASE_URL = "http://10.0.2.2:5000"; // Local API URL for emulator
//private static final String BASE_URL = "https://e8f2-1-52-29-185.ngrok-free.app"; // Local API URL for emulator

    // https://bcaf-199-187-211-23.ngrok-free.app   //NGROK SERVER
    //https://58f6-58-186-240-10.ngrok-free.app
   // private static final String BASE_URL = "https://bcaf-199-187-211-23.ngrok-free.app";

 //private static final String BASE_URL = "http://192.168.0.101:5000";  // Mang cuc phat 4Gy

//  private static final String BASE_URL = "http://10.0.2.2:5000"; // Local API URL for emulator
    //192.168.28.53
    //private static final String BASE_URL = "http://192.168.28.53:5000"; //mang Azzan
    //192.168.1.81 Mang SuperGym
    //10.33.44.115 Mang FU FPT
    //172.20.10.13 mang Iphone Minh
    //192.168.0.101 Mang cuc phat 4G
  //  private static final String BASE_URL = "http://172.20.10.13:5000"; //mang Iphone Minh
   // private static final String BASE_URL = "http://10.33.44.115:5000"; // Mang FU FPT
   //   private static final String BASE_URL = "http://192.168.1.81:5000"; // Mang SuperGym
   //  private static final String BASE_URL = "http://192.168.59.107:5000"; // Mang Nha
    // private static final String BASE_URL = "https://0a05-58-186-240-4.ngrok-free.app:5000"; // Use your laptop's IPv4 address

    private static Retrofit retrofit;

    public static Retrofit getInstance(Context context) {
        if (retrofit == null) {
            // Khởi tạo OkHttpClient với timeout
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(context)) // Add interceptor
                    .connectTimeout(60, TimeUnit.SECONDS) // Thời gian chờ kết nối
                    .readTimeout(60, TimeUnit.SECONDS)    // Thời gian chờ đọc dữ liệu
                    .writeTimeout(60, TimeUnit.SECONDS)   // Thời gian chờ ghi dữ liệu
                    .build();

            // Khởi tạo Gson với DateDeserializer cho kiểu Date
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss") // Định dạng phù hợp với DateTime từ API
                    .registerTypeAdapter(Date.class, new DateDeserializer())
                    .create();

            // Khởi tạo Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient) // Gắn OkHttpClient với timeout vào Retrofit
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService(Context context) {
        return getInstance(context).create(ApiService.class);
    }
}
