package com.example.weatherforecast.data.remote
/*
    response from Nominatim API
    to define latitude longitude for iqAir request
 */

data class NominatimResponse(
    val lat: String,
    val lon: String
)
