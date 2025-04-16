package com.example.weatherforecast.data.repositories

import androidx.lifecycle.LiveData
import com.example.weatherforecast.data.db.CurrentWeatherEntity
import com.example.weatherforecast.response.ForecastResponse
import com.example.weatherforecast.response.WeatherResponse
import com.example.weatherforecast.utils.Resource


interface VisualCrossingRepository {
    suspend fun getCurrentWeather(): Resource<WeatherResponse>
    suspend fun getForecastWeather(): Resource<ForecastResponse>
    suspend fun getWeatherForecastFromDB(): LiveData<List<CurrentWeatherEntity>>
}