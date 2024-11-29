package com.supergym.sep490_supergymmanagement.apiadapter;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;

public class AuthInterceptor implements Interceptor {

    private Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        // Get the JWT token
        SharedPreferences prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String jwtToken = prefs.getString("JWT_TOKEN", null);

        // Attach the token if available
        Request request = chain.request();
        if (jwtToken != null) {
            request = request.newBuilder()
                    .addHeader("Authorization", "Bearer " + jwtToken)
                    .build();
        }
        return chain.proceed(request);
    }
}
