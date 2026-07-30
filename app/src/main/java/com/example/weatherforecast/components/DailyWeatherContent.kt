package com.example.weatherforecast.components

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherforecast.R
import com.example.weatherforecast.domain.models.DailyWeather
import com.example.weatherforecast.domain.models.HourlyWeather
import com.example.weatherforecast.utils.WeatherUtils
import com.example.weatherforecast.utils.WeatherUtils.Companion.WeatherHeader

/**
 * Shared LazyListScope extension functions for DailyWeatherForecast portrait/landscape content.
 *
 * Eliminates code duplication between:
 * - Landscape left pane  (DailyWeatherCard + hourly forecast)
 * - Landscape right pane (detail cards: Humidity/Wind, UV/Pressure, Sunrise/Sunset/MoonPhase)
 * - Portrait single-column (all of the above)
 */

/**
 * Left-pane items: DailyWeatherCard + 24-hour forecast row.
 * Used in both portrait (single LazyColumn) and landscape left pane.
 */
fun LazyListScope.dailyWeatherLeftItems(
    daily: DailyWeather,
    hourlyList: List<HourlyWeather>,
    timeZone: String,
    context: Context
) {
    item {
        DailyWeatherCard(daily = daily)
    }
    item {
        Spacer(modifier = Modifier.height(8.dp))
        WeatherHeader(text = context.resources.getString(R.string.weather_24_hour))
    }
    item {
        val filteredHourlyWeatherList =
            WeatherUtils.filterNext24Hours(hourlyList = hourlyList, timezone = timeZone, startEpochSeconds = daily.dt)
        HourlyWeatherRow(filteredHourlyWeatherList, timeZone)
    }
}

/**
 * Right-pane items: Humidity/Wind, UV/Pressure, Sunrise/Sunset/MoonPhase detail cards.
 * Used in both portrait (single LazyColumn) and landscape right pane.
 */
fun LazyListScope.dailyWeatherRightItems(
    daily: DailyWeather,
    timeZone: String
) {
    item {
        Row(
            modifier = Modifier
                .padding(all = 3.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            HumidityCard(humidity = daily.humidity, dewPoint = daily.dew.toInt())
            WindSpeedCard(speed = daily.windSpeed.toInt(), windDegree = daily.windDeg)
        }
    }
    item {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 5.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            UVIndexCard(index = daily.uvindex)
            PressureCard(pressure = daily.pressure)
        }
    }
    item {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 3.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            val timeOfDawn = daily.sunrise
            val timeOfDusk = daily.sunset
            val timeOfDawnAndDusk = WeatherUtils.calculateDawnAndDusk(timeOfDawn, timeOfDusk)
            timeOfDawnAndDusk[0]?.let { dawn ->
                timeOfDawnAndDusk[1]?.let { dusk ->
                    SunriseSunsetCard(
                        sunrise = WeatherUtils.updateTime(daily.sunrise.toInt(), timeZone),
                        sunset = WeatherUtils.updateTime(daily.sunset.toInt(), timeZone),
                        dawn = dawn,
                        dusk = dusk,
                        timeZone
                    )
                }
            }
            MoonriseMoonsetCard(moonPhase = daily.moonPhase)
        }
    }
}
