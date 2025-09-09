package com.example.weatherforecast.data.db


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_weather")
data class DailyWeatherEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dew:Double,              //point of dew (точка росы)
    val uvindex:Int,             //UV index (УФ индекс)
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
    val moonPhase: Double,
    val visibility:Double,
    val cityName: String?,
    val timezone: String,
    val latitude: Double,
    val longitude: Double
)
