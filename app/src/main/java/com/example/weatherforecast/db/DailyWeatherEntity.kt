package com.example.weatherforecast.db

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.weatherforecast.response.FeelsLike
import com.example.weatherforecast.response.Temp

@Entity(tableName = "daily_weather")
@TypeConverters(Converters::class)
data class DailyWeatherEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name="clouds")
    val clouds: Int,
    @ColumnInfo(name="dew_point")
    val dewPoint: Double,
    @ColumnInfo(name="dt")
    val dt: Int,
    @Embedded(prefix = "feelsLike_")
    val feelsLike: FeelsLike= FeelsLike(0.0,0.0,0.0,0.0),
    @ColumnInfo(name="humidity")
    val humidity: Int=0,
    @ColumnInfo(name="moon_phase")
    val moonPhase: Double=0.0,
    @ColumnInfo(name="moonrise")
    val moonrise: Int,
    @ColumnInfo(name="moonset")
    val moonset: Int,
    @ColumnInfo(name="pressure")
    val pressure: Int,
    @ColumnInfo(name="sunrise")
    val sunrise: Int,
    @ColumnInfo(name="sunset")
    val sunset: Int,
    @Embedded(prefix = "temp_")
    val temp: Temp=Temp(0.0,0.0,0.0,0.0,0.0,0.0),
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