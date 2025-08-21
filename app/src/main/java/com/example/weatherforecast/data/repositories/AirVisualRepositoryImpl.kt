package com.example.weatherforecast.data.repositories

import com.example.weatherforecast.data.remote.AirVisualApiService
import com.example.weatherforecast.data.remote.AirVisualResponse


class AirVisualRepositoryImpl(
    private val api: AirVisualApiService
) : AirVisualRepository {
    override suspend fun getNearestCity(lat: Double, lon: Double, apiKey: String): AirVisualResponse? {
        return try {
            val response = api.getNearestCity(lat, lon, apiKey)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
