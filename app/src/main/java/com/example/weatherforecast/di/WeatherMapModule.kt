package com.example.weatherforecast.di

// =============================
// di/WeatherMapModule.kt
// =============================

import android.content.Context
import com.example.weatherforecast.BuildConfig
import com.example.weatherforecast.data.remote.WeatherApiService
import com.example.weatherforecast.data.repositories.WeatherMapRepository
import com.example.weatherforecast.data.repositories.WeatherMapRepositoryImpl
import com.example.weatherforecast.domain.usecases.GetWeatherMapDataUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.maplibre.android.MapLibre
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object WeatherMapModule {

    @Provides
    @Singleton
    @Named("visualCrossingApiKey")
    fun provideVisualCrossingApiKey(): String {
        return BuildConfig.API_KEY
    }

    @Provides
    @Singleton
    fun provideWeatherMapRepository(
        api: WeatherApiService,
        @Named("visualCrossingApiKey") apiKey: String,
        @Named("weatherTileBaseUrl") tileBaseUrl: String,
        @Named("currentTime") currentTime: String
    ): WeatherMapRepository {
        return WeatherMapRepositoryImpl(api, apiKey, tileBaseUrl, currentTime)
    }

    @Provides
    @Singleton
    fun provideGetWeatherMapDataUseCase(
        repository: WeatherMapRepository
    ): GetWeatherMapDataUseCase {
        return GetWeatherMapDataUseCase(repository)
    }

    @Provides
    @Singleton
    @Named("weatherTileBaseUrl")
    fun provideTileBaseUrl(): String = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/tile"

    @Provides
    @Singleton
    @Named("currentTime")
    fun provideCurrentTime(): String =
        java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd").format(java.time.LocalDate.now())

    /**
     * Initialize MapLibre once per application.
     */
    @Provides
    @Singleton
    fun provideMapLibre(@ApplicationContext context: Context): MapLibre {
        return MapLibre.getInstance(context)
    }
}
