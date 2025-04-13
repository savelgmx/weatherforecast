package com.example.weatherforecast.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface OpenWeatherMapDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(currentWeatherEntity: CurrentWeatherEntity)
    @Query("SELECT * FROM weather")
    fun getWeather(): LiveData<List<CurrentWeatherEntity>>

    @Query("DELETE FROM weather")
    fun deleteAllFromWeather()
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSevenDaysForecast(forecastWeatherEntity: ForecastWeatherEntity)

    @Query("SELECT * FROM forecast_weather")
    fun getForecastWeather(): LiveData<List<ForecastWeatherEntity>>

    @Query("DELETE FROM forecast_weather")
    fun deleteAllFromForecastWeather()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyWeather(dailyWeatherEntity: DailyWeatherEntity)
    @Query("DELETE FROM daily_weather")
    fun deleteAllFromDailyWeather()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHourlyWeather(hourlyWeatherEntity: HourlyWeatherEntity)
    @Query("DELETE FROM hourly_weather")
    fun deleteAllFromHourlyWeather()

}

