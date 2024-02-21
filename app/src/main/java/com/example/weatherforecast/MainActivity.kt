package com.example.weatherforecast

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherforecast.response.Clouds
import com.example.weatherforecast.response.Coord
import com.example.weatherforecast.response.Main
import com.example.weatherforecast.response.Sys
import com.example.weatherforecast.response.Weather
import com.example.weatherforecast.response.WeatherResponse
import com.example.weatherforecast.response.Wind
import com.example.weatherforecast.ui.theme.WeatherforecastTheme
import com.example.weatherforecast.ui.viewmodel.OpenWeatherMapViewModel
import com.example.weatherforecast.utils.AppConstants
import com.example.weatherforecast.utils.Resource
import com.example.weatherforecast.utils.WeatherUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherforecastTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Use the viewModel composition local to get an instance of OpenWeatherMapViewModel
                    val openWeatherMapViewModel: OpenWeatherMapViewModel =  viewModel()

                    // Collect the LiveData state in a Compose State
                    var weatherState by remember { mutableStateOf<Resource<WeatherResponse>>(Resource.Loading()) }

                    // Call getCurrentWeather with the desired city
                    openWeatherMapViewModel.getCurrentWeather("Krasnoyarsk")

                    // Observe the LiveData and update the weatherState
                    LaunchedEffect(openWeatherMapViewModel.weatherLiveData) {
                        openWeatherMapViewModel.weatherLiveData.observe(this@MainActivity) { resource ->
                            weatherState = resource
                        }
                    }

                    // Display a structured UI based on the state
                    WeatherUI(weatherState)
                }
            }
        }
    }
}


@Composable
fun WeatherUI(weatherState: Resource<WeatherResponse>) {
    val iconurl=AppConstants.WEATHER_API_IMAGE_ENDPOINT
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (weatherState) {
            is Resource.Success -> {
                val localContext = LocalContext.current //To access the context within a Composable function, use the LocalContext provided by Jetpack Compose

                weatherState.data?.coord?.let { WeatherUtils.setLatitude(it.lat) }  //set latitude
                weatherState.data?.coord?.let { WeatherUtils.setLongitude(it.lon) } //set longitude

                val temperature = weatherState.data?.main?.temp?.let { WeatherUtils.updateTemperature(it.toInt()) }
                val name =weatherState.data?.name
                val day=  weatherState.data?.dt?.let { WeatherUtils.updateDateToToday(it.toInt()) }
                val pressure = localContext.getString(R.string.pressure)+":"+ weatherState.data?.main?.pressure?.let { WeatherUtils.updatePressure(it) }
                val feels_like =localContext.getString(R.string.feels_like)+":"+ weatherState.data?.main?.feels_like?.let { WeatherUtils.updateTemperature(it.toInt()) }
                val wind= weatherState.data?.wind?.speed?.let { WeatherUtils.updateWind(weatherState.data?.wind?.deg.toString(), it.toInt(),localContext) }


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
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = " $temperature",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    /*
                                         GlideImage(
                                            model = "$iconurl${weatherState.data?.weather?.get(0)?.icon}.png",
                                            contentDescription = "Weather Icon",
                                            modifier = Modifier.size(50.dp)
                                        )
                    */
                }

                Text(text = " $feels_like", modifier = Modifier.padding(8.dp))
                Text(text = " $pressure", modifier = Modifier.padding(8.dp))

                Text(text="$wind",Modifier.padding(8.dp))

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
    val successState = Resource.Success(getMockWeatherResponse())
    WeatherUI(successState)
}
fun getMockWeatherResponse(): WeatherResponse {
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


