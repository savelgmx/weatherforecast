package com.example.weatherforecast.components


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.weatherforecast.response.Daily
import com.example.weatherforecast.theme.Blue700
import com.example.weatherforecast.utils.UIUtils
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import com.example.weatherforecast.R
import com.example.weatherforecast.theme.AppShapes
import com.example.weatherforecast.theme.Blue500
import com.example.weatherforecast.theme.QuickSandTypography
import com.example.weatherforecast.utils.WeatherUtils

@Composable
fun DailyWeatherForecast(
    daily: Daily
){
    val localContext = LocalContext.current //To access the context within a Composable function,
    // use the LocalContext provided by Jetpack Compose
    //we need this context to load  string values form strings.xml
    val feels_like =localContext.getString(R.string.feels_like) + ": "+ WeatherUtils.updateTemperature(daily.feelsLike.day.toInt())

    val wind =
        WeatherUtils.updateWind(
            daily.windDeg.toString(),
            daily.windSpeed.toInt(),
            localContext
        )
    val sunrise="${localContext.getString(R.string.sunrise)}: " +
            "${WeatherUtils.updateTime(daily.sunrise)} "

    val sunset="${localContext.getString(R.string.sunset)}: " +
            "${WeatherUtils.updateTime(daily.sunset)} "


    Column(
 modifier= Modifier
     .fillMaxSize()
     .background(Blue500),
    ){
        //row 1 data and
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center

        ){
            Text(
                text= WeatherUtils.updateDateToToday(daily.dt),
                fontWeight = FontWeight.SemiBold,
                color= Color.White,
                style= QuickSandTypography.caption,
                modifier = Modifier.padding(all = 16.dp)
            )
            Text(
                text= WeatherUtils.updateTime(daily.dt),
                fontWeight = FontWeight.SemiBold,
                color= Color.White,
                style= QuickSandTypography.caption,
                modifier = Modifier.padding(all = 16.dp)
            )

        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly

        ){
            AsyncImage(
                model = "${UIUtils.iconurl}${daily.weather[0].icon}.png",
                contentDescription = "Weather icon",
                modifier = Modifier
                    .size(70.dp)// Define your desired width and height
                    .padding(all = 3.dp)
            )
            Text(
                text =localContext.getString(R.string.day)+": "+ WeatherUtils.updateTemperature(daily.temp.day.toInt()),
                fontWeight = FontWeight.Bold, color = Color.White,
                style = QuickSandTypography.body1,
                modifier = Modifier.padding(all = 3.dp)
            )
            Text(
                text =localContext.getString(R.string.night)+": "
                        +WeatherUtils.updateTemperature(daily.temp.night.toInt()),
                fontWeight = FontWeight.Bold, color = Color.White,
                style = QuickSandTypography.body1,
                modifier = Modifier.padding(all = 3.dp)
            )
                Text(
                    text = feels_like,
                    color = Color.White,
                    style = QuickSandTypography.subtitle2,
                    modifier = Modifier.padding(all = 3.dp)
                )

        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = daily.weather[0].description,
                color = Color.White,
                style = QuickSandTypography.subtitle1,
                modifier = Modifier.padding(all = 3.dp)
            )
            Text(

                text = localContext.getString(R.string.pressure)+": "
                        +daily.pressure.toString(),
                color= Color.White,
                style = QuickSandTypography.subtitle1,
                modifier = Modifier.padding(all=3.dp)
            )

            Text(
                text = localContext.getString(R.string.humidity)+": "
                        +daily.humidity,
                color= Color.White,
                style = QuickSandTypography.subtitle1,
                modifier = Modifier.padding(all=3.dp)
            )


        }
//wind speed and directions
      Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceAround
      )  {
          Text(text=wind,
              color= Color.White,
              style = QuickSandTypography.subtitle1,
              modifier = Modifier.padding(all=3.dp)
          )
      }
        //sunset sun rise
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(text=sunrise,
                color = Color.White,
                style= QuickSandTypography.h6,
                modifier=Modifier.padding(all=3.dp)
            )
            Text(text=sunset,
                color = Color.White,
                style= QuickSandTypography.h6,
                modifier=Modifier.padding(all=3.dp)
            )

        }
    }   
}
@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun DailyWeatherPreview(){
    val dailyData = UIUtils.getMockDailyWeather()
    DailyWeatherForecast(daily = dailyData)
}
