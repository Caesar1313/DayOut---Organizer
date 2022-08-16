package com.example.dayout_organizer.api;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.dayout_organizer.config.AppSharedPreferences.GET_ACC_TOKEN;


public class TokenInterceptor implements Interceptor {

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request newRequest=chain.request().newBuilder()
                .header("Authorization","Bearer "+GET_ACC_TOKEN())
                .header("Accept","*/*")
                .header("Content-Type","application/json")
                .build();

        return chain.proceed(newRequest);
    }
}
