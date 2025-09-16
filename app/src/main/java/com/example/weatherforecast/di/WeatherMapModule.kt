package com.example.weatherforecast.di

// =============================
// di/WeatherMapModule.kt
// =============================

import com.example.weatherforecast.data.remote.WeatherApiService
import com.example.weatherforecast.data.repositories.WeatherMapRepository
import com.example.weatherforecast.data.repositories.WeatherMapRepositoryImpl
import com.example.weatherforecast.domain.usecases.GetWeatherMapDataUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WeatherMapModule {

    @Provides
    @Singleton
    fun provideWeatherMapRepository(
        api: WeatherApiService
    ): WeatherMapRepository {
        return WeatherMapRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideGetWeatherMapDataUseCase(
        repository: WeatherMapRepository
    ): GetWeatherMapDataUseCase = GetWeatherMapDataUseCase(repository)
}
