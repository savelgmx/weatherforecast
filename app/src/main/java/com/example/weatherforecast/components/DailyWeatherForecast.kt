package com.example.weatherforecast.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.weatherforecast.R
import com.example.weatherforecast.response.Daily
import com.example.weatherforecast.response.Hourly
import com.example.weatherforecast.theme.Blue500
import com.example.weatherforecast.utils.WeatherUtils
import com.example.weatherforecast.utils.WeatherUtils.Companion.WeatherHeader
import com.google.accompanist.pager.ExperimentalPagerApi

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun DailyWeatherForecast(
    navController: NavController,
    dailyList: List<Daily>,
    hourlyList: List<Hourly>,
    startIndex: Int = 0,
    timeZone: String
) {
    val pagerState = rememberPagerState(pageCount = { dailyList.size }, initialPage = startIndex)
    val localContext = LocalContext.current
    val switchState by DataStoreManager.tempSwitchPrefFlow(localContext).collectAsState(initial = false)
    val timeZone=timeZone

    Scaffold(
        topBar = {
            val currentPage = pagerState.currentPage
            val currentDaily = dailyList.getOrNull(currentPage) ?: dailyList.firstOrNull() ?: return@Scaffold
            TopAppBar(
                title = { Text(text = WeatherUtils.updateDateToToday(currentDaily.dt)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        HorizontalPager(state = pagerState, modifier = Modifier.padding(padding))
        { page ->
            val daily = dailyList[page]
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Blue500)
            ) {
                item{

                    DailyWeatherCard(daily = daily)
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    WeatherHeader(text = localContext.resources.getString(R.string.weather_24_hour))
                }

                item {
                    val next24Hours = daily.dt + 86400L
                    val filteredHourlyWeatherList = hourlyList
                        .filter { it.dt >= daily.dt && it.dt < next24Hours }
                        .sortedBy { it.dt }
                        .take(24)
                    HourlyWeatherRow(filteredHourlyWeatherList,timeZone)
                }

                item {


                    Row(
                        modifier = Modifier
                            .padding(all = 3.dp)
                            .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround
                    ) {

                        HumidityCard(humidity = daily.humidity, dewPoint =daily.dewPoint.toInt() )
                        WindSpeedCard(speed = daily.windSpeed.toInt(), windDegree = daily.windDeg)

                    }
                }
                item{
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 5.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {

                        UVIndexCard(index = daily.uvi.toInt())
                        PressureCard(pressure = daily.pressure)

                    }
                }
                item{
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
                                    sunrise = WeatherUtils.updateTime(daily.sunrise, timeZone),
                                    sunset = WeatherUtils.updateTime(daily.sunset,timeZone),
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
        }
    }
}
