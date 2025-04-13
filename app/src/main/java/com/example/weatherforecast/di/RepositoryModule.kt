package com.example.weatherforecast.di;


import com.example.weatherforecast.api.OpenWeatherMapAPI
import com.example.weatherforecast.api.OpenWeatherMapRepository
import com.example.weatherforecast.api.OpenWeatherMapRepositoryImpl
import com.example.weatherforecast.data.db.OpenWeatherMapDao
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
    fun provideAPI() : OpenWeatherMapAPI {
        return   NetworkObject.getAPIInstance()
    }

    @Provides
    @Singleton
    fun provideOpenWeatherMapRepositoryImpl(api: OpenWeatherMapAPI,
                                            dao: OpenWeatherMapDao,
                                            contextProvider: ContextProvider) : OpenWeatherMapRepositoryImpl {
        return OpenWeatherMapRepositoryImpl(api, dao, contextProvider)
    }

        @Provides
        @Singleton
    fun provideOpenWeatherMapRepository(
            api: OpenWeatherMapAPI,
            dao: OpenWeatherMapDao,
            contextProvider: ContextProvider
    ): OpenWeatherMapRepository {
        return provideOpenWeatherMapRepositoryImpl(api, dao, contextProvider)
        }
    }


