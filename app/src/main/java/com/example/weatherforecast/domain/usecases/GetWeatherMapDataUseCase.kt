// =============================
// domain/usecases/GetWeatherMapDataUseCase.kt
// =============================

package com.example.weatherforecast.domain.usecases

import com.example.weatherforecast.data.repositories.WeatherMapPointsResult
import com.example.weatherforecast.data.repositories.WeatherMapRepository
import com.example.weatherforecast.domain.models.WeatherMapData
import com.example.weatherforecast.domain.models.WeatherPoint
import com.example.weatherforecast.utils.WeatherLayer
import javax.inject.Inject

/**
 * UseCase provides both points + center coords by calling repository
 */

class GetWeatherMapDataUseCase @Inject constructor(
    private val repository: WeatherMapRepository
) {
    suspend operator fun invoke(city: String, layer: WeatherLayer): WeatherMapData {
        val result: WeatherMapPointsResult = repository.getWeatherPoints(city, layer)
        var centerLat = result.centerLat
        var centerLon = result.centerLon
        if (centerLat == null || centerLon == null) {
            repository.getCityCenter(city)?.let { center ->
                centerLat = center.first
                centerLon = center.second
            }
        }
        return WeatherMapData(
            centerLat = centerLat,
            centerLon = centerLon,
            points = result.points
        )
    }
}
