package com.example.weatherforecast.data.repositories

import com.example.weatherforecast.data.remote.NominatimResponse

interface NominatimRepository {
    suspend fun getCoordinates(city: String): NominatimResponse?
}
