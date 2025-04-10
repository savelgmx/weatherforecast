package com.example.weatherforecast.domain.models
/*
These entities will replace or adapt the existing WeatherResponse (current weather) and ForecastResponse (10-day forecast).
 If the old classes contain more data, they can be extended by adding additional fields with default values
 */

data class DailyWeather(
    val date: String,           // "2025-04-03"
    val maxTemp: Double,        // tempmax
    val minTemp: Double,        // tempmin
    val avgTemp: Double,        // temp
    val conditions: String,     // "Clear"
    val icon: String,           // "clear-day"
    val hours: List<HourlyWeather>? = null // Почасовые данные, если запрошены
)