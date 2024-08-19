package com.example.weatherforecast.components



import androidx.compose.foundation.background
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

    val date = currentState.data?.dt?.let { WeatherUtils.updateDateToToday(it.toInt()) }
    val cityName = currentState.data?.name
    Scaffold(
        scaffoldState=scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("$date $cityName") },
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
                currentState.data?.let { weatherState ->
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
                forecastState?.data?.hourly?.let { hourlyWeatherList ->
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
        }
    }
}
