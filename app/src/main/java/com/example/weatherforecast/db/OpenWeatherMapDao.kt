package com.example.weatherforecast.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherforecast.response.ForecastResponse

@Dao
interface OpenWeatherMapDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(currentWeatherEntity: CurrentWeatherEntity)
    @Query("SELECT * FROM weather")
    fun getWeather(): LiveData<List<CurrentWeatherEntity>>

    @Query("DELETE FROM weather")
    fun deleteAllFromWeather()
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSevenDaysForecast(forecastResponse: ForecastResponse)

    @Query("SELECT * FROM forecast_response")
    fun getForecastResponse(): LiveData<List<ForecastResponse>>

    @Query("DELETE FROM forecast_response")
    fun deleteAllFromForecastResponse()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyWeather(dailyWeather: DailyWeather)
    @Query("DELETE FROM daily_weather")
    fun deleteAllFromDailyWeather()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHourlyWeather(hourlyWeather: HourlyWeather)
    @Query("DELETE FROM hourly_weather")
    fun deleteAllFromHourlyWeather()

}

