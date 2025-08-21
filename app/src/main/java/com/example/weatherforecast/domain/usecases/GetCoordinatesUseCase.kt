package com.example.weatherforecast.domain.usecases


import com.example.weatherforecast.data.repositories.NominatimRepository
import com.example.weatherforecast.data.remote.NominatimResponse

/*
    A simple use-case class in a clean architecture style,
    designed to fetch geographic coordinates
    for a given city using a repository.
 */

class GetCoordinatesUseCase(
    private val repository: NominatimRepository
) {
    suspend operator fun invoke(city: String): NominatimResponse? {
        return repository.getCoordinates(city)
    }
}
