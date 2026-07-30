package com.example.weatherforecast.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import com.example.weatherforecast.R
import com.example.weatherforecast.data.remote.AirVisualPollution
import com.example.weatherforecast.domain.models.HourlyWeather
import com.example.weatherforecast.domain.models.DailyWeather
import com.example.weatherforecast.theme.AppTheme
import com.example.weatherforecast.utils.Resource
import com.example.weatherforecast.utils.WeatherUtils.Companion.WeatherHeader
import com.example.weatherforecast.utils.WeatherUtils.Companion.WeatherText
import kotlinx.coroutines.launch

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
    navController: NavController,
    currentState: Resource<DailyWeather>?,
    forecastState: Resource<List<DailyWeather>>?,
    onRefresh: () -> Unit,
    cityName: String = "",
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
    val hasInternetError = currentState is Resource.Internet && forecastState is Resource.Internet
    val isStale = (currentState as? Resource.Success)?.isStale == true || (forecastState as? Resource.Success)?.isStale == true
    val weatherData = (currentState as? Resource.Success)?.data
    val forecastData = (forecastState as? Resource.Success)?.data
    val hourlyData: List<HourlyWeather>? = null

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
                            text = cityName.ifBlank { "Loading..." },
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
                DrawerContent(navController = navController)
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
                    // Левая  (weight 0.5f): CurrentWeatherCard + 24-hour почасовой прогноз + детали
                    //                        (Sunrise/Sunset, Humidity/Wind, UV/Pressure, AirQuality/MoonPhase)
                    // Правая (weight 0.5f): 15-day forecast
                    // PullRefreshIndicator один на обе панели — поверх Row по TopCenter
                    Box(
                        modifier = Modifier
                            .pullRefresh(refreshState)
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Row(modifier = Modifier.fillMaxSize()) {
                            // ——— Left pane: CurrentWeather + почасовой прогноз + детали ———
                            LazyColumn(
                                modifier = Modifier
                                    .weight(0.5f)
                                    .fillMaxHeight()
                            ) {
                                weatherMainItems(
                                    isLandscape = true,
                                    hasError = hasError,
                                    hasInternetError = hasInternetError,
                                    isStale = isStale,
                                    weatherData = weatherData,
                                    forecastData = forecastData,
                                    hourlyData = hourlyData,
                                    currentState = currentState,
                                    forecastState = forecastState,
                                    pollution = pollution,
                                    context = context,
                                    navController = navController
                                )
                            }

                            // ——— Vertical divider ———
                            VerticalDivider(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(1.dp)
                            )

                            // ——— Right pane: 15-day прогноз ———
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
                                            dailyForecastList = forecastData,
                                            navController = navController
                                        )
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
                    // ——— PORTRAIT: однопанельный режим ———
                    // Тот же контент, но в одной LazyColumn. isLandscape=false
                    // включает 15-дневный прогноз (который в landscape уходит в правую панель).
                    Box(
                        modifier = Modifier
                            .pullRefresh(refreshState)
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            weatherMainItems(
                                isLandscape = false,
                                hasError = hasError,
                                hasInternetError = hasInternetError,
                                isStale = isStale,
                                weatherData = weatherData,
                                forecastData = forecastData,
                                hourlyData = hourlyData,
                                currentState = currentState,
                                forecastState = forecastState,
                                pollution = pollution,
                                context = context,
                                navController = navController
                            )
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
