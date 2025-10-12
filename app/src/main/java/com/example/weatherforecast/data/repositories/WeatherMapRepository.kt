package com.example.weatherforecast.data.repositories

import com.example.weatherforecast.domain.models.WeatherPoint
import com.example.weatherforecast.utils.WeatherLayer


/**
 * Repository contract for Weather Map feature.
 * Provides weather points and center coordinates of a city.
 */
interface WeatherMapRepository {
    suspend fun getWeatherPoints(city: String, layer: WeatherLayer): List<WeatherPoint>
    suspend fun getCityCenter(city: String): Pair<Double, Double>?
    // Конфигурация карты (style URL для базовой карты)
    fun getMapStyleUrl(): String
    // URL тайлов погодного слоя (XYZ template, {z}/{x}/{y})
    fun getWeatherTileUrl(layer: WeatherLayer): String
}