package com.example.weatherforecast.di

import android.app.Application
import com.example.weatherforecast.components.DataStoreManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApplicationContextProvider(application: Application): ContextProvider {
        return ApplicationContextProvider(application)
    }

    @Provides
    @Singleton
    fun provideDataStoreManager(contextProvider: ContextProvider): DataStoreManager {
        return DataStoreManager
}
}