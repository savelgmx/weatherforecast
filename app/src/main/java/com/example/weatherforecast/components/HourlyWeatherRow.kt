package com.example.weatherforecast.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.weatherforecast.R
import com.example.weatherforecast.response.Hourly
import com.example.weatherforecast.theme.AppShapes
import com.example.weatherforecast.theme.Blue300
import com.example.weatherforecast.theme.Blue700
import com.example.weatherforecast.theme.QuickSandTypography
import com.example.weatherforecast.utils.AppConstants
import com.example.weatherforecast.utils.UIUtils
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
                .padding(all = 2.dp),
            contentPadding = PaddingValues(all = 1.dp),

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

        val localContext= LocalContext.current
        val switchState by DataStoreManager.tempSwitchPrefFlow(localContext).collectAsState(initial = false)

        val icon = hourly.weather[0].icon
        val localIconName = icon.replace("-", "_")
        val drawableId = localContext.resources.getIdentifier(localIconName, "drawable",localContext. packageName)
        val imageModel = if (drawableId != 0) drawableId else R.drawable.default_icon


        Text(
            text = WeatherUtils.updateTime(hourly.dt),
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            style = QuickSandTypography.h6,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(all = 3.dp)
        )
        AsyncImage(
            model = imageModel, //"${iconurl}${hourly.weather[0].icon}.png",
            contentDescription = "Weather icon",
            modifier = Modifier
                .size(40.dp) // Define your desired width and height
                .padding(all = 3.dp)
        )
        Text(
            text = WeatherUtils.updateTemperature(hourly.temp.toInt(), switchState),
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            style = QuickSandTypography.h6,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(all =3.dp)
        )
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun HourlyUISuccessPreview() {
    val successState = UIUtils.getMockHourlylist()
    HourlyWeatherRow(successState)
}

