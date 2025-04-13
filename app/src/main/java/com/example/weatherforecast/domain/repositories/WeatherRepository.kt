package com.example.weatherforecast.domain.repositories

// domain/repositories/WeatherRepository.kt


import com.example.weatherforecast.domain.models.DailyWeather
import com.example.weatherforecast.utils.Resource
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getCurrentWeather(location: String): Flow<Resource<DailyWeather>>
    fun getForecast(location: String, includeHours: Boolean): Flow<Resource<List<DailyWeather>>>
}
