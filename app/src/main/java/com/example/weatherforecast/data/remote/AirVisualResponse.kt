package com.example.weatherforecast.data.remote

data class AirVisualResponse(
    val status: String,
    val data: AirVisualData
)

data class AirVisualData(
    val city: String,
    val state: String,
    val country: String,
    val location: AirVisualLocation,
    val current: AirVisualCurrent
)

data class AirVisualLocation(
    val type: String,
    val coordinates: List<Double>
)

data class AirVisualCurrent(
    val pollution: AirVisualPollution,
    val weather: AirVisualWeather
)

data class AirVisualPollution(
    val aqius: Int,
    val mainus: String
)

data class AirVisualWeather(
    val tp: Int,
    val pr: Int,
    val hu: Int
)

