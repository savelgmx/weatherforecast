// =============================
// domain/usecases/GetWeatherMapDataUseCase.kt
// =============================

package com.example.weatherforecast.domain.usecases

import com.example.weatherforecast.data.repositories.WeatherMapRepository
import com.example.weatherforecast.domain.models.WeatherPoint
import com.example.weatherforecast.utils.WeatherLayer
import javax.inject.Inject

// UseCase to fetch data
class GetWeatherMapDataUseCase @Inject constructor(
    private val repository: WeatherMapRepository
) {
    suspend operator fun invoke(city: String, layer: WeatherLayer): List<WeatherPoint> {
        return repository.getWeatherPoints(city, layer)
    }
}
