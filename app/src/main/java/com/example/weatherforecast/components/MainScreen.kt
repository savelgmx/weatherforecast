package com.example.weatherforecast.components



import android.content.res.Resources.Theme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.weatherforecast.R
import com.example.weatherforecast.response.ForecastResponse
import com.example.weatherforecast.response.WeatherResponse
import com.example.weatherforecast.theme.AppTheme
import com.example.weatherforecast.theme.Blue300
import com.example.weatherforecast.theme.QuickSandTypography
import com.example.weatherforecast.utils.Resource
import com.example.weatherforecast.utils.WeatherUtils
import com.example.weatherforecast.utils.WeatherUtils.Companion.WeatherHeader
import com.example.weatherforecast.utils.WeatherUtils.Companion.WeatherText
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    currentState: Resource<WeatherResponse>?,
    forecastState: Resource<ForecastResponse>?
) {
    val scaffoldState= rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val isLoading = currentState is Resource.Loading || forecastState is Resource.Loading
    val hasError = currentState is Resource.Error || forecastState is Resource.Error
    val weatherData = (currentState as? Resource.Success)?.data
    val forecastData = (forecastState as? Resource.Success)?.data

    AppTheme {
        Scaffold(
            scaffoldState=scaffoldState,
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
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary)
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
                } else if (weatherData != null && forecastData != null) {
                    item {
                        CurrentWeatherCard(weatherState = currentState)
                    }
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        WeatherHeader(text = context.resources.getString(R.string.weather_24_hour))
                    }
                    item {
                        forecastData.hourly?.let { hourlyWeatherList ->
                            val currentTime=System.currentTimeMillis()
                            val next24Hours = currentTime + (24 * 60 * 60 * 1000)
                            //filter list to display next 24 hours relatively to current time
                            //sort items by dt and take only first 24 items in list
                            val filteredHourlyWeatherList = hourlyWeatherList
                                .filter { it.dt * 1000L in currentTime..next24Hours }
                                .sortedBy { it.dt }
                                .take(24)

                            HourlyWeatherRow(filteredHourlyWeatherList)
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        WeatherHeader(text = context.resources.getString(R.string.weather_7_days))
                    }
                    item {
                        ForecastWeatherList(
                            forecastState = forecastState,
                            navController = navController
                        )
                    }

                    item{
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))
                        WeatherHeader(text = context.getString(R.string.daily_weather_forecast))
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    item{
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 3.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            weatherData.main?.humidity?.let { humidity ->
                                forecastData.current?.dewPoint?.let { dewPoint ->
                                    HumidityCard(humidity =humidity, dewPoint =dewPoint.toInt() )
                                }
                            }
                            weatherData.wind?.speed?.toInt()?.let { windSpeed ->
                                weatherData.wind.deg?.let { windDegree ->
                                    WindSpeedCard(speed = windSpeed, windDegree =windDegree )
                                }
                            }

                        }

                    }

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
                    item{
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 3.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            forecastData.daily[0].sunrise?.let { sunrise ->
                                forecastData.daily[0].sunset?.let { sunset ->
                                    val timeOfSunrise = WeatherUtils.updateTime(sunrise)
                                    val timeOfSunset = WeatherUtils.updateTime(sunset)
                                    val timeOfDawnAndDusk = WeatherUtils.calculateDawnAndDusk(sunrise, sunset)
                                    timeOfDawnAndDusk[0]?.let { dawn ->
                                        timeOfDawnAndDusk[1]?.let { dusk ->
                                            SunriseSunsetCard(
                                                sunrise = timeOfSunrise,
                                                sunset = timeOfSunset,
                                                dawn = dawn,
                                                dusk = dusk
                                            )
                                        }
                                    }
                                }
                            }
                            forecastData.daily?.get(0)?.moonPhase?.let { moonPhase ->
                                val moonRise = WeatherUtils.updateTime(forecastData.daily[0].moonrise)
                                val moonSet = WeatherUtils.updateTime(forecastData.daily[0].moonset)
                                MoonriseMoonsetCard(
                                    moonrise = moonRise,
                                    moonset = moonSet,
                                    moonPhase = moonPhase
                                )
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
        }
    }
}