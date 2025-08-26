package com.example.weatherforecast.data.mappers

import com.example.weatherforecast.data.db.DailyWeatherEntity
import com.example.weatherforecast.domain.models.DailyWeather


object EntityMapper {
    fun toDailyWeather(entity: DailyWeatherEntity): DailyWeather {
        return DailyWeather(
             dew=entity.dew,              //point of dew (точка росы)
            uvindex=entity.uvindex,             //UV index (УФ индекс)
            date = entity.date,
            dt = entity.dt,
            temp = entity.temp,
            visibility = entity.visibility,
            feelsLike = entity.feelsLike,
            tempMin = entity.tempMin,
            tempMax = entity.tempMax,
            pressure = entity.pressure,
            humidity = entity.humidity,
            windSpeed = entity.windSpeed,
            windDeg = entity.windDeg,
            cloudiness = entity.cloudiness,
            description = entity.description,
            icon = entity.icon,
            sunrise = entity.sunrise,
            sunset = entity.sunset,
            moonPhase = entity.moonPhase,
            hours = null, // Почасовые данные хранятся отдельно
            timezone = entity.timezone,//propogate timezone
            tzOffset = entity.tzOffset //propogate tzOffset
        )
    }
}