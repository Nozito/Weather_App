package com.example.openwatherapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.BreakIterator;
import java.util.List;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {

    private final Context context;
    private final List<WeatherResponse> weatherList;

    public WeatherAdapter(Context context, List<WeatherResponse> weatherList) {
        this.context = context;
        this.weatherList = weatherList;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_card_item, parent, false);
        return new WeatherViewHolder(itemView);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(WeatherViewHolder holder, int position) {
        WeatherResponse weather = weatherList.get(position);
        holder.cityNameTextView.setText(weather.getName());
        holder.temperatureTextView.setText(String.format("%.1f°C", weather.getMain().getTemp()));
        holder.descriptionTextView.setText(weather.getWeather().get(0).getDescription());

        holder.windTextView.setText(String.format("Vent : %.1f km/h", weather.getWind().getSpeed()));
        holder.humidityTextView.setText(String.format("Humidité : %d%%", weather.getMain().getHumidity()));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, WeatherDetailActivity.class);
            intent.putExtra("city_name", weather.getName());
            intent.putExtra("temperature", weather.getMain().getTemp());
            intent.putExtra("description", weather.getWeather().get(0).getDescription());
            intent.putExtra("humidity", weather.getMain().getHumidity());
            intent.putExtra("wind_speed", weather.getWind().getSpeed());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    public static class WeatherViewHolder extends RecyclerView.ViewHolder {
        public TextView cityNameTextView;
        public TextView temperatureTextView;
        public TextView descriptionTextView;
        public TextView humidityTextView;
        public TextView windTextView;

        public WeatherViewHolder(View view) {
            super(view);
            cityNameTextView = view.findViewById(R.id.cityNameTextView);
            temperatureTextView = view.findViewById(R.id.temperatureTextView);
            descriptionTextView = view.findViewById(R.id.descriptionTextView);
            humidityTextView = view.findViewById(R.id.humidityTextView);
            windTextView = view.findViewById(R.id.windTextView);
        }
    }
}