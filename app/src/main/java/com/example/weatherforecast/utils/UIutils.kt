package com.example.weatherforecast.utils

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weatherforecast.R
import com.example.weatherforecast.response.Daily
import com.example.weatherforecast.response.ForecastResponse
import com.example.weatherforecast.response.Hourly
import com.example.weatherforecast.response.WeatherResponse

class UIUtils {
    companion object {
        @Composable
        fun ForecastUI(forecastState: Resource<ForecastResponse>) {
            Log.d("Forecast2 response", forecastState.data.toString())

            // Assuming forecastState contains the list of daily forecast data
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
            val iconurl = AppConstants.WEATHER_API_IMAGE_ENDPOINT
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .padding(vertical = 3.dp)
                    .clickable {
                        // Handle click action, you can navigate to detailed info screen here
                        // or show detailed info in a bottom sheet or dialog
                    },
                shape = RoundedCornerShape(16.dp),

                ) {
                Column(modifier = Modifier
                    .padding(1.dp)
                    .background(Color.Cyan)) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly

                    ) {
                        Text(

                            text = WeatherUtils.updateDateToToday(daily.dt),
                            fontWeight = FontWeight.Bold,
                            //  fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        AsyncImage(
                            model = "$iconurl${daily.weather[0].icon}.png",
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
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                    }



                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = localContext.getString(R.string.feels_like) + ": ${daily.feelsLike.day.toInt()} °C",
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = daily.weather[0].description,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                    }



                }
            }
        }


        @Composable
        fun WeatherUI(
            weatherState: Resource<WeatherResponse>,
            forecastState: Resource<ForecastResponse>?
        ) {
            val iconurl = AppConstants.WEATHER_API_IMAGE_ENDPOINT
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (weatherState) {
                    is Resource.Success -> {
                        val localContext =
                            LocalContext.current //To access the context within a Composable function, use the LocalContext provided by Jetpack Compose

                        val temperature = weatherState.data?.main?.temp?.let {
                            WeatherUtils.updateTemperature(it.toInt())
                        }
                        val name = weatherState.data?.name
                        val day =
                            weatherState.data?.dt?.let { WeatherUtils.updateDateToToday(it.toInt()) }
                        val pressure =
                            localContext.getString(R.string.pressure) + ":" + weatherState.data?.main?.pressure?.let {
                                WeatherUtils.updatePressure(it)
                            }
                        val feels_like =
                            localContext.getString(R.string.feels_like) + ":" + weatherState.data?.main?.feels_like?.let {
                                WeatherUtils.updateTemperature(it.toInt())
                            }
                        val wind = weatherState.data?.wind?.speed?.let {
                            WeatherUtils.updateWind(
                                weatherState.data?.wind?.deg.toString(),
                                it.toInt(),
                                localContext
                            )
                        }

                        val icon = weatherState.data?.weather?.get(0)?.icon


                        // Row 1: Name and Day with Blue Background
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(Color.Blue)
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(text = name!!, color = Color.White)
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(Color.Blue)
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(text = day!!, color = Color.White)
                            }
                        }

                        // Row 2: Temperature with Weather Icon
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = " $temperature",
                                fontWeight = FontWeight.Bold,
                                fontSize = 35.sp,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                            Text(text = " $feels_like", modifier = Modifier.padding(8.dp))
                            AsyncImage(
                                model = "$iconurl$icon.png",
                                contentDescription = "Weather icon",
                                modifier = Modifier
                                    .size(50.dp) // Define your desired width and height
                            )
                        }

                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "$wind", Modifier.padding(8.dp))
                            Text(text = " $pressure", modifier = Modifier.padding(1.dp))

                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Погода на сутки",fontWeight = FontWeight.Bold)
                        //And now include hourly UI here
                        forecastState?.data?.hourly.let {
                            if (it != null) {
                                HourlyWeatherRow(it)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Погода на 7 дней",fontWeight = FontWeight.Bold)


                        // Include ForecastUI here
                        forecastState?.let { ForecastUI(it) }
                    }

                    is Resource.Loading -> {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading...")
                    }

                    is Resource.Error -> {
                        Text("Error: ${weatherState.msg}")
                    }

                    else -> {}
                }
            }
        }
        @Composable
        fun HourlyWeatherRow(hourlyForecast: List<Hourly>) {
            LazyRow(
                modifier = Modifier.fillMaxWidth() ,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
            ) {
                items(hourlyForecast) { hourly ->
                    HourlyWeatherItem(hourly = hourly)
                }
            }
        }

        @Composable
        fun HourlyWeatherItem(hourly: Hourly) {
            val iconurl = AppConstants.WEATHER_API_IMAGE_ENDPOINT
            Card(
                modifier = Modifier
                    .fillMaxWidth() // Define your desired width
                    .padding(vertical = 8.dp).background(Color.Transparent),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(4.dp).background(Color.Cyan)) {
                    Text(
                        text = WeatherUtils.updateTime(hourly.dt),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(all = 4.dp)
                    )
                    AsyncImage(
                        model = "$iconurl${hourly.weather[0].icon}.png",
                        contentDescription = "Weather icon",
                        modifier = Modifier
                            .size(40.dp) // Define your desired width and height
                            .padding(all = 3.dp)
                    )
                    Text(
                        text = WeatherUtils.updateTemperature(hourly.temp.toInt()),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(all = 4.dp)
                    )
                }
            }
        }

    }
}






