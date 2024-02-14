package com.example.weatherforecast.di



import com.example.weatherforecast.api.OpenWeatherMapAPI
import com.example.weatherforecast.utils.AppConstants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkObject {

    fun getAPIInstance(): OpenWeatherMapAPI {
        return Retrofit.Builder()
            .baseUrl(AppConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenWeatherMapAPI::class.java)
    }
}
