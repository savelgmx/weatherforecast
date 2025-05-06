package com.example.weatherforecast.data.db

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.weatherforecast.response.FeelsLike
import com.example.weatherforecast.response.Temp

@Entity(tableName = "daily_weather")
data class DailyWeatherEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
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

