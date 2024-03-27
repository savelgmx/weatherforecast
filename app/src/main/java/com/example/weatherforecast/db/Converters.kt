package com.example.weatherforecast.db

import androidx.room.TypeConverter
import com.example.weatherforecast.response.Clouds
import com.example.weatherforecast.response.Coord
import com.example.weatherforecast.response.Current
import com.example.weatherforecast.response.Daily
import com.example.weatherforecast.response.Hourly
import com.example.weatherforecast.response.Main
import com.example.weatherforecast.response.Sys
import com.example.weatherforecast.response.Weather
import com.example.weatherforecast.response.Wind
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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
    fun toSys(sysString: String): Sys {
        return Gson().fromJson(sysString, Sys::class.java)
    }

    @TypeConverter
    fun fromWeatherList(value: List<Weather>): String {
        return value.joinToString(",") {
            "${it.id}|${it.main}|${it.description}|${it.icon}"
        }
    }

    @TypeConverter
    fun toWeatherList(value: String): List<Weather> {
        val items = value.split(",")
        return items.map {
            val parts = it.split("|")
            Weather(parts[0].toInt(), parts[1], parts[2], parts[3])
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


        @TypeConverter
        fun fromCurrent(current: Current): String {
            return Gson().toJson(current)
        }

        @TypeConverter
        fun toCurrent(currentString: String): Current {
            return Gson().fromJson(currentString, Current::class.java)
        }

        @TypeConverter
        fun fromDailyList(daily: List<Daily>): String {
            return Gson().toJson(daily)
        }

        @TypeConverter
        fun toDailyList(dailyString: String): List<Daily> {
            val listType = object : TypeToken<List<Daily>>() {}.type
            return Gson().fromJson(dailyString, listType)
        }

        @TypeConverter
        fun fromHourlyList(hourly: List<Hourly>): String {
            return Gson().toJson(hourly)
        }

        @TypeConverter
        fun toHourlyList(hourlyString: String): List<Hourly> {
            val listType = object : TypeToken<List<Hourly>>() {}.type
            return Gson().fromJson(hourlyString, listType)
        }

}
