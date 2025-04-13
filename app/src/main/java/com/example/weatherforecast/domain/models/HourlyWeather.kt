package com.example.weatherforecast.domain.models

/* These entities will replace or adapt the existing WeatherResponse (current weather) and ForecastResponse (10-day forecast).
 If the old classes contain more data, they can be extended by adding additional fields with default values */

data class HourlyWeather(
    val time: String,           // "12:00:00" (datetime)
    val temp: Double,           // Температура (temp)
    val feelsLike: Double,      // Ощущается как (feelslike)
    val pressure: Double,       // Давление (pressure)
    val humidity: Int,          // Влажность (humidity)
    val windSpeed: Double,      // Скорость ветра (windspeed)
    val windDeg: Int,           // Направление ветра (winddir)
    val cloudiness: Int,        // Облачность (cloudcover)
    val description: String,    // Описание (conditions)
    val icon: String            // Иконка (icon)
)