package com.example.weatherforecast.data.db


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "hourly_weather",
    foreignKeys = [ForeignKey(
        entity = DailyWeatherEntity::class,
        parentColumns = ["id"],
        childColumns = ["dailyId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class HourlyWeatherEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val dailyId: Long,
    val time: String,
    val dt: Long,
    val temp: Double,
    val feelsLike: Double,
    val pressure: Double,
    val humidity: Int,
    val windSpeed: Double,
    val windDeg: Int,
    val cloudiness: Int,
    val description: String,
    val icon: String
)