package com.example.weatherforecast.components

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
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
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
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
            .background(Color.Transparent)
            .padding(vertical = 3.dp)
            .clickable {
                       Log.d("daily clickable",daily.toString())
            },
        shape = RoundedCornerShape(8.dp),

        ) {
        Column(modifier = Modifier
            .padding(16.dp)
            .background(Color(0x3498eb))) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween

            ) {
                Text(

                    text = WeatherUtils.updateDateToToday(daily.dt),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
                AsyncImage(
                    model = "${UIUtils.iconurl}${daily.weather[0].icon}.png",
                    contentDescription = "Weather icon",
                    modifier = Modifier
                        .size(50.dp) // Define your desired width and height
                        .padding(all = 3.dp)
                )
                Text(
                    text = WeatherUtils.updateTemperature(daily.temp.day.toInt()) +"/"+
                            WeatherUtils.updateTemperature(daily.temp.night.toInt()),
                    fontWeight = FontWeight.Bold,
                    //  fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 3.dp)
                )

            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = localContext.getString(R.string.feels_like) + ": ${daily.feelsLike.day.toInt()} °C ",
                    modifier = Modifier.padding(all = 3.dp)
                )
                Text(
                    text = daily.weather[0].description,
                    modifier = Modifier.padding(all = 3.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun ForecastUISuccessPreview() {
    val successState = Resource.Success(getMockForecastlist())
    ForecastWeatherList( successState)
}

fun getMockForecastlist(): ForecastResponse {
    return ForecastResponse(

        Current(
            100, -18.32, 1708774497, -17.78, 95, 1037,
            1708736117, 1708772966, -17.78, 0.0, 10000,
            listOf(Weather(804, "Clouds", "пасмурно", "04n")),
            228, 0.6, 6.0
        ),
        listOf(
            Daily(
                100, -18.32, 1708754400,
                FeelsLike(-19.14, -16.77, -27.53, -19.14),
                73, 0.5, 1708862520, 1708824420, 1036,
                1708736117, 1708772966,
                Temp(-19.14, -16.77, -27.53, -19.14, -20.0, -24.4),
                0.5, listOf(Weather(804, "Clouds", "пасмурно", "04n")),
                224, 1.37, 2.40
            )
        ),
        listOf(
            Hourly(
                99, -18.32, 1708774497, -17.78, 95,
                1037, -21.2, 0.0, 10000,
                listOf(Weather(804, "Clouds", "пасмурно", "04n")),
                223, 2.2, 1.8
            )
        ),
        56.0097, 92.79, "Asia/Krasnoyarsk", 25200
    )
}
