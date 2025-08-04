package com.example.weatherforecast.domain.usecases

import com.example.weatherforecast.data.repositories.VisualCrossingRepository
import javax.inject.Inject

class SetCityNameUseCase @Inject constructor(
    private val weatherRepository: VisualCrossingRepository
) {
    suspend fun execute(cityName: String) {
        weatherRepository.setCityName(cityName)
    }
}