package com.example.weatherforecast.di;

import com.example.openweatherforecast.db.OpenWeatherMapDao;
import com.example.weatherforecast.api.OpenWeatherMapAPI;
import com.example.weatherforecast.api.OpenWeatherMapRepositoryImpl;



import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;


@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideOpenWeatherMapRepositoryImpl(api: OpenWeatherMapAPI, dao:OpenWeatherMapDao) : OpenWeatherMapRepositoryImpl {
        return OpenWeatherMapRepositoryImpl(api, dao)
    }

}
