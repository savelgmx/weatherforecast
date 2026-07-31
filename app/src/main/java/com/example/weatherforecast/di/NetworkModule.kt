package com.example.weatherforecast.di

import com.example.weatherforecast.data.remote.AirVisualApiService
import com.example.weatherforecast.data.remote.WeatherApiService
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 * Hilt module providing the two Retrofit/OkHttp stacks used by the remote weather
 * and air-quality providers.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // @Named qualifiers distinguish the two OkHttp clients and the two API services
    // so Hilt can inject the correct one at each call site
    @Provides
    @Singleton
    @Named("weatherOkHttpClient")
    /**
     * OkHttp client with generous 60s timeouts for the heavy Visual Crossing weather payloads.
     */
    fun provideWeatherOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)  // Set connection timeout
            .readTimeout(60, TimeUnit.SECONDS)     // Set read timeout
            .writeTimeout(60, TimeUnit.SECONDS)    // Set write timeout
            .build()
    }

    /**
     * OkHttp client with short 10s timeouts for the lightweight AirVisual AQI endpoint.
     */
    @Provides
    @Singleton
    @Named("airVisualOkHttpClient")
    fun provideAirVisualOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Retrofit service bound to the Visual Crossing base URL, qualified @Named("weatherApi").
     */
    @Provides
    @Singleton
    @Named("weatherApi")
    fun provideWeatherApi(@Named("weatherOkHttpClient") client: OkHttpClient): WeatherApiService {
        return Retrofit.Builder()
            .baseUrl("https://weather.visualcrossing.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(WeatherApiService::class.java)
    }

    /**
     * Retrofit service bound to the AirVisual base URL, qualified @Named("airVisualApi").
     */
    @Provides
    @Singleton
    @Named("airVisualApi")
    fun provideAirVisualApi(@Named("airVisualOkHttpClient") client: OkHttpClient): AirVisualApiService {
        return Retrofit.Builder()
            .baseUrl("https://api.airvisual.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(AirVisualApiService::class.java)
    }
}
