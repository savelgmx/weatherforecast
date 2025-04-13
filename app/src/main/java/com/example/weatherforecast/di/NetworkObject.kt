package com.example.weatherforecast.di



import com.example.weatherforecast.data.remote.WeatherApiService
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkObject {
    private const val BASE_URL = "https://weather.visualcrossing.com/"

    // Configuring OkHttpClient with timeout
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)  // Set connection timeout
        .readTimeout(60, TimeUnit.SECONDS)     // Set read timeout
        .writeTimeout(60, TimeUnit.SECONDS)    // Set write timeout
        .build()

    fun getAPIInstance(): WeatherApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(WeatherApiService::class.java)
    }
}
