package com.example.weatherforecast.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
// Nominatim API returns in response latitude longitude for given city name
interface NominatimApiService {
    @GET("search")
    suspend fun searchCity(
        @Query("q") city: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 1
    ): Response<List<NominatimResponse>>
}
