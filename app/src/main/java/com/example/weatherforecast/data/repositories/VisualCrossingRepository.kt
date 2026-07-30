package com.example.weatherforecast.data.repositories

import com.example.weatherforecast.domain.models.DailyWeather
import com.example.weatherforecast.utils.Resource


interface VisualCrossingRepository {
    suspend fun getCurrentWeather(city: String, forceRefresh: Boolean = false): Resource<DailyWeather>
    suspend fun getForecastWeather(city: String, forceRefresh: Boolean = false): Resource<List<DailyWeather>>
    suspend fun syncWeather(city: String)
    suspend fun getDeviceCity():String
    suspend fun setCityName(city: String)
}