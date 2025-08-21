package com.example.weatherforecast.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AirVisualApiService {
    @GET("v2/nearest_city")
    suspend fun getNearestCity(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("key") apiKey: String
    ): Response<AirVisualResponse>
}
