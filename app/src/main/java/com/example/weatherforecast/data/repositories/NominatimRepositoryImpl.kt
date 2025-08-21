package com.example.weatherforecast.data.repositories

import com.example.weatherforecast.data.remote.NominatimApiService
import com.example.weatherforecast.data.remote.NominatimResponse

class NominatimRepositoryImpl(
    private val api: NominatimApiService
) : NominatimRepository {

    override suspend fun getCoordinates(city: String): NominatimResponse? {
        return try {
            val response = api.searchCity(city)
            if (response.isSuccessful) {
                response.body()?.firstOrNull()
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
