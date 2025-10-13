package com.example.weatherforecast.domain.models

data class WeatherMapData(
    val centerLat: Double?,
    val centerLon: Double?,
    val points: List<WeatherPoint>
)

