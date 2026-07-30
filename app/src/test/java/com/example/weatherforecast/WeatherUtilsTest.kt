package com.example.weatherforecast

import com.example.weatherforecast.domain.models.HourlyWeather
import com.example.weatherforecast.utils.WeatherUtils
import org.junit.Assert.assertEquals
import org.junit.Test

class WeatherUtilsTest {

    @Test
    fun updateTemperature_switchStateTrue_returnsCelsius() {
        val result = WeatherUtils.updateTemperature(25, true)
        assertEquals("25C° ", result)
    }

    @Test
    fun updateTemperature_switchStateFalse_returnsFahrenheit() {
        val result = WeatherUtils.updateTemperature(25, false)
        // (25 * 9/5) + 32 = 77
        assertEquals("77F° ", result)
    }

    @Test
    fun updateTemperature_zeroCelsius_returns32Fahrenheit() {
        val result = WeatherUtils.updateTemperature(0, false)
        assertEquals("32F° ", result)
    }

    @Test
    fun updateTemperature_negativeCelsius_returnsCorrectFahrenheit() {
        val result = WeatherUtils.updateTemperature(-10, false)
        // (-10 * 9/5) + 32 = 14
        assertEquals("14F° ", result)
    }

    @Test
    fun convertWindSpeed_defaultUnit_returnsKmh() {
        val result = WeatherUtils.convertWindSpeed(10, 0)
        assertEquals("10", result)
    }

    @Test
    fun convertWindSpeed_ms_convertsCorrectly() {
        val result = WeatherUtils.convertWindSpeed(36, 1)
        // 36 / 3.6 = 10
        assertEquals("10", result)
    }

    @Test
    fun convertWindSpeed_knots_convertsCorrectly() {
        val result = WeatherUtils.convertWindSpeed(10, 2)
        // 10 * 0.587 = 5 (toInt)
        assertEquals("5", result)
    }

    @Test
    fun convertWindSpeed_fts_convertsCorrectly() {
        val result = WeatherUtils.convertWindSpeed(10, 3)
        // 10 * 0.91 = 9 (toInt)
        assertEquals("9", result)
    }

    @Test
    fun updateDateToToday_withValidTimestamp_returnsFormattedDate() {
        // 2025-04-03 00:00:00 UTC = 1743638400
        val result = WeatherUtils.updateDateToToday(1743638400)
        // The exact format depends on locale, but should contain the date
        org.junit.Assert.assertNotNull(result)
        org.junit.Assert.assertTrue(result.contains("2025") || result.contains("April") || result.contains("Apr"))
    }

    @Test
    fun updateDateToToday_withNull_returnsToday() {
        val result = WeatherUtils.updateDateToToday(null)
        assertEquals("Today", result)
    }

    @Test
    fun updateTime_withValidEpoch_returnsFormattedTime() {
        // 1708754400 = 2024-02-24 06:00:00 UTC
        // The exact output depends on timezone, but should be non-empty
        val result = WeatherUtils.updateTime(1708754400, "UTC")
        assertEquals("06:00", result)
    }

    @Test
    fun updateTime_withNullEpoch_returnsDefault() {
        val result = WeatherUtils.updateTime(null, "UTC")
        assertEquals("--:--", result)
    }

    @Test
    fun filterNext24Hours_returnsAtMost24Items() {
        val now = 1708754400L // 2024-02-24 06:00:00 UTC
        val hourly = (0..47).map { i ->
            HourlyWeather(
                time = "${i}:00",
                dt = now + i * 3600L,
                temp = 20.0 + i,
                feelsLike = 18.0 + i,
                pressure = 1013.0,
                humidity = 50,
                windSpeed = 5.0,
                windDeg = 180,
                cloudiness = 0,
                description = "Clear",
                icon = "01d"
            )
        }

        val result = WeatherUtils.filterNext24Hours(hourly, "UTC", now)

        assertEquals(24, result.size)
        assertEquals(now, result.first().dt)
        assertEquals(now + 23 * 3600L, result.last().dt)
    }

    @Test
    fun calculateDayDuration_returnsNonEmpty() {
        val result = WeatherUtils.calculateDayDurationElapsedDayTimeAndSunIconProgress(
            "06:00", "18:00", "UTC"
        )
        assertEquals(3, result.size)
        // 12 hours = 12:00
        org.junit.Assert.assertTrue(result[0].startsWith("12:"))
    }

}
