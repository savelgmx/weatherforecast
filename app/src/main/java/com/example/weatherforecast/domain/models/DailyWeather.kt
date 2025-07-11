package com.example.weatherforecast.domain.models
/*
These entities will replace or adapt the existing WeatherResponse (current weather) and ForecastResponse (10-day forecast).
 If the old classes contain more data, they can be extended by adding additional fields with default values
 */

data class DailyWeather(
    val dew:Double,              //point of dew (точка росы)
    val uvindex:Int,             //UV index (УФ индекс)
    val date: String,           // "2025-04-03" (datetime)
    val dt:Long,                 //datetime in Unix epoch
    val temp: Double,           // Средняя температура (temp)
    val feelsLike: Double,      // Ощущается как (feelslike)
    val tempMin: Double,        // Минимальная температура (tempmin)
    val tempMax: Double,        // Максимальная температура (tempmax)
    val pressure: Double,       // Давление (pressure)
    val humidity: Int,          // Влажность (humidity)
    val windSpeed: Double,      // Скорость ветра (windspeed)
    val windDeg: Int,           // Направление ветра (winddir)
    val cloudiness: Int,        // Облачность (cloudcover)
    val description: String,    // Описание (conditions)
    val icon: String,           // Иконка (icon)
    val sunrise: Long,          // Восход (sunriseEpoch)
    val sunset: Long,           // Закат (sunsetEpoch)
    val moonPhase: Double,      //фаза луны
    val hours: List<HourlyWeather>? = null // Почасовые данные
)

