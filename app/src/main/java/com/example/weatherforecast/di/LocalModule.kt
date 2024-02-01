package com.example.weatherforecast.di

import android.content.Context
import androidx.room.Room
import com.example.weatherforecast.db.OpenWeatherMapDao
import com.example.weatherforecast.db.OpenWeatherMapDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalModule {

    @Provides
    @Singleton
    fun provideOpenWeatherMapDao(database: OpenWeatherMapDatabase): OpenWeatherMapDao =
        database.openWeatherMapDao()

    companion object {
        @Provides
        @Singleton
        fun provideRoomDatabase(@ApplicationContext context: Context): OpenWeatherMapDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                OpenWeatherMapDatabase::class.java,
                "weather_database"
            ).fallbackToDestructiveMigration().build()
    }

}
