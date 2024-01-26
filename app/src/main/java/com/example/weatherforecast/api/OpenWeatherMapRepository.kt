package com.example.weatherforecast.api

import androidx.lifecycle.LiveData
import com.example.weatherforecast.db.CurrentWeatherEntity
import com.example.weatherforecast.response.WeatherResponse
import com.example.weatherforecast.utils.Resource


interface OpenWeatherMapRepository {
    suspend fun getWeatherForecast(city: String): Resource<WeatherResponse>
    suspend fun getWeatherForecastFromDB(): LiveData<List<CurrentWeatherEntity>>
}
