package com.example.weatherforecast.di

// =============================
// di/WeatherMapModule.kt
// =============================

import com.example.weatherforecast.BuildConfig
import com.example.weatherforecast.data.remote.WeatherApiService
import com.example.weatherforecast.data.repositories.WeatherMapRepository
import com.example.weatherforecast.data.repositories.WeatherMapRepositoryImpl

import com.example.weatherforecast.domain.usecases.GetWeatherMapDataUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WeatherMapModule {

    @Provides
    @Singleton
    @Named("visualCrossingApiKey")
    fun provideVisualCrossingApiKey(): String {
        // API_KEY из gradle.properties → BuildConfig.API_KEY
        return BuildConfig.API_KEY
    }

    @Provides
    @Singleton
    fun provideWeatherMapRepository(
        api: WeatherApiService,
        @Named("visualCrossingApiKey") apiKey: String
    ): WeatherMapRepository {
        return WeatherMapRepositoryImpl(api, apiKey)
    }

    @Provides
    @Singleton
    fun provideGetWeatherMapDataUseCase(
        repository: WeatherMapRepository
    ): GetWeatherMapDataUseCase = GetWeatherMapDataUseCase(repository)
}
