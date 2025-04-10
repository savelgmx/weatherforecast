package com.example.weatherforecast.domain.models

/* These entities will replace or adapt the existing WeatherResponse (current weather) and ForecastResponse (10-day forecast).
 If the old classes contain more data, they can be extended by adding additional fields with default values */

data class HourlyWeather(
    val time: String,           // "12:00:00"
    val temp: Double,           // temp
    val conditions: String,     // "Clear"
    val icon: String            // "clear-day"
)