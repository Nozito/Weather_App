package com.example.openwatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String API_KEY = "945eade533577498c1645b72a3a7b104";

    private TextView cityTextView, tempTextView, descTextView;
    private Button seeCitiesButton;  // Nouveau bouton pour voir d'autres villes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisation des éléments de l'UI
        cityTextView = findViewById(R.id.cityTextView);
        tempTextView = findViewById(R.id.tempTextView);
        descTextView = findViewById(R.id.descTextView);

        // Initialiser le bouton pour rediriger vers les autres villes
        seeCitiesButton = findViewById(R.id.seeCitiesButton);
        seeCitiesButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CityListActivity.class);
            startActivity(intent);
        });

        // Appeler la météo pour Paris par défaut
        fetchWeatherData("Paris");
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
}