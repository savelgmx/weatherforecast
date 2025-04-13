package com.example.weatherforecast.data.mappers

import com.example.weatherforecast.data.remote.ApiDay
import com.example.weatherforecast.data.remote.ApiHour
import com.example.weatherforecast.domain.models.DailyWeather
import com.example.weatherforecast.domain.models.HourlyWeather

object WeatherMapper {
    fun toDailyWeather(apiDay: ApiDay): DailyWeather {
        return DailyWeather(
            date = apiDay.date,
            temp = apiDay.temp,
            feelsLike = apiDay.feelsLike,
            tempMin = apiDay.tempMin,
            tempMax = apiDay.tempMax,
            pressure = apiDay.pressure,
            humidity = apiDay.humidity.toInt(),
            windSpeed = apiDay.windSpeed,
            windDeg = apiDay.windDir.toInt(),
            cloudiness = apiDay.cloudCover.toInt(),
            description = apiDay.conditions,
            icon = apiDay.icon,
            sunrise = apiDay.sunriseEpoch,
            sunset = apiDay.sunsetEpoch,
            hours = apiDay.hours?.map { toHourlyWeather(it) }
        )
    }

    fun toHourlyWeather(apiHour: ApiHour): HourlyWeather {
        return HourlyWeather(
            time = apiHour.time,
            temp = apiHour.temp,
            feelsLike = apiHour.feelsLike,
            pressure = apiHour.pressure,
            humidity = apiHour.humidity.toInt(),
            windSpeed = apiHour.windSpeed,
            windDeg = apiHour.windDir.toInt(),
            cloudiness = apiHour.cloudCover.toInt(),
            description = apiHour.conditions,
            icon = apiHour.icon
        )
    }


}