package com.example.weatherforecast.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weatherforecast.response.ForecastResponse


@Database(entities = [CurrentWeatherEntity::class, ForecastResponse::class,
                DailyWeather::class,HourlyWeather::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class OpenWeatherMapDatabase : RoomDatabase() {
    abstract fun openWeatherMapDao(): OpenWeatherMapDao

    companion object {
        @Volatile
        private var instance: OpenWeatherMapDatabase? = null

        operator fun invoke(context: Context): OpenWeatherMapDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): OpenWeatherMapDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                OpenWeatherMapDatabase::class.java,
                "open_weather_map_database.db"
            ).build()
        }
    }
}

