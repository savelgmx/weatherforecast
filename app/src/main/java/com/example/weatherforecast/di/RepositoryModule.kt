package com.example.weatherforecast.di;


import com.example.weatherforecast.data.db.WeatherDao
import com.example.weatherforecast.data.remote.AirVisualApiService
import com.example.weatherforecast.data.remote.WeatherApiService
import com.example.weatherforecast.data.repositories.AirVisualRepository
import com.example.weatherforecast.data.repositories.AirVisualRepositoryImpl
import com.example.weatherforecast.data.repositories.VisualCrossingRepository
import com.example.weatherforecast.data.repositories.VisualCrossingRepositoryImpl
import android.content.Context
import dagger.Module
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideApi(): WeatherApiService {
        return   NetworkObject.getAPIInstance()
    }

    @Provides
    @Singleton
    fun provideVisualCrossingRepository(
        api: WeatherApiService,@ApplicationContext context: Context,weatherDao: WeatherDao

    ): VisualCrossingRepository {
        return VisualCrossingRepositoryImpl(api,context,weatherDao)
    }
    @Provides
    @Singleton
    fun provideAirVisualApi(): AirVisualApiService {
        return NetworkObject.getAirVisualAPIInstance()
    }

    @Provides
    @Singleton
    fun provideAirVisualRepository(api: AirVisualApiService): AirVisualRepository {
        return AirVisualRepositoryImpl(api)
    }

}


