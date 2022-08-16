package com.example.dayout_organizer.api;

import android.annotation.SuppressLint;

import com.example.dayout_organizer.config.AppSharedPreferences;
import com.example.dayout_organizer.ui.App;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.readystatesoftware.chuck.ChuckInterceptor;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

   public static  String BASE_URL = "http://192.168.1.105:8000/";
    public static  String CACHE_BASE_URL = AppSharedPreferences.GET_BASE_URL();



    public static Retrofit retrofit;

    public static TokenInterceptor interceptor = new TokenInterceptor();

    public static OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new ChuckInterceptor(App.getContext()))
            .addInterceptor(interceptor).build();

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            Gson gson = new GsonBuilder().setLenient().create();
            retrofit = new Retrofit.Builder()
                    .client(client)
//                    .client(getUnsafeOkHttpClient())
                    .baseUrl(CACHE_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            return retrofit;
        }
        return retrofit;
    }

    public API getAPI() {
        return getRetrofitInstance().create(API.class);
    }


    public static OkHttpClient getUnsafeOkHttpClient() {
        try {
            TrustManager[] arrayOfTrustManager = new TrustManager[1];
            X509TrustManager x509TrustManager = new X509TrustManager() {
                @SuppressLint("TrustAllX509TrustManager")
                public void checkClientTrusted(X509Certificate[] param1ArrayOfX509Certificate, String param1String) throws CertificateException {
                }

                @SuppressLint("TrustAllX509TrustManager")
                public void checkServerTrusted(X509Certificate[] param1ArrayOfX509Certificate, String param1String) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };

            arrayOfTrustManager[0] = x509TrustManager;
            SSLContext sSLContext = SSLContext.getInstance("SSL");
            SecureRandom secureRandom = new SecureRandom();

            sSLContext.init(null, arrayOfTrustManager, secureRandom);
            SSLSocketFactory sSLSocketFactory = sSLContext.getSocketFactory();
            OkHttpClient.Builder builder = new OkHttpClient.Builder();

            builder.sslSocketFactory(sSLSocketFactory, (X509TrustManager) arrayOfTrustManager[0]);
            HostnameVerifier hostnameVerifier = (param1String, param1SSLSession) -> true;
            builder.hostnameVerifier(hostnameVerifier);
            builder.addInterceptor(interceptor);

            builder.connectTimeout(30, TimeUnit.SECONDS);
            builder.readTimeout(30, TimeUnit.SECONDS);
            builder.writeTimeout(30, TimeUnit.SECONDS);

            return builder.addInterceptor(interceptor).build();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

}
