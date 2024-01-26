package com.example.weatherforecast.di



import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkObject {

    const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    const val API_KEY = "33cc710b4ef18155198d89c3b2033f56"

    fun getAPIInstance(): OpenWeatherMapAPI {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenWeatherMapAPI::class.java)
    }
}
