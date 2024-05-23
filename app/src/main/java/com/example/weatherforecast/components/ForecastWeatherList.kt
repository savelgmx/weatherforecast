package com.example.weatherforecast.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.shape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.weatherforecast.R
import com.example.weatherforecast.response.Current
import com.example.weatherforecast.response.Daily
import com.example.weatherforecast.response.FeelsLike
import com.example.weatherforecast.response.ForecastResponse
import com.example.weatherforecast.response.Hourly
import com.example.weatherforecast.response.Temp
import com.example.weatherforecast.response.Weather
import com.example.weatherforecast.theme.AppShapes
import com.example.weatherforecast.theme.Blue400
import com.example.weatherforecast.theme.Blue700
import com.example.weatherforecast.theme.QuickSandTypography
import com.example.weatherforecast.theme.White2
import com.example.weatherforecast.utils.Resource
import com.example.weatherforecast.utils.UIUtils
import com.example.weatherforecast.utils.WeatherUtils

@Composable
fun ForecastWeatherList(
    forecastState:Resource<ForecastResponse>
) {
    val dailyForecast = forecastState.data?.daily
    val count = dailyForecast?.size ?: 0

    LazyColumn(
        modifier = Modifier.fillMaxSize()
            .padding(all=16.dp)
            .border(width=3.dp, color = Color.Transparent,
                shape=AppShapes.large),
       // contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
    ) {
        items(count) { index ->
            dailyForecast?.getOrNull(index)?.let { daily ->
                ClickableDayForecastItem(daily = daily)
            }
        }
    }
}
@Composable
fun ClickableDayForecastItem(daily: Daily) {
    val localContext =
        LocalContext.current //To access the context within a Composable function,
    // use the LocalContext provided by Jetpack Compose
    //we need this context to load  string values form strings.xml
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 1.dp)

            .clickable {
                       Log.d("daily clickable",daily.toString())
            },
       // shape = AppShapes.large

        ) {
        Column(modifier = Modifier
            .padding(all=1.dp)
            .background(Blue700)

        )
        {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly

            ) {
                Text(

                    text = WeatherUtils.updateDateToToday(daily.dt),
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    style = QuickSandTypography.body1,
                    modifier = Modifier
                        .padding(all = 8.dp)
                )
                AsyncImage(
                    model = "${UIUtils.iconurl}${daily.weather[0].icon}.png",
                    contentDescription = "Weather icon",
                    modifier = Modifier
                        .size(70.dp)// Define your desired width and height
                        .padding(all = 8.dp)
                )
                Text(
                    text = WeatherUtils.updateTemperature(daily.temp.day.toInt()) +"/"+
                            WeatherUtils.updateTemperature(daily.temp.night.toInt()),
                    fontWeight = FontWeight.Bold, color = Color.White,
                    style = QuickSandTypography.body1,
                     modifier = Modifier.padding(all = 8.dp)
                )

            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = localContext.getString(R.string.feels_like) + ": ${daily.feelsLike.day.toInt()} °C ",
                    color = Color.White,
                    style = QuickSandTypography.subtitle2,
                    modifier = Modifier.padding(all = 8.dp)
                )
                Text(
                    text = daily.weather[0].description,
                    color = Color.White,
                    style = QuickSandTypography.subtitle1,
                    modifier = Modifier.padding(all = 8.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun ForecastUISuccessPreview() {
    val successState = Resource.Success(UIUtils.getMockForecastlist())
    ForecastWeatherList( successState)
}
