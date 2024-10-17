package com.example.openwatherapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CityListActivity extends AppCompatActivity {

    private static final String API_KEY = "945eade533577498c1645b72a3a7b104";
    private WeatherAdapter adapter;
    private List<WeatherResponse> cityWeatherList = new ArrayList<>();
    private SharedPreferences sharedPreferences; // Pour la persistance des données

    private final ActivityResultLauncher<Intent> addCityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    String cityName = data.getStringExtra("cityName");
                    float temperature = data.getFloatExtra("temperature", 0);
                    String description = data.getStringExtra("description");
                    float windSpeed = data.getFloatExtra("windSpeed", 0);
                    int humidity = data.getIntExtra("humidity", 0);

                    WeatherResponse newCityWeather = new WeatherResponse();
                    newCityWeather.setName(cityName);

                    WeatherResponse.Main main = new WeatherResponse.Main();
                    main.setTemp(temperature);
                    main.setHumidity(humidity);
                    newCityWeather.setMain(main);

                    WeatherResponse.Wind wind = new WeatherResponse.Wind();
                    wind.setSpeed(windSpeed);
                    newCityWeather.setWind(wind);

                    WeatherResponse.Weather weather = new WeatherResponse.Weather();
                    weather.setDescription(description);
                    List<WeatherResponse.Weather> weatherList = new ArrayList<>();
                    weatherList.add(weather);
                    newCityWeather.setWeather(weatherList);

                    cityWeatherList.add(newCityWeather);
                    adapter.notifyDataSetChanged();

                    // Sauvegarder la nouvelle ville dans SharedPreferences
                    saveCityList(cityWeatherList);
                } else {
                    Toast.makeText(this, "Aucune ville ajoutée", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);

        // Initialisation de SharedPreferences
        sharedPreferences = getSharedPreferences("CityListPrefs", MODE_PRIVATE);

        RecyclerView recyclerView = findViewById(R.id.weatherRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new WeatherAdapter(this, cityWeatherList);
        recyclerView.setAdapter(adapter);

        Button addCityButton = findViewById(R.id.addCityButton);
        addCityButton.setOnClickListener(v -> {
            Intent intent = new Intent(CityListActivity.this, AddCityActivity.class);
            addCityLauncher.launch(intent); // Utiliser l'ActivityResultLauncher
        });

        // Charger les villes enregistrées dans SharedPreferences
        loadCityWeatherData();
    }

    private void saveCityList(List<WeatherResponse> cityList) {
        // Convertir la liste des villes en JSON
        String json = new Gson().toJson(cityList);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("cityWeatherList", json);
        editor.apply(); // Sauvegarder les données
    }

    private void loadCityWeatherData() {
        String json = sharedPreferences.getString("cityWeatherList", ""); // Récupérer les données

        if (!json.isEmpty()) {
            // Convertir le JSON en liste d'objets WeatherResponse
            WeatherResponse[] weatherArray = new Gson().fromJson(json, WeatherResponse[].class);
            cityWeatherList.clear();
            for (WeatherResponse weather : weatherArray) {
                cityWeatherList.add(weather);
            }
            adapter.notifyDataSetChanged();
        } else {
            // Si aucune donnée, charger des villes par défaut (si nécessaire)
            String[] cities = {"Marseille", "Lyon", "Toulouse", "Nice", "Nantes"};
            for (String city : cities) {
                fetchWeatherData(city);
            }
        }
    }

    private void fetchWeatherData(String cityName) {
        OpenWeatherServices weatherApiService = RetrofitClientInstance.getRetrofitInstance().create(OpenWeatherServices.class);
        Call<WeatherResponse> call = weatherApiService.getCurrentWeather(cityName, API_KEY, "metric", "fr");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    WeatherResponse weatherResponse = response.body();
                    if (weatherResponse != null) {
                        cityWeatherList.add(weatherResponse);
                        adapter.notifyDataSetChanged();
                        saveCityList(cityWeatherList); // Sauvegarder après ajout
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
