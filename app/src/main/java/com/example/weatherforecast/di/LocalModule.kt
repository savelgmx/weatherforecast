package com.example.weatherforecast.di

import android.content.Context
import androidx.room.Room
import com.example.weatherforecast.data.db.OpenWeatherMapDao
import com.example.weatherforecast.data.db.OpenWeatherMapDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Provides
    @Singleton
    fun provideOpenWeatherMapDao(database: OpenWeatherMapDatabase): OpenWeatherMapDao =
        database.openWeatherMapDao()


        @Provides
        @Singleton
        fun provideRoomDatabase(@ApplicationContext context: Context): OpenWeatherMapDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                OpenWeatherMapDatabase::class.java,
                "open_weather_map_database"
            ).fallbackToDestructiveMigration().build()
    }

