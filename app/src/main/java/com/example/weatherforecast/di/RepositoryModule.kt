package com.example.weatherforecast.di;


import com.example.weatherforecast.data.db.WeatherDao
import com.example.weatherforecast.data.remote.WeatherApiService
import com.example.weatherforecast.data.repositories.VisualCrossingRepository
import com.example.weatherforecast.data.repositories.VisualCrossingRepositoryImpl
import dagger.Module
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
        api: WeatherApiService,contextProvider:ContextProvider,weatherDao: WeatherDao

    ): VisualCrossingRepository {
        return VisualCrossingRepositoryImpl(api,contextProvider,weatherDao)
        }
    }


