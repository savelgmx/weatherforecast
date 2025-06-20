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

    @Query("SELECT MAX(dt) FROM daily_weather")
    suspend fun getLastUpdateTime(): Long?

    @Query("SELECT COUNT(*) FROM daily_weather")
    suspend fun getDailyWeatherCount(): Int

    @Query("SELECT * FROM daily_weather ORDER BY date ASC LIMIT 1")
    suspend fun getCurrentDailyWeatherEntity(): DailyWeatherEntity?

    @Query("SELECT * FROM daily_weather")
    fun getAllDailyWeatherSync(): List<DailyWeatherEntity>}

