package com.example.openwatherapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCityActivity extends AppCompatActivity {

    private EditText cityTextView;
    private Button addCityButton;
    private static final String API_KEY = "945eade533577498c1645b72a3a7b104"; // Utilise ta propre clé API

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);

        // Initialisation des composants
        cityTextView = findViewById(R.id.cityTextView);
        addCityButton = findViewById(R.id.addCityButton);

        // Gestion du clic sur le bouton pour ajouter une ville
        addCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cityName = cityTextView.getText().toString().trim();
                if (!cityName.isEmpty()) {
                    // Normaliser le nom de la ville (première lettre majuscule, reste en minuscule)
                    cityName = normalizeCityName(cityName);

                    // Appeler la méthode pour récupérer les données de la ville via l'API
                    fetchWeatherData(cityName);
                } else {
                    Toast.makeText(AddCityActivity.this, "Veuillez entrer le nom d'une ville", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Méthode pour normaliser le nom de la ville
    private String normalizeCityName(String cityName) {
        if (cityName != null && !cityName.isEmpty()) {
            return cityName.substring(0, 1).toUpperCase() + cityName.substring(1).toLowerCase();
        }
        return cityName;
    }

    // Méthode pour interroger l'API OpenWeather et récupérer les données météo
    private void fetchWeatherData(String cityName) {
        OpenWeatherServices weatherApiService = RetrofitClientInstance.getRetrofitInstance().create(OpenWeatherServices.class);

        // Appel à l'API OpenWeather pour obtenir les informations météo
        Call<WeatherResponse> call = weatherApiService.getCurrentWeather(cityName, API_KEY, "metric", "fr");

        // Enqueue de la requête pour récupérer les données
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    WeatherResponse weatherResponse = response.body();
                    if (weatherResponse != null) {
                        // Log des données reçues pour déboguer
                        Log.d("API Response", "Ville: " + weatherResponse.getName() + ", Temp: " + weatherResponse.getMain().getTemp());

                        // Passer les données météo à l'activité précédente via Intent
                        Intent intent = new Intent();
                        intent.putExtra("cityName", weatherResponse.getName());
                        intent.putExtra("temperature", weatherResponse.getMain().getTemp());
                        intent.putExtra("description", weatherResponse.getWeather().get(0).getDescription());
                        intent.putExtra("windSpeed", weatherResponse.getWind().getSpeed());
                        intent.putExtra("humidity", weatherResponse.getMain().getHumidity());

                        // Renvoi des données à l'activité précédente
                        setResult(RESULT_OK, intent);
                        finish(); // Fin de l'activité et retour à la précédente
                    }
                } else {
                    // Afficher un log d'erreur en cas d'échec de la requête
                    Log.e("API Error", "Erreur HTTP: " + response.code());
                    Toast.makeText(AddCityActivity.this, "Impossible de récupérer les données de la ville", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                // Afficher un message d'erreur en cas d'échec de la requête
                Log.e("API Failure", "Erreur de connexion: " + t.getMessage());
                Toast.makeText(AddCityActivity.this, "Erreur de connexion. Veuillez réessayer.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}