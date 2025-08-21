package com.example.weatherforecast.domain.usecases

import com.example.weatherforecast.data.repositories.AirVisualRepository
import com.example.weatherforecast.data.remote.AirVisualResponse

class GetAirVisualDataUseCase(
    private val repository: AirVisualRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double, apiKey: String): AirVisualResponse? {
        return repository.getNearestCity(lat, lon, apiKey)
    }
}
