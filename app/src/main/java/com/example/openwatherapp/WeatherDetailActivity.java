package com.example.openwatherapp;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class WeatherDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_card_item);

        // Initialisation des TextViews
        TextView cityTextView = findViewById(R.id.cityNameTextView);
        TextView tempTextView = findViewById(R.id.temperatureTextView);
        TextView descTextView = findViewById(R.id.descriptionTextView);
        TextView humidityTextView = findViewById(R.id.humidityTextView);
        TextView windTextView = findViewById(R.id.windTextView);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String cityName = extras.getString("city_name");
            float temperature = extras.getFloat("temperature");
            String description = extras.getString("description");
            int humidity = extras.getInt("humidity");
            float windSpeed = extras.getFloat("wind_speed");

            cityTextView.setText(cityName);
            tempTextView.setText(String.format("%.1f°C", temperature));
            descTextView.setText(description);
            humidityTextView.setText(String.format("Humidité : %d%%", humidity));
            windTextView.setText(String.format("Vent : %.1f km/h", windSpeed));
        }
    }
}