package com.example.weatherforecast.domain.repositories

// domain/repositories/WeatherRepository.kt


import com.example.weatherforecast.domain.models.DailyWeather
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getWeatherForecast(location: String, includeHours: Boolean): Flow<List<DailyWeather>>
}
