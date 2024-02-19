package com.example.weatherforecast.db

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.weatherforecast.response.Clouds
import com.example.weatherforecast.response.Coord
import com.example.weatherforecast.response.Main
import com.example.weatherforecast.response.Sys
import com.example.weatherforecast.response.Weather
import com.example.weatherforecast.response.Wind

@Entity(tableName = "weather")
@TypeConverters(Converters::class)
data class CurrentWeatherEntity @JvmOverloads constructor(
    @Embedded
    val coord: Coord = Coord(0.0, 0.0),
    val weather: List<Weather> = emptyList(),
    val base: String = "",
    @Embedded
    val main: Main = Main(0.0, 0.0, 0.0, 0.0, 0, 0),
    val visibility: Int = 0,
    @Embedded
    val wind: Wind = Wind(0.0, 0),
    @Embedded
    val clouds: Clouds = Clouds(0),
    val dt: Long = 0L,
    @Embedded(prefix = "sys_")
    val sys: Sys = Sys(0, 0, "", 0L, 0L),
    val timezone: Int = 0,

    val name: String,
    val cod: Int
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

