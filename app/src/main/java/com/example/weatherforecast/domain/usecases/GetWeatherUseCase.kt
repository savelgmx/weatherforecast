package com.example.weatherforecast.domain.usecases


import com.example.weatherforecast.domain.models.DailyWeather
import com.example.weatherforecast.domain.repositories.WeatherRepository
import com.example.weatherforecast.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
//Создадим юзкейс для получения прогноза погоды.
class GetWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend fun getCurrentWeather(location: String): Flow<Resource<DailyWeather>> {
        return weatherRepository.getCurrentWeather(location)
    }

    suspend fun getForecast(location: String, includeHours: Boolean): Flow<Resource<List<DailyWeather>>> {
        return weatherRepository.getForecast(location, includeHours)
    }
}