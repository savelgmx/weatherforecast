package com.example.weatherforecast.db

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromWeatherList(value: String): List<WeatherEntityItem> {
        val items = value.split(",")
        return items.map {
            val parts = it.split("|")
            WeatherEntityItem(parts[0].toInt(), parts[1], parts[2], parts[3])
        }
    }

    @TypeConverter
    fun toWeatherList(value: List<WeatherEntityItem>): String {
        return value.joinToString(",") {
            "${it.weatherId}|${it.main}|${it.description}|${it.icon}"
        }
    }

    @TypeConverter
    fun fromWeatherEntityItem(value: String): WeatherEntityItem {
        val parts = value.split("|")
        return WeatherEntityItem(parts[0].toInt(), parts[1], parts[2], parts[3])
    }

    @TypeConverter
    fun toWeatherEntityItem(value: WeatherEntityItem): String {
        return "${value.weatherId}|${value.main}|${value.description}|${value.icon}"
    }
}
