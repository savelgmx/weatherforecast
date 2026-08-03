package com.example.weatherforecast.data.repositories

import android.util.Log
import com.example.weatherforecast.data.remote.AirVisualApiService
import com.example.weatherforecast.data.remote.AirVisualResponse


class AirVisualRepositoryImpl(
    private val api: AirVisualApiService
) : AirVisualRepository {

    private companion object {
        private const val TAG = "AirVisualRepositoryImpl"
    }

    override suspend fun getNearestCity(lat: Double, lon: Double, apiKey: String): AirVisualResponse? {
        return try {
            val response = api.getNearestCity(lat, lon, apiKey)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            // Log the failure through the tag so it is greppable in logcat
            // instead of relying on a raw stack trace to stderr.
            Log.e(TAG, "getNearestCity failed", e)
            null
        }
    }
}
