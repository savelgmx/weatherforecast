package com.example.weatherforecast.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.weatherforecast.R
import com.example.weatherforecast.data.remote.AirVisualPollution
import com.example.weatherforecast.domain.models.HourlyWeather
import com.example.weatherforecast.domain.models.DailyWeather
import com.example.weatherforecast.theme.WeatherTheme
import com.example.weatherforecast.utils.Resource
import com.example.weatherforecast.utils.WeatherComposables.WeatherHeader
import com.example.weatherforecast.utils.WeatherComposables.WeatherText
import kotlinx.coroutines.launch


@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
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
    // Material 3: вместо M2 rememberScaffoldState используем rememberDrawerState —
    // состояние выдвижной панели, которой управляет ModalNavigationDrawer (см. ниже).
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val isLoading = currentState is Resource.Loading || forecastState is Resource.Loading
    val hasError = currentState is Resource.Error || forecastState is Resource.Error
    val hasInternetError = currentState is Resource.Internet && forecastState is Resource.Internet
    val isStale = (currentState as? Resource.Success)?.isStale == true || (forecastState as? Resource.Success)?.isStale == true
    val weatherData = (currentState as? Resource.Success)?.data
    val forecastData = (forecastState as? Resource.Success)?.data
    // ⚠ Bug #1 fix: hourlyData вычисляется через flatMap вместо присвоения null.
    // Ранее здесь было "= null" — из-за этого HourlyWeatherRow не получал данные
    // и не отображал почасовой прогноз. flatMap безопасно разворачивает
    // List<DailyWeather> в List<HourlyWeather>, используя Elvis-оператор для null-поля hours.
    val hourlyData: List<HourlyWeather>? = forecastData?.flatMap { it.hours ?: emptyList() }

    // ——— Material 3: drawer вынесен из Scaffold в ModalNavigationDrawer ———
        // M2 Scaffold имел параметры drawerContent / drawerElevation / scaffoldState.
        // В Material 3 их НЕТ — Scaffold стал проще, а drawer реализуется отдельным
        // компонентом ModalNavigationDrawer + ModalDrawerSheet. Внешний вид и поведение
        // (открытие по кнопке-меню, swipe-жест с края) сохранены полностью.
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                // ——— Drawer: отдельная группа токенов drawerSurface/onDrawerSurface ———
                // Не равен surface/onSurface: тёмная тема — тёмно-серый фон (#1E1E1E)/
                // белый текст; светлая — белый фон/чёрный текст.
                Surface(
                    color = WeatherTheme.tokens.drawerSurface,
                    contentColor = WeatherTheme.tokens.onDrawerSurface
                ) {
                    DrawerContent(navController = navController)
                }
            }
        ) {
            Scaffold(
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
                                    drawerState.open()
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
                }
            ) { paddingValues ->
                // ——— Pull-to-refresh: hoisted to a single box wrapping BoxWithConstraints ———
                // Один PullToRefreshBox (а не по одному внутри каждой ветки isLandscape).
                // BoxWithConstraints внутри определяет ориентацию: maxWidth > maxHeight  → landscape
                // (две панели, Row 50%/50%), иначе portrait (оригинальный одноколоночный LazyColumn).
                PullToRefreshBox(
                    isRefreshing = isLoading,
                    onRefresh = onRefresh,
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    BoxWithConstraints(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val isLandscape = maxWidth > maxHeight
                        if (isLandscape) {
                            // ——— LANDSCAPE: двухпанельный режим (split-pane) ———
                            // Левая  (weight 0.5f): CurrentWeatherCard + 24-hour почасовой прогноз + детали
                            //                        (Sunrise/Sunset, Humidity/Wind, UV/Pressure, AirQuality/MoonPhase)
                            // Правая (weight 0.5f): 15-day forecast
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
                        } else {
                            // ——— PORTRAIT: однопанельный режим ———
                            // Тот же контент, но в одной LazyColumn. isLandscape=false
                            // включает 15-дневный прогноз (который в landscape уходит в правую панель).
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
