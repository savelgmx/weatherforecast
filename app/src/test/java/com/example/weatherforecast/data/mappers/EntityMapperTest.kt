package com.example.weatherforecast.data.mappers

import com.example.weatherforecast.data.db.DailyWeatherEntity
import com.example.weatherforecast.data.db.HourlyWeatherEntity
import com.example.weatherforecast.domain.models.HourlyWeather
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [EntityMapper] (review item 6).
 *
 * Room stores hourly data in a separate `hourly_weather` table, so the repository
 * restores it with [EntityMapper.toHourlyWeather] and passes it back into
 * [EntityMapper.toDailyWeather]. These tests lock that round-trip so the UI never
 * receives a `hours == null` forecast straight from the cache.
 */
class EntityMapperTest {

    private fun hourlyEntity(id: Int, dailyId: Int) = HourlyWeatherEntity(
        id = id,
        dailyId = dailyId,
        time = "12:00:00",
        dt = 1708797600L,
        temp = 15.0,
        feelsLike = 13.0,
        pressure = 1013.0,
        humidity = 60,
        windSpeed = 4.5,
        windDeg = 200,
        cloudiness = 50,
        description = "Partly cloudy",
        icon = "02d"
    )

    private val dailyEntity = DailyWeatherEntity(
        id = 7,
        dew = 5.0,                       // point of dew (точка росы)
        uvindex = 3,                     // UV index (УФ индекс)
        date = "2025-04-03",
        dt = 1708754400L,
        temp = 15.0,
        feelsLike = 13.0,
        tempMin = 10.0,
        tempMax = 20.0,
        pressure = 1013.0,
        humidity = 60,
        windSpeed = 4.5,
        windDeg = 200,
        cloudiness = 50,
        description = "Partly cloudy",
        icon = "02d",
        sunrise = 1708736117L,
        sunset = 1708772966L,
        moonPhase = 0.5,
        visibility = 10000.0,
        cityName = "London",
        timezone = "Europe/London",
        latitude = 51.5,
        longitude = -0.13
    )

    /**
     * [EntityMapper.toHourlyWeather] must copy every one of the 11 entity fields
     * 1:1 into the domain model — a single dropped field would silently corrupt
     * the hourly forecast rendered from the cache.
     */
    @Test
    fun toHourlyWeather_mapsEveryField() {
        val entity = hourlyEntity(id = 1, dailyId = 7)

        val result = EntityMapper.toHourlyWeather(entity)

        assertEquals("12:00:00", result.time)
        assertEquals(1708797600L, result.dt)
        assertEquals(15.0, result.temp, 0.0)
        assertEquals(13.0, result.feelsLike, 0.0)
        assertEquals(1013.0, result.pressure, 0.0)
        assertEquals(60, result.humidity)
        assertEquals(4.5, result.windSpeed, 0.0)
        assertEquals(200, result.windDeg)
        assertEquals(50, result.cloudiness)
        assertEquals("Partly cloudy", result.description)
        assertEquals("02d", result.icon)
    }

    /**
     * [EntityMapper.toDailyWeather] maps the full daily entity AND restores the
     * supplied [hours] list into the result — `hours` must equal the input list
     * so the cache round-trip is lossless.
     */
    @Test
    fun toDailyWeather_mapsEntityAndRestoresHours() {
        val inputHours = listOf(
            EntityMapper.toHourlyWeather(hourlyEntity(id = 1, dailyId = 7)),
            EntityMapper.toHourlyWeather(hourlyEntity(id = 2, dailyId = 7))
        )

        val result = EntityMapper.toDailyWeather(dailyEntity, inputHours)

        // Top-level daily fields, mapped 1:1.
        assertEquals(5.0, result.dew, 0.0)
        assertEquals(3, result.uvindex)
        assertEquals("2025-04-03", result.date)
        assertEquals(1708754400L, result.dt)
        assertEquals(15.0, result.temp, 0.0)
        assertEquals(10000.0, result.visibility, 0.0)
        assertEquals(13.0, result.feelsLike, 0.0)
        assertEquals(10.0, result.tempMin, 0.0)
        assertEquals(20.0, result.tempMax, 0.0)
        assertEquals(1013.0, result.pressure, 0.0)
        assertEquals(60, result.humidity)
        assertEquals(4.5, result.windSpeed, 0.0)
        assertEquals(200, result.windDeg)
        assertEquals(50, result.cloudiness)
        assertEquals("Partly cloudy", result.description)
        assertEquals("02d", result.icon)
        assertEquals(1708736117L, result.sunrise)
        assertEquals(1708772966L, result.sunset)
        assertEquals(0.5, result.moonPhase, 0.0)
        assertEquals("Europe/London", result.timezone)
        assertEquals(51.5, result.latitude, 0.0)
        assertEquals(-0.13, result.longitude, 0.0)

        // The hourly data must survive the DB read — it is never dropped to null.
        assertNotNull(result.hours)
        assertEquals(inputHours, result.hours)
        assertEquals(2, result.hours?.size)
    }

    /**
     * A day that has no hourly rows must map to `hours = emptyList()`, NOT null.
     * `DailyWeather.hours` is nullable, but a cache read must never produce a null
     * forecast: the UI iterates the list unconditionally.
     */
    @Test
    fun toDailyWeather_withEmptyHours_returnsEmptyListInsteadOfNull() {
        val result = EntityMapper.toDailyWeather(dailyEntity, emptyList())

        assertEquals(emptyList<HourlyWeather>(), result.hours)
        assertTrue(result.hours is List<HourlyWeather>)
    }

    /**
     * Multiple hours map 1:1 through [EntityMapper.toHourlyWeather], preserving
     * order (the UI's 24h list depends on the cache order).
     */
    @Test
    fun toDailyWeather_preservesHourlyOrder() {
        val hours = (1..24).map { i ->
            EntityMapper.toHourlyWeather(
                hourlyEntity(id = i, dailyId = 7).copy(dt = 1708797600L + i * 3600L, time = "$i:00")
            )
        }

        val result = EntityMapper.toDailyWeather(dailyEntity, hours)

        assertEquals(24, result.hours?.size)
        assertEquals(hours, result.hours)
        assertEquals(1708797600L + 3600L, result.hours?.get(0)?.dt)
        assertEquals(1708797600L + 24 * 3600L, result.hours?.last()?.dt)
    }
}
