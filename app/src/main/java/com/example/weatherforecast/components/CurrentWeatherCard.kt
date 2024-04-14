package com.example.weatherforecast.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.weatherforecast.response.Clouds
import com.example.weatherforecast.response.Coord
import com.example.weatherforecast.response.Main
import com.example.weatherforecast.response.Sys
import com.example.weatherforecast.response.Weather
import com.example.weatherforecast.response.WeatherResponse
import com.example.weatherforecast.response.Wind
import com.example.weatherforecast.utils.AppConstants
import com.example.weatherforecast.utils.Resource
import com.example.weatherforecast.utils.UIUtils
import com.example.weatherforecast.utils.WeatherUtils

@Composable
fun CurrentWeatherCard(
    weatherState: Resource<WeatherResponse>
){

    Box(
        modifier = Modifier
            .background(color = Color.LightGray, shape = RoundedCornerShape(6.dp))
            .fillMaxWidth()
            .padding(6.dp),
       // verticalArrangement = Arrangement.Top,
       // horizontalAlignment = Alignment.Start
    ){
        when (weatherState){
            is Resource.Success->{
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(text = name!!, color= Color.DarkGray,
                        fontSize = 15.sp,modifier = Modifier.padding(start = 16.dp))

                    Text(text =day!!, color= Color.DarkGray,fontSize = 15.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))
                // Row 2: Temperature with Weather Icon

                val icon = weatherState.data?.weather?.get(0)?.icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = " $temperature",
                        fontWeight = FontWeight.Bold,
                        fontSize = 35.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )

                    AsyncImage(
                        model = "${UIUtils.iconurl}$icon.png",
                        contentDescription = "Weather icon",
                        modifier = Modifier
                            .size(70.dp) // Define your desired width and height
                            .padding(all = 6.dp)
                    )

                    Text(text = " $feels_like", modifier = Modifier.padding(8.dp))
                }


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
@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun WeatherUISuccessPreview() {
    val successState = Resource.Success(getMockWeatherCard())
    CurrentWeatherCard( successState)
}
fun getMockWeatherCard(): WeatherResponse {
    return WeatherResponse(
        Coord(92.7917, 56.0097),
        listOf(Weather(804, "Clouds", "пасмурно", "04n")),
        "stations",
        Main(-21.77, -28.77, -22.78, -21.77, 1040, 85),
        10000,
        Wind(2.73, 228),
        Clouds(100),
        1708170884,
        Sys(2, 2088371, "RU", 1708132314, 1708167250),
        25200,
        1502026,
        "Красноярск",
        200
    )
}

