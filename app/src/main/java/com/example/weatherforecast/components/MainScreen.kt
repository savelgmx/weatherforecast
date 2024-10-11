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
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.weatherforecast.R
import com.example.weatherforecast.response.ForecastResponse
import com.example.weatherforecast.response.WeatherResponse
import com.example.weatherforecast.theme.Blue300
import com.example.weatherforecast.theme.QuickSandTypography
import com.example.weatherforecast.utils.Resource
import com.example.weatherforecast.utils.WeatherUtils
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    currentState: Resource<WeatherResponse>,
    forecastState: Resource<ForecastResponse>
) {
    val scaffoldState= rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val cityName = currentState.data?.name
    val humidity= currentState.data?.main?.humidity
    val dewPoint = forecastState.data?.current?.dewPoint
    val windSpeed = currentState.data?.wind?.speed?.toInt()
    val windDegree= currentState.data?.wind?.deg
    val timeOfSunrise = forecastState.data?.current?.sunrise.let { WeatherUtils.updateTime(it) }
    val timeOfSunset = forecastState.data?.current?.sunset.let{WeatherUtils.updateTime(it)}

    val timeOfDawn=forecastState.data?.current?.sunrise
    val timeOfDusk=forecastState.data?.current?.sunset
    val timeOfDawnAndDusk= WeatherUtils.calculateDawnAndDusk(timeOfDawn,timeOfDusk)//it returns array of two elements

    val uvIndex = forecastState.data?.current?.uvi
    val pressureValue = currentState.data?.main?.pressure

    val moonPhase = forecastState.data?.daily?.get(0)?.moonPhase
    val moonRise = forecastState.data?.daily?.get(0)?.moonrise.let { WeatherUtils.updateTime(it) }
    val moonSet = forecastState.data?.daily?.get(0)?.moonset.let { WeatherUtils.updateTime(it) }

    Scaffold(
        scaffoldState=scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("$cityName") },
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch {

                            scaffoldState.drawerState.open()
                        }
                    }) {
                        Icon(Icons.Filled.Menu, contentDescription = null)
                    }
                }
            )
        },
        drawerContent = {
            DrawerContent()
        }    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Blue300)
        ) {
            item {
                currentState.data?.let {
                    CurrentWeatherCard(weatherState = currentState)
                }
            }
            item {
                Spacer(modifier = Modifier
                    .height(3.dp)
                    .fillMaxWidth())
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = context.resources.getString(R.string.weather_24_hour),
                    fontWeight = FontWeight.Bold,
                    style = QuickSandTypography.subtitle1,
                    color = Color.White,
                    modifier = Modifier.padding(start = 20.dp)
                )
            }
            item {
                forecastState.data?.hourly?.let { hourlyWeatherList ->
                    HourlyWeatherRow(hourlyWeatherList)
                }
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = context.resources.getString(R.string.weather_7_days),
                    fontWeight = FontWeight.Bold,
                    style = QuickSandTypography.subtitle1,
                    color = Color.White, modifier = Modifier.padding(start = 20.dp)
                )
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
                Text(
                    text = context.getString(R.string.daily_weather_forecast),
                    fontWeight = FontWeight.Bold,
                    style = QuickSandTypography.subtitle1,
                    color = Color.White, modifier = Modifier.padding(start = 20.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item{

                Row (modifier = Modifier
                    .fillMaxWidth().padding(all = 3.dp),
                    horizontalArrangement = Arrangement.SpaceAround)
                {
                    if (humidity != null) {
                        if (dewPoint != null) {
                            HumidityCard(humidity =humidity, dewPoint =dewPoint.toInt() )
                        }
                    }
                    if (windSpeed != null) {
                        if (windDegree != null) {
                            WindSpeedCard(speed = windSpeed, windDegree =windDegree )
                        }
                    }

                }

            }

            item {
                Row(modifier = Modifier.fillMaxWidth().padding(all=3.dp),
                    horizontalArrangement = Arrangement.SpaceAround)
                {
                    if (uvIndex != null) {
                        UVIndexCard(index = uvIndex.toInt())
                    }
                    if (pressureValue != null) {
                        PressureCard(pressure =pressureValue )
                    }
                }
            }//end uv pressure item
            item{

                //        Spacer(modifier = Modifier.height(8.dp))

                Row (modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 3.dp), horizontalArrangement = Arrangement.SpaceAround){
                    timeOfDawnAndDusk[0]?.let {
                        timeOfDawnAndDusk[1]?.let { it1 ->
                            SunriseSunsetCard(sunrise = timeOfSunrise, sunset = timeOfSunset,
                                dawn = it, dusk = it1
                            )
                        }
                    }

                    if (moonPhase != null) {
                        MoonriseMoonsetCard(moonrise =moonRise, moonset = moonSet, moonPhase = moonPhase )
                    }

                }

            }//end sunrise item
        }
    }
}
