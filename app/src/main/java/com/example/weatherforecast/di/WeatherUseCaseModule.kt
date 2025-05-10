package com.example.weatherforecast.di

import com.example.weatherforecast.data.repositories.VisualCrossingRepository
import com.example.weatherforecast.domain.usecases.GetWeatherUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object  WeatherUseCaseModule {

    @Provides
    fun provideGetWeatherUseCase(repository: VisualCrossingRepository): GetWeatherUseCase = GetWeatherUseCase(repository)
}