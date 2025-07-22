package com.example.weatherforecast.data.repositories

import com.example.weatherforecast.response.ForecastResponse
import com.example.weatherforecast.response.WeatherResponse
import com.example.weatherforecast.utils.Resource


interface VisualCrossingRepository {
    suspend fun getCurrentWeather(city: String, forceRefresh: Boolean = false): Resource<WeatherResponse>
    suspend fun getForecastWeather(city: String, forceRefresh: Boolean = false): Resource<ForecastResponse>
    suspend fun syncWeather(city: String)
}