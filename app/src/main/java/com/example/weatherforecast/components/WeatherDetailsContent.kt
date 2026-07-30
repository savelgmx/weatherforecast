package com.example.weatherforecast.components

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.weatherforecast.BuildConfig
import com.example.weatherforecast.R
import com.example.weatherforecast.data.remote.AirVisualPollution
import com.example.weatherforecast.domain.models.DailyWeather
import com.example.weatherforecast.domain.models.HourlyWeather
import com.example.weatherforecast.theme.QuickSandTypography
import com.example.weatherforecast.utils.Resource
import com.example.weatherforecast.utils.WeatherUtils
import com.example.weatherforecast.utils.WeatherUtils.Companion.WeatherHeader
import com.example.weatherforecast.utils.WeatherUtils.Companion.WeatherText

/**
 * Shared LazyListScope extension functions for MainScreen portrait/landscape content.
 *
 * Eliminates code duplication between the portrait single-column layout
 * and the landscape left-pane layout in MainScreen.
 *
 * @param isLandscape When true, the 15-day forecast block is SKIPPED
 *   (it lives in the right pane in landscape mode).
 *   When false, all items including the 15-day forecast are rendered.
 * @param navController Only used in portrait mode (for ForecastWeatherList navigation).
 */
fun LazyListScope.weatherMainItems(
    isLandscape: Boolean,
    hasError: Boolean,
    hasInternetError: Boolean,
    isStale: Boolean,
    weatherData: DailyWeather?,
    forecastData: List<DailyWeather>?,
    hourlyData: List<HourlyWeather>?,
    currentState: Resource<DailyWeather>?,
    forecastState: Resource<List<DailyWeather>>?,
    pollution: AirVisualPollution?,
    context: Context,
    navController: NavController
) {
        if (hasError) {
            item {
                WeatherText(
                    text = (currentState as? Resource.Error)?.msg
                        ?: (forecastState as? Resource.Error)?.msg
                        ?: "Error loading data",
                    style = QuickSandTypography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else if (hasInternetError) {
            item {
                WeatherText(
                    text = context.resources.getString(R.string.no_internet_connection),
                    style = QuickSandTypography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else if (weatherData != null && forecastData != null) {
            // ——— Stale data warning ———
            if (isStale) {
                item {
                    WeatherText(
                        text = context.resources.getString(R.string.data_is_stale),
                        style = QuickSandTypography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // ——— Current Weather Card ———
            item {
                hourlyData?.let { hourlyWeatherList ->
                    val currentTime = System.currentTimeMillis()
                    val nextHour = currentTime + (60 * 60 * 1000)
                    val filteredCurrentWeatherList = hourlyWeatherList
                        .filter { it.dt * 1000L in currentTime..nextHour }
                        .sortedBy { it.dt }
                        .take(1)
                    if (BuildConfig.DEBUG) {
                        android.util.Log.d("current weather response", filteredCurrentWeatherList.toString())
                    }
                    CurrentWeatherCard(weatherState = requireNotNull(currentState), filteredCurrentWeatherList)
                }
            }

            // ——— 24-hour forecast ———
            item {
                Spacer(modifier = Modifier.height(8.dp))
                WeatherHeader(text = context.resources.getString(R.string.weather_24_hour))
            }
            item {
                hourlyData?.let { hourlyWeatherList ->
                    val filteredHourlyWeatherList = WeatherUtils.filterNext24Hours(
                        hourlyList = hourlyWeatherList,
                        timezone = ""
                    )
                    HourlyWeatherRow(filteredHourlyWeatherList, "")
                }
            }

            // ——— 15-day forecast (only in portrait — landscape puts this in the right pane) ———
            if (!isLandscape) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    WeatherHeader(text = context.resources.getString(R.string.weather_15_days))
                }
                item {
                    ForecastWeatherList(
                        dailyForecastList = forecastData,
                        navController = navController
                    )
                }
            }

            // ——— Daily: Sunrise / Sunset ———
            item {
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                WeatherHeader(text = context.getString(R.string.daily_weather_forecast))
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 3.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    val daily = forecastData.firstOrNull()
                    if (daily != null) {
                        val timeOfSunrise = WeatherUtils.updateTime(daily.sunrise.toInt(), "")
                        val timeOfSunset = WeatherUtils.updateTime(daily.sunset.toInt(), "")
                        SunriseSunsetArcCard(
                            sunrise = timeOfSunrise,
                            sunset = timeOfSunset,
                            timezone = ""
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // ——— Humidity + Wind Speed ———
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 3.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    HumidityCard(humidity = weatherData.humidity, dewPoint = weatherData.dew.toInt())
                    WindSpeedCard(speed = weatherData.windSpeed.toInt(), windDegree = weatherData.windDeg)
                }
            }

            // ——— UV Index + Pressure ———
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 3.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    UVIndexCard(index = weatherData.uvindex)
                    PressureCard(pressure = weatherData.pressure.toInt())
                }
            }

            // ——— Air Quality + Moon Phase ———
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 3.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    pollution?.let { AirQualityCard(pollution = it) }
                    forecastData.firstOrNull()?.let { daily ->
                        MoonriseMoonsetCard(moonPhase = daily.moonPhase)
                    }
                }
            }
        } else {
            item {
                WeatherText(
                    text = "Loading...",
                    style = QuickSandTypography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
