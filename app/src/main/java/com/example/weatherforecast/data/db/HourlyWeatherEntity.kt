package com.example.weatherforecast.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "hourly_weather")
@TypeConverters(Converters::class)
data class HourlyWeatherEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name="clouds")
    val clouds: Int,
    @ColumnInfo(name="dew_point")
    val dewPoint: Double,
    // Include other fields as required
    @ColumnInfo(name="dt")
    val dt: Int,
    @ColumnInfo(name="feelsLike")
    val feelsLike: Double ,
    @ColumnInfo(name="humidity")
    val humidity: Int,
    @ColumnInfo(name="pressure")
    val pressure: Int,
    @ColumnInfo(name = "temp")
    val temp: Double ,
    @ColumnInfo(name="uvi")
    val uvi: Double,
    // Individual fields for the first weather object
    @ColumnInfo(name = "weather_id")
    val weatherId: Int,
    @ColumnInfo(name = "weather_main")
    val weatherMain: String,
    @ColumnInfo(name = "weather_description")
    val weatherDescription: String,
    @ColumnInfo(name = "weather_icon")
    val weatherIcon: String,

    @ColumnInfo(name="wind_deg")
    val windDeg: Int,
    @ColumnInfo(name="wind_gust")
    val windGust: Double,
    @ColumnInfo(name="wind_speed")
    val windSpeed: Double

)
