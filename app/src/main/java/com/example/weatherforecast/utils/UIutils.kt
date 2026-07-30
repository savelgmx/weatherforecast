package com.example.weatherforecast.utils

import com.example.weatherforecast.domain.models.DailyWeather
import com.example.weatherforecast.domain.models.HourlyWeather

class UIUtils {
    companion object {
        val iconurl = AppConstants.WEATHER_API_IMAGE_ENDPOINT

        fun getMockDailyWeather(): DailyWeather {
            return DailyWeather(
                dew = 1.37,
                uvindex = 1,
                date = "2025-04-03",
                dt = 1708754400L,
                temp = -18.32,
                feelsLike = -19.14,
                tempMin = -27.53,
                tempMax = -16.77,
                pressure = 1036.0,
                visibility = 10000.0,
                humidity = 73,
                windSpeed = 2.2,
                windDeg = 224,
                cloudiness = 100,
                description = "пасмурно",
                icon = "04n",
                sunrise = 1708736117L,
                sunset = 1708772966L,
                moonPhase = 0.5,
                hours = null,
                timezone = "Asia/Krasnoyarsk",
                latitude = 56.0097,
                longitude = 92.79
            )
        }

        fun getMockHourlylist(): List<HourlyWeather> {
            return listOf(
                HourlyWeather(
                    time = "12:00:00",
                    dt = 1708774497L,
                    temp = -18.32,
                    feelsLike = -17.78,
                    pressure = 1037.0,
                    humidity = 95,
                    windSpeed = 2.2,
                    windDeg = 223,
                    cloudiness = 99,
                    description = "пасмурно",
                    icon = "04n"
                ),
                HourlyWeather(
                    time = "13:00:00",
                    dt = 1708778097L,
                    temp = -17.78,
                    feelsLike = -17.78,
                    pressure = 1036.0,
                    humidity = 94,
                    windSpeed = 2.2,
                    windDeg = 223,
                    cloudiness = 99,
                    description = "пасмурно",
                    icon = "04n"
                ),
                HourlyWeather(
                    time = "14:00:00",
                    dt = 1708781697L,
                    temp = -17.78,
                    feelsLike = -17.78,
                    pressure = 1036.0,
                    humidity = 94,
                    windSpeed = 2.2,
                    windDeg = 223,
                    cloudiness = 99,
                    description = "пасмурно",
                    icon = "04n"
                )
            )
        }
    }
}
