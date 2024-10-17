package com.example.openwatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    static final String API_KEY = "945eade533577498c1645b72a3a7b104";

    private TextView cityTextView, tempTextView, descTextView;
    private Button seeCitiesButton;

    // Liste pour stocker les villes ajoutées temporairement
    private List<String> cityList = new ArrayList<>();
    private String currentCity = "Paris"; // Ville par défaut

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityTextView = findViewById(R.id.cityTextView);
        tempTextView = findViewById(R.id.tempTextView);
        descTextView = findViewById(R.id.descTextView);

        seeCitiesButton = findViewById(R.id.seeCitiesButton);

        // Affiche la météo de la ville par défaut ou de la ville sélectionnée
        fetchWeatherData(currentCity);

        // Bouton pour voir les villes ajoutées
        seeCitiesButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CityListActivity.class);
            startActivity(intent);
        });
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
                        updateUI(weatherResponse);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Erreur lors de la récupération des données", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Échec de la requête", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(WeatherResponse weatherResponse) {
        cityTextView.setText(weatherResponse.getName());

        int roundedTemp = Math.round(weatherResponse.getMain().getTemp());
        tempTextView.setText(String.format("%d °C", roundedTemp));

        descTextView.setText(weatherResponse.getWeather().get(0).getDescription());
    }

    // Méthode pour ajouter une ville à la liste
    public void addCity(String newCityName) {
        if (!cityList.contains(newCityName)) {
            cityList.add(newCityName); // Ajoute la ville à la liste
            Toast.makeText(MainActivity.this, "Ville ajoutée: " + newCityName, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Ville déjà ajoutée.", Toast.LENGTH_SHORT).show();
        }
    }

    // Méthode appelée après l'ajout d'une ville depuis AddCityActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Récupère la ville ajoutée depuis AddCityActivity
            String cityName = data.getStringExtra("cityName");
            if (cityName != null) {
                addCity(cityName); // Ajoute la ville à la liste
            }
        }
    }
}