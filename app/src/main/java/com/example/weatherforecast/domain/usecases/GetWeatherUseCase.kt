package com.example.weatherforecast.domain.usecases


import com.example.weatherforecast.domain.models.DailyWeather
import com.example.weatherforecast.domain.repositories.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
//Создадим юзкейс для получения прогноза погоды.
class GetWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    operator fun invoke(location: String, includeHours: Boolean = false): Flow<List<DailyWeather>> {
        return weatherRepository.getWeatherForecast(location, includeHours)
    }
}