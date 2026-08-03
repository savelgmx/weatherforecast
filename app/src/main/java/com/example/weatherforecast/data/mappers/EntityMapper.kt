package com.example.weatherforecast.data.mappers

import com.example.weatherforecast.data.db.DailyWeatherEntity
import com.example.weatherforecast.data.db.HourlyWeatherEntity
import com.example.weatherforecast.domain.models.DailyWeather
import com.example.weatherforecast.domain.models.HourlyWeather

/**
 * Maps Room entities to the domain models.
 *
 * Hourly data is stored in a separate table (`hourly_weather`), so converting a
 * [DailyWeatherEntity] back to a [DailyWeather] requires the caller to supply the
 * restored [hours] list — it is no longer dropped (`hours = null`) on every DB read.
 */
object EntityMapper {

    /**
     * Maps an [HourlyWeatherEntity] to its domain counterpart [HourlyWeather].
     * All 11 fields are copied 1:1.
     */
    fun toHourlyWeather(entity: HourlyWeatherEntity): HourlyWeather {
        return HourlyWeather(
            time = entity.time,
            dt = entity.dt,
            temp = entity.temp,
            feelsLike = entity.feelsLike,
            pressure = entity.pressure,
            humidity = entity.humidity,
            windSpeed = entity.windSpeed,
            windDeg = entity.windDeg,
            cloudiness = entity.cloudiness,
            description = entity.description,
            icon = entity.icon
        )
    }

    /**
     * Maps a [DailyWeatherEntity] plus its (previously-mapped) [hours] to a [DailyWeather].
     * The hourly data is restored here instead of being set to null, so the UI receives
     * the full per-hour forecast straight from the cache.
     */
    fun toDailyWeather(entity: DailyWeatherEntity, hours: List<HourlyWeather>): DailyWeather {
        return DailyWeather(
            dew = entity.dew,              // point of dew (точка росы)
            uvindex = entity.uvindex, // UV index (УФ индекс)
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
            hours = hours, // hourly data restored from the Room cache
            timezone = entity.timezone,
            latitude = entity.latitude,
            longitude = entity.longitude
        )
    }
}