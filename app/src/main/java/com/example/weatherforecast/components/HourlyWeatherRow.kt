package com.example.weatherforecast.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults.shape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weatherforecast.response.ForecastResponse
import com.example.weatherforecast.response.Hourly
import com.example.weatherforecast.response.Weather
import com.example.weatherforecast.theme.AppShapes
import com.example.weatherforecast.theme.Blue300
import com.example.weatherforecast.theme.Blue700
import com.example.weatherforecast.theme.QuickSandTypography
import com.example.weatherforecast.utils.AppConstants
import com.example.weatherforecast.utils.Resource
import com.example.weatherforecast.utils.WeatherUtils

@Composable
fun HourlyWeatherRow(hourlyForecast: List<Hourly>)
{

    Box (
        modifier = Modifier
            .background(
                Blue300
                ,shape = AppShapes.large)
            .fillMaxWidth()
            .padding(all=20.dp),
        )
    {

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(Blue700, shape= AppShapes.large
                )
                .padding(all = 20.dp),
            contentPadding = PaddingValues(all = 8.dp),

            ) {
            items(hourlyForecast) { hourly ->
                HourlyWeatherItem(hourly = hourly)
            }
        }
    }
}
@Composable
fun HourlyWeatherItem(hourly: Hourly) {

    var iconurl=AppConstants.WEATHER_API_IMAGE_ENDPOINT
    Column(modifier = Modifier
        .padding(3.dp)
        .background(Blue700),
         )

    {
        Text(
            text = WeatherUtils.updateTime(hourly.dt),
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            style = QuickSandTypography.h5,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(all = 3.dp)
        )
        AsyncImage(
            model = "${iconurl}${hourly.weather[0].icon}.png",
            contentDescription = "Weather icon",
            modifier = Modifier
                .size(50.dp) // Define your desired width and height
                .padding(all = 1.dp)
        )
        Text(
            text = WeatherUtils.updateTemperature(hourly.temp.toInt()),
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            style = QuickSandTypography.h5,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(all = 1.dp)
        )
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun HourlyUISuccessPreview() {
    val successState = getMockHourlylist()
    HourlyWeatherRow(successState)
}

fun getMockHourlylist(): List<Hourly> {
    return listOf(
        Hourly(
            99, -18.32, 1708774497, -17.78, 95,
            1037, -21.2, 0.0, 10000,
            listOf(Weather(804, "Clouds", "пасмурно", "04n")),
            223, 2.2, 1.8
        ),
        Hourly(
            99, -18.32, 1708774497, -17.78, 95,
            1037, -21.2, 0.0, 10000,
            listOf(Weather(804, "Clouds", "пасмурно", "04n")),
            223, 2.2, 1.8
        ),
        Hourly(
            99, -18.32, 1708774497, -17.78, 95,
            1037, -21.2, 0.0, 10000,
            listOf(Weather(804, "Clouds", "пасмурно", "04n")),
            223, 2.2, 1.8
        )


    )


}
