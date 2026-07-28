package com.example.weatherforecast.utils

// Enum to represent layer types
enum class WeatherLayer(val displayName: String, val tilePath: String) {
    Temperature("Temperature", "temp"),
    Precipitation("Precipitation", "precipcomposite"),
    Wind("Wind", "windspeed"),
}