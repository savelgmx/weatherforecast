package com.example.weatherforecast.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherApiResponse(
    @Json(name = "resolvedAddress") val resolvedAddress: String,
    @Json(name = "days") val days: List<ApiDay>
)
@JsonClass(generateAdapter = true)
data class ApiDay(
    @Json(name = "datetime") val date: String,
    @Json(name = "temp") val temp: Double,
    @Json(name = "conditions") val conditions: String,
    @Json(name = "icon") val icon: String
)