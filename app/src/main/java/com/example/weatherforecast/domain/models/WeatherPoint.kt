package com.example.weatherforecast.domain.models

// =============================
// Domain Layer
// =============================

// Domain model for weather point
data class WeatherPoint(
    val lat: Double,
    val lon: Double,
    val temperature: Double?,
    val cloudCover: Double?,
    val precipitation: Double?
)