package com.example.weatherforecast.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.weatherforecast.BuildConfig
import com.example.weatherforecast.R
import com.example.weatherforecast.data.remote.AirVisualPollution
import com.example.weatherforecast.response.ForecastResponse
import com.example.weatherforecast.response.WeatherResponse
import com.example.weatherforecast.theme.AppTheme
import com.example.weatherforecast.theme.QuickSandTypography
import com.example.weatherforecast.utils.Resource
import com.example.weatherforecast.utils.WeatherUtils
import com.example.weatherforecast.utils.WeatherUtils.Companion.WeatherHeader
import com.example.weatherforecast.utils.WeatherUtils.Companion.WeatherText
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
    navController: NavController,
    currentState: Resource<WeatherResponse>?,
    forecastState: Resource<ForecastResponse>?,
    onRefresh: () -> Unit,
    showCitySelectionDialog: Boolean = false,
    onCitySelected: (String) -> Unit = {},
    onDismissCityDialog: () -> Unit = {},
    pollution: AirVisualPollution? = null
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val isLoading = currentState is Resource.Loading || forecastState is Resource.Loading
    val hasError = currentState is Resource.Error || forecastState is Resource.Error
    val hasInternetError = currentState is Resource.Internet || forecastState is Resource.Internet
    val isStale = (currentState as? Resource.Success)?.isStale == true || (forecastState as? Resource.Success)?.isStale == true
    val weatherData = (currentState as? Resource.Success)?.data
    val forecastData = (forecastState as? Resource.Success)?.data
    val hourlyData = (forecastState as? Resource.Success)?.data?.hourly

    val refreshState = rememberPullRefreshState(
        refreshing = isLoading,
        onRefresh = onRefresh
    )

    AppTheme {
        Scaffold(
            scaffoldState = scaffoldState,
            drawerElevation = 16.dp,
            topBar = {
                TopAppBar(
                    title = {
                        WeatherText(
                            text = weatherData?.name ?: "Loading...",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                scaffoldState.drawerState.open()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            drawerContent = {
                DrawerContent()
            },
            contentColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            // ——— BoxWithConstraints: автоопределение ориентации ———
            // maxWidth > maxHeight  → landscape  → две панели (Row: 50% / 50%)
            // maxWidth <= maxHeight → portrait   → оригинальный одноколоночный LazyColumn
            // Не используем ConfigurationChange или отдельный landscape layout —
            // BoxWithConstraints адаптируется на лету при изменении размеров.
            BoxWithConstraints(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                val isLandscape = maxWidth > maxHeight
                if (isLandscape) {
                    // ——— LANDSCAPE: двухпанельный режим (split-pane) ———
                    // Левая  (weight 0.5f): CurrentWeatherCard + 24-hour почасовой прогноз
                    // Правая (weight 0.5f): 15-day forecast + детали (Sunrise, Humidity,
                    //                        Wind, UV, Pressure, AirQuality, MoonPhase)
                    // PullRefreshIndicator один на обе панели — поверх Row по TopCenter
                    Box(
                        modifier = Modifier
                            .pullRefresh(refreshState)
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Row(modifier = Modifier.fillMaxSize()) {
                        // ——— Left pane: CurrentWeather + почасовой прогноз ———
                            LazyColumn(
                                modifier = Modifier
                                    .weight(0.5f)
                                    .fillMaxHeight()
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
                                    if (isStale) {
                                        item {
                                            WeatherText(
                                                text = context.resources.getString(R.string.data_is_stale),
                                                style = QuickSandTypography.bodyMedium,
                                                modifier = Modifier.padding(16.dp)
                                            )
                                        }
                                    }
                                    item {
                                        hourlyData?.let { hourlyWeatherList ->
                                            val currentTime = System.currentTimeMillis()
                                            val nextHour = currentTime + (60 * 60 * 1000)
                                            val filteredCurrentWeatherList = hourlyWeatherList
                                                .filter { it.dt * 1000L in currentTime..nextHour }
                                                .sortedBy { it.dt }
                                                .take(1)
                                            if (BuildConfig.DEBUG) {
                                                Log.d("current weather response", filteredCurrentWeatherList.toString())
                                            }
                                            CurrentWeatherCard(weatherState = currentState, filteredCurrentWeatherList)
                                        }
                                    }
                                    item {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        WeatherHeader(text = context.resources.getString(R.string.weather_24_hour))
                                    }
                                    item {
                                        forecastData.hourly?.let { hourlyWeatherList ->
                                            val filteredHourlyWeatherList = WeatherUtils.filterNext24Hours(
                                                hourlyList = hourlyWeatherList,
                                                timezone = forecastData.timezone
                                            )
                                            HourlyWeatherRow(filteredHourlyWeatherList, forecastData.timezone)
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

                            // ——— Vertical divider ———
                            VerticalDivider(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(1.dp)
                            )

                        // ——— Right pane: 15-day прогноз + детальные карточки ———
                            LazyColumn(
                                modifier = Modifier
                                    .weight(0.5f)
                                    .fillMaxHeight()
                            ) {
                                if (weatherData != null && forecastData != null) {
                                    item {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        WeatherHeader(text = context.resources.getString(R.string.weather_15_days))
                                    }
                                    item {
                                        ForecastWeatherList(
                                            forecastState = forecastState,
                                            navController = navController
                                        )
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
                                            forecastData.daily[0].sunrise?.let { sunrise ->
                                                forecastData.daily[0].sunset?.let { sunset ->
                                                    val timeOfSunrise = WeatherUtils.updateTime(sunrise, forecastData.timezone)
                                                    val timeOfSunset = WeatherUtils.updateTime(sunset, forecastData.timezone)
                                                    SunriseSunsetArcCard(
                                                        sunrise = timeOfSunrise,
                                                        sunset = timeOfSunset,
                                                        timezone = forecastData.timezone
                                                    )
                                                }
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
                                            weatherData.main?.humidity?.let { humidity ->
                                                forecastData.current?.dewPoint?.let { dewPoint ->
                                                    HumidityCard(humidity = humidity, dewPoint = dewPoint.toInt())
                                                }
                                            }
                                            weatherData.wind?.speed?.toInt()?.let { windSpeed ->
                                                weatherData.wind.deg?.let { windDegree ->
                                                    WindSpeedCard(speed = windSpeed, windDegree = windDegree)
                                                }
                                            }
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
                                            forecastData.current?.uvi?.let { uvIndex ->
                                                UVIndexCard(index = uvIndex.toInt())
                                            }
                                            weatherData.main?.pressure?.let { pressure ->
                                                PressureCard(pressure = pressure)
                                            }
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
                                            forecastData.daily?.get(0)?.moonPhase?.let { moonPhase ->
                                                MoonriseMoonsetCard(moonPhase = moonPhase)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        // PullRefreshIndicator для landscape — один на обе панели,
                        // выровнен по TopCenter внутри Box (над Row).
                        PullRefreshIndicator(
                            refreshing = isLoading,
                            state = refreshState,
                            modifier = Modifier.align(Alignment.TopCenter)
                        )
                    }
                } else {
                    // ——— PORTRAIT: однопанельный режим (оригинальный скролл) ———
                    // Тот же контент, но в одной LazyColumn — без изменений относительно
                    // исходной логики до добавления split-pane.
                    Box(
                        modifier = Modifier
                            .pullRefresh(refreshState)
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
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
                                if (isStale) {
                                    item {
                                        WeatherText(
                                            text = context.resources.getString(R.string.data_is_stale),
                                            style = QuickSandTypography.bodyMedium,
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }
                                }
                                item {
                                    hourlyData?.let { hourlyWeatherList ->
                                        val currentTime = System.currentTimeMillis()
                                        val nextHour = currentTime + (60 * 60 * 1000)
                                        val filteredCurrentWeatherList = hourlyWeatherList
                                            .filter { it.dt * 1000L in currentTime..nextHour }
                                            .sortedBy { it.dt }
                                            .take(1)
                                        if (BuildConfig.DEBUG) {
                                            Log.d("current weather response", filteredCurrentWeatherList.toString())
                                        }
                                        CurrentWeatherCard(weatherState = currentState, filteredCurrentWeatherList)
                                    }
                                }
                                item {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    WeatherHeader(text = context.resources.getString(R.string.weather_24_hour))
                                }
                                item {
                                    forecastData.hourly?.let { hourlyWeatherList ->
                                        val filteredHourlyWeatherList = WeatherUtils.filterNext24Hours(
                                            hourlyList = hourlyWeatherList,
                                            timezone = forecastData.timezone
                                        )
                                        HourlyWeatherRow(filteredHourlyWeatherList, forecastData.timezone)
                                    }
                                }
                                item {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    WeatherHeader(text = context.resources.getString(R.string.weather_15_days))
                                }
                                item {
                                    ForecastWeatherList(
                                        forecastState = forecastState,
                                        navController = navController
                                    )
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
                                        forecastData.daily[0].sunrise?.let { sunrise ->
                                            forecastData.daily[0].sunset?.let { sunset ->
                                                val timeOfSunrise = WeatherUtils.updateTime(sunrise, forecastData.timezone)
                                                val timeOfSunset = WeatherUtils.updateTime(sunset, forecastData.timezone)
                                                SunriseSunsetArcCard(
                                                    sunrise = timeOfSunrise,
                                                    sunset = timeOfSunset,
                                                    timezone = forecastData.timezone
                                                )
                                            }
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
                                        weatherData.main?.humidity?.let { humidity ->
                                            forecastData.current?.dewPoint?.let { dewPoint ->
                                                HumidityCard(humidity = humidity, dewPoint = dewPoint.toInt())
                                            }
                                        }
                                        weatherData.wind?.speed?.toInt()?.let { windSpeed ->
                                            weatherData.wind.deg?.let { windDegree ->
                                                WindSpeedCard(speed = windSpeed, windDegree = windDegree)
                                            }
                                        }
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
                                        forecastData.current?.uvi?.let { uvIndex ->
                                            UVIndexCard(index = uvIndex.toInt())
                                        }
                                        weatherData.main?.pressure?.let { pressure ->
                                            PressureCard(pressure = pressure)
                                        }
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
                                        forecastData.daily?.get(0)?.moonPhase?.let { moonPhase ->
                                            MoonriseMoonsetCard(moonPhase = moonPhase)
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
                        // PullRefreshIndicator для portrait — стандартный, над единственной
                        // LazyColumn. refreshState общий с landscape-режимом.
                        PullRefreshIndicator(
                            refreshing = isLoading,
                            state = refreshState,
                            modifier = Modifier.align(Alignment.TopCenter)
                        )
                    }
                }
            }

            // Show city selection dialog if needed
            if (showCitySelectionDialog) {
                CitySelectionDialog(
                    onCitySelected = onCitySelected,
                    onDismiss = onDismissCityDialog
                )
            }
        }
    }
}
