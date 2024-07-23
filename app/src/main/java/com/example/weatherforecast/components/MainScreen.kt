package com.example.weatherforecast.components

// MainScreen.kt

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.weatherforecast.response.ForecastResponse
import com.example.weatherforecast.response.WeatherResponse
import com.example.weatherforecast.theme.Blue300
import com.example.weatherforecast.theme.QuickSandTypography
import com.example.weatherforecast.utils.Resource


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    currentState: Resource<WeatherResponse>,
    forecastState: Resource<ForecastResponse>
) {
    // Update the toolbar title
    //  (activity as MainActivity).updateToolbarTitle("$date $cityName")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weather Forecast") },
                actions = {
                    IconButton(onClick = { navController.navigate("settings_screen") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Blue300)
        ) {
            item {
                currentState.data?.let { weatherState ->
                //    CurrentWeatherCard(weatherState = weatherState)
                }
            }
            item {
                Spacer(modifier = Modifier.height(3.dp).fillMaxWidth())
            }
            item {
                Text(
                    "Weather for 24 hours",
                    fontWeight = FontWeight.Bold,
                    style = QuickSandTypography.subtitle1,
                    color = Color.White,
                    modifier = Modifier.padding(start = 20.dp)
                )
            }
            item {
                forecastState.data?.hourly?.let {
                    HourlyWeatherRow(it)
                }
            }
            item {
                Spacer(modifier = Modifier.height(3.dp).fillMaxWidth())
            }
            item {
                Text(
                    "Weather for 7 days",
                    fontWeight = FontWeight.Bold,
                    style = QuickSandTypography.subtitle1,
                    color = Color.White, modifier = Modifier.padding(start = 20.dp)
                )
            }
            item {
/*
                ForecastWeatherList(
                    forecastState = forecastState,
                    navController = navController
                )
*/
            }
        }
    }
}
