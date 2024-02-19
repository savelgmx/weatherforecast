package com.example.weatherforecast.db

import androidx.room.TypeConverter
import com.example.weatherforecast.response.Clouds
import com.example.weatherforecast.response.Coord
import com.example.weatherforecast.response.Main
import com.example.weatherforecast.response.Sys
import com.example.weatherforecast.response.Weather
import com.example.weatherforecast.response.Wind
import com.google.gson.Gson

class Converters {
    // Implement similar methods for other nested data classes
    //  (Main, Coord, Wind, Clouds, Sys)
    @TypeConverter
    fun fromMain(main: Main): String {
        return Gson().toJson(main)
    }
    @TypeConverter
    fun toMain(mainString: String): Main {
        return Gson().fromJson(mainString, Main::class.java)
    }

    @TypeConverter
    fun fromCoord(coord: Coord): String {
        return Gson().toJson(coord)
    }
    @TypeConverter
    fun toCoord(coordString: String): Coord {
        return Gson().fromJson(coordString, Coord::class.java)
    }
    @TypeConverter
    fun fromWind(wind: Wind): String {
        return Gson().toJson(wind)
    }
    @TypeConverter
    fun toWind(windString: String): Wind {
        return Gson().fromJson(windString, Wind::class.java)
    }

    @TypeConverter
    fun fromClouds(clouds: Clouds): String {
        return Gson().toJson(clouds)
    }
    @TypeConverter
    fun toCloud(cloudsString: String): Clouds {
        return Gson().fromJson(cloudsString, Clouds::class.java)
    }

    @TypeConverter
    fun fromSys(sys: Sys): String {
        return Gson().toJson(sys)
    }
    @TypeConverter
    fun tosys(sysString: String): Sys {
        return Gson().fromJson(sysString, Sys::class.java)
    }

    @TypeConverter
    fun fromWeatherList(value: String): List<Weather> {
        val items = value.split(",")
        return items.map {
            val parts = it.split("|")
            Weather(parts[0].toInt(), parts[1], parts[2], parts[3])
        }
    }

    @TypeConverter
    fun toWeatherList(value: List<Weather>): String {
        return value.joinToString(",") {
            "${it.id}|${it.main}|${it.description}|${it.icon}"
        }
    }

    @TypeConverter
    fun fromWeatherEntityItem(value: String): Weather {
        val parts = value.split("|")
        return Weather(parts[0].toInt(), parts[1], parts[2], parts[3])
    }

    @TypeConverter
    fun toWeather(value: Weather): String {
        return "${value.id}|${value.main}|${value.description}|${value.icon}"
    }
}
