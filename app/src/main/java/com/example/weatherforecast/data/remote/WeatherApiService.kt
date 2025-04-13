package com.example.weatherforecast.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
        @GET("VisualCrossingWebServices/rest/services/timeline")
        suspend fun getWeather(
            @Query("location") location: String,
            @Query("unitGroup") unitGroup: String = "metric",
            @Query("include") include: String = "days,hours",
            @Query("key") apiKey: String,
            @Query("contentType") contentType: String = "json"
        ): WeatherApiResponse

}
