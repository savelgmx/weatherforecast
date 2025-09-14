package com.example.weatherforecast.data.repositories

import com.example.weatherforecast.domain.models.WeatherPoint
import com.example.weatherforecast.utils.WeatherLayer

// Repository interface (domain contract)
interface WeatherMapRepository {
    suspend fun getWeatherPoints(city: String, layer: WeatherLayer): List<WeatherPoint>
}