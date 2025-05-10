package com.example.weatherforecast.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WeatherDao {
    @Insert
    suspend fun insertDailyWeather(daily: DailyWeatherEntity): Long

    @Insert
    suspend fun insertHourlyWeather(hourlies: List<HourlyWeatherEntity>)

    @Query("DELETE FROM daily_weather")
    suspend fun deleteAllDailyWeather()

    @Query("DELETE FROM hourly_weather")
    suspend fun deleteAllHourlyWeather()

    @Query("SELECT * FROM daily_weather")
    fun getAllDailyWeather(): LiveData<List<DailyWeatherEntity>>
}

