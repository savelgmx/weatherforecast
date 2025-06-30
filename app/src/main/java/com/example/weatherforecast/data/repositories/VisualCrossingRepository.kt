package com.example.weatherforecast.data.repositories

import com.example.weatherforecast.response.ForecastResponse
import com.example.weatherforecast.response.WeatherResponse
import com.example.weatherforecast.utils.Resource


interface VisualCrossingRepository {
    suspend fun getCurrentWeather(): Resource<WeatherResponse>
    suspend fun getForecastWeather(): Resource<ForecastResponse>
}