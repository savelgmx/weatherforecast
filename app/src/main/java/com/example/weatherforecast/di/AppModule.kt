package com.example.weatherforecast.di

import com.example.weatherforecast.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Named("iqAirApiKey")
    fun provideIqAirApiKey(): String = BuildConfig.IQAIR_API_KEY
}