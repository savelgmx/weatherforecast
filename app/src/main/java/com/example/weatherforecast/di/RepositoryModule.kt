package com.example.weatherforecast.di;


import com.example.weatherforecast.data.db.WeatherDao
import com.example.weatherforecast.data.remote.AirVisualApiService
import com.example.weatherforecast.data.remote.NominatimApiService
import com.example.weatherforecast.data.remote.WeatherApiService
import com.example.weatherforecast.data.repositories.AirVisualRepository
import com.example.weatherforecast.data.repositories.AirVisualRepositoryImpl
import com.example.weatherforecast.data.repositories.NominatimRepository
import com.example.weatherforecast.data.repositories.NominatimRepositoryImpl
import com.example.weatherforecast.data.repositories.VisualCrossingRepository
import com.example.weatherforecast.data.repositories.VisualCrossingRepositoryImpl
import com.example.weatherforecast.domain.usecases.GetAirVisualDataUseCase
import com.example.weatherforecast.domain.usecases.GetCoordinatesUseCase
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
    //added nomitamin repositary
    @Provides
    @Singleton
    fun provideNominatimApi(): NominatimApiService {
        return NetworkObject.getNominatimAPIInstance()
    }

    @Provides
    @Singleton
    fun provideNominatimRepository(api: NominatimApiService): NominatimRepository {
        return NominatimRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideGetCoordinatesUseCase(repository: NominatimRepository): GetCoordinatesUseCase {
        return GetCoordinatesUseCase(repository)
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

    @Provides
    @Singleton
    fun provideGetAirVisualDataUseCase(repository: AirVisualRepository): GetAirVisualDataUseCase {
        return GetAirVisualDataUseCase(repository)
    }


}


