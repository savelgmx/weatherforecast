package com.example.weatherforecast.domain.usecases


import com.example.weatherforecast.data.repositories.VisualCrossingRepository
import com.example.weatherforecast.domain.models.DailyWeather
import com.example.weatherforecast.utils.Resource
import javax.inject.Inject

//Создадим юзкейс для получения прогноза погоды.
class GetWeatherUseCase @Inject constructor(
    private val weatherRepository: VisualCrossingRepository
) {
    suspend fun getCurrentWeather(city: String, forceRefresh: Boolean = false): Resource<DailyWeather> {
        return weatherRepository.getCurrentWeather(city, forceRefresh)
    }

    suspend fun getForecastWeather(city: String, forceRefresh: Boolean = false): Resource<List<DailyWeather>> {
        return weatherRepository.getForecastWeather(city, forceRefresh)
    }
}