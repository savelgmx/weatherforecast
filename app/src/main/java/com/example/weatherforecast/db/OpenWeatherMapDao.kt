package com.example.weatherforecast.db

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

}