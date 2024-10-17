package com.example.openwatherapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CityListActivity extends AppCompatActivity {

    private WeatherAdapter adapter;
    private final List<WeatherResponse> cityWeatherList = new ArrayList<>();
    private static final String API_KEY = "945eade533577498c1645b72a3a7b104";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);

        RecyclerView recyclerView = findViewById(R.id.weatherRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Charger les données météo pour quelques villes au démarrage
        loadCityWeatherData();

        // Initialiser l'adapter de la RecyclerView
        adapter = new WeatherAdapter(this, cityWeatherList);
        recyclerView.setAdapter(adapter);
    }

    private void loadCityWeatherData() {
        String[] cities = {"Marseille", "Lyon", "Toulouse", "Nice", "Nantes"};

        for (String city : cities) {
            fetchWeatherData(city);
        }
    }

    private void fetchWeatherData(String cityName) {
        OpenWeatherServices weatherApiService = RetrofitClientInstance.getRetrofitInstance().create(OpenWeatherServices.class);

        Call<WeatherResponse> call = weatherApiService.getCurrentWeather(cityName, API_KEY, "metric", "fr");

        call.enqueue(new Callback<WeatherResponse>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    WeatherResponse weatherResponse = response.body();
                    if (weatherResponse != null) {
                        cityWeatherList.add(weatherResponse);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(CityListActivity.this, "Erreur lors de la récupération des données", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(CityListActivity.this, "Échec de la requête", Toast.LENGTH_SHORT).show();
            }
        });
    }
}