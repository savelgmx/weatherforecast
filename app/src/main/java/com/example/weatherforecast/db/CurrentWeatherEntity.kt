package com.example.weatherforecast.db

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "weather")
@TypeConverters(Converters::class)
data class CurrentWeatherEntity @JvmOverloads constructor(
    @Embedded
    val coord: CoordEntity = CoordEntity(0.0, 0.0),
    val weather: List<WeatherEntityItem> = emptyList(),
    val base: String = "",
    @Embedded
    val main: MainEntity = MainEntity(0.0, 0.0, 0.0, 0.0, 0, 0),
    val visibility: Int = 0,
    @Embedded
    val wind: WindEntity = WindEntity(0.0, 0),
    @Embedded
    val clouds: CloudsEntity = CloudsEntity(0),
    val dt: Long = 0L,
    @Embedded(prefix = "sys_")
    val sys: SysEntity = SysEntity(0, 0, 0.0, "", 0L, 0L),
    val timezone: Int = 0,

    val name: String,
    val cod: Int
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

data class CoordEntity(
    val lon: Double,
    val lat: Double
)

data class WeatherEntityItem(
    val weatherId: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class MainEntity(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val humidity: Int
)

data class WindEntity(
    val speed: Double,
    val deg: Int
)

data class CloudsEntity(
    val all: Int
)

data class SysEntity(
    val type: Int,
    val id: Int,
    val message: Double,
    val country: String,
    val sunrise: Long,
    val sunset: Long
)

