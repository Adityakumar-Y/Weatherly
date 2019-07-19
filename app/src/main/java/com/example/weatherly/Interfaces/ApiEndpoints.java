package com.example.weatherly.Interfaces;

import com.example.weatherly.Models.Data;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiEndpoints {

    @GET("weather")
    Call<Data> getData(@Query("q") String city, @Query("APPID") String key, @Query("units") String units);
}
