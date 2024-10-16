package com.example.openwatherapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeatherServices {

    @GET("data/2.5/weather")
    Call<WeatherResponse> getCurrentWeather(
            @Query("q") String cityName,         // Nom de la ville
            @Query("appid") String apiKey,       // Clé API
            @Query("units") String units,        // Unités
            @Query("lang") String lang           // Langue des descriptions
    );
}