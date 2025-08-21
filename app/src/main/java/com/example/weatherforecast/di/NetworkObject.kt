package com.example.weatherforecast.di



import com.example.weatherforecast.data.remote.AirVisualApiService
import com.example.weatherforecast.data.remote.NominatimApiService
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

    fun getNominatimAPIInstance(): NominatimApiService {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", "WeatherApp/1.0 (savelgmx@gmail.com)")
                    .build()
                chain.proceed(request)
            }
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(NominatimApiService::class.java)
    }
    fun getAirVisualAPIInstance(): AirVisualApiService {
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api.airvisual.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(AirVisualApiService::class.java)
    }


}
