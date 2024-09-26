package com.example.weatherforecast.di



import com.example.weatherforecast.api.OpenWeatherMapAPI
import com.example.weatherforecast.utils.AppConstants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkObject {

    // Configuring OkHttpClient with timeout
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)  // Set connection timeout
        .readTimeout(60, TimeUnit.SECONDS)     // Set read timeout
        .writeTimeout(60, TimeUnit.SECONDS)    // Set write timeout
        .build()

    // Function to get API instance
    fun getAPIInstance(): OpenWeatherMapAPI {
        return Retrofit.Builder()
            .baseUrl(AppConstants.BASE_URL)
            .client(okHttpClient)  // Pass the OkHttpClient with timeout
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenWeatherMapAPI::class.java)
    }
}
