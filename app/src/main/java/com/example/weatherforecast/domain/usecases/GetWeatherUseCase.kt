package com.example.weatherforecast.domain.usecases


import com.example.weatherforecast.data.repositories.VisualCrossingRepository
import com.example.weatherforecast.response.ForecastResponse
import com.example.weatherforecast.response.WeatherResponse
import com.example.weatherforecast.utils.Resource
import javax.inject.Inject

//Создадим юзкейс для получения прогноза погоды.
class GetWeatherUseCase @Inject constructor(
    private val weatherRepository: VisualCrossingRepository
) {
    suspend fun getCurrentWeather(): Resource<WeatherResponse> {
        return weatherRepository.getCurrentWeather()
    }

    suspend fun getForecastWeather(): Resource<ForecastResponse> {
        return weatherRepository.getForecastWeather()
    }
}