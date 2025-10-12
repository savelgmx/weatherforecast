// =============================
// domain/usecases/GetWeatherMapDataUseCase.kt
// =============================

package com.example.weatherforecast.domain.usecases

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
        val points = repository.getWeatherPoints(city, layer)
        val center = repository.getCityCenter(city)
        val styleUrl = repository.getMapStyleUrl()
        val tileUrl = repository.getWeatherTileUrl(layer)
        return WeatherMapData(
            centerLat = center?.first,
            centerLon = center?.second,
            points = points,
            styleUrl = styleUrl,
            tileUrlTemplate = tileUrl
        )
    }
}
