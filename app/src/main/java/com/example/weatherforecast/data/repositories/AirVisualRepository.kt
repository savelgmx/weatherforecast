package com.example.weatherforecast.data.repositories

import com.example.weatherforecast.data.remote.AirVisualApiService
import com.example.weatherforecast.data.remote.AirVisualResponse

interface AirVisualRepository {
    suspend fun getNearestCity(lat: Double, lon: Double, apiKey: String): AirVisualResponse?
}
