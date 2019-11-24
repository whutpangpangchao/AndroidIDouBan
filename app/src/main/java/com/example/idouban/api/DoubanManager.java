package com.example.idouban.api;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.converter.gson.GsonConverterFactory;

public class DoubanManager {
    private static IDoubbanService sDoubbanService;
    public synchronized  static IDoubbanService createDoubanService(){
        if(sDoubbanService==null){
            Retrofit retrofit=createRetrofit();
            sDoubbanService=retrofit.create(IDoubbanService.class);
        }
        return sDoubbanService;
    }
    private static Retrofit createRetrofit(){
        OkHttpClient httpClient;
        HttpLoggingInterceptor logging= new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient= new OkHttpClient.Builder().addInterceptor(logging).build();

        return new Retrofit.Builder().baseUrl(IDoubbanService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();
    }
}
