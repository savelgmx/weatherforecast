package com.example.weatherforecast.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_weather")
data class DailyWeatherEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val dt: Long,
    val temp: Double,
    val feelsLike: Double,
    val tempMin: Double,
    val tempMax: Double,
    val pressure: Double,
    val humidity: Int,
    val windSpeed: Double,
    val windDeg: Int,
    val cloudiness: Int,
    val description: String,
    val icon: String,
    val sunrise: Long,
    val sunset: Long,
    val moonPhase: Double
)

