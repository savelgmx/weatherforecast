package com.example.weatherforecast


import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
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
import coil.compose.AsyncImage
import com.example.weatherforecast.response.Clouds
import com.example.weatherforecast.response.Coord
import com.example.weatherforecast.response.Current
import com.example.weatherforecast.response.Daily
import com.example.weatherforecast.response.FeelsLike
import com.example.weatherforecast.response.ForecastResponse
import com.example.weatherforecast.response.Hourly
import com.example.weatherforecast.response.Main
import com.example.weatherforecast.response.Sys
import com.example.weatherforecast.response.Temp
import com.example.weatherforecast.response.Weather
import com.example.weatherforecast.response.WeatherResponse
import com.example.weatherforecast.response.Wind
import com.example.weatherforecast.ui.theme.WeatherforecastTheme
import com.example.weatherforecast.ui.viewmodel.OpenWeatherForecastViewModel
import com.example.weatherforecast.ui.viewmodel.OpenWeatherMapViewModel
import com.example.weatherforecast.utils.AppConstants
import com.example.weatherforecast.utils.Resource
import com.example.weatherforecast.utils.WeatherUtils
import dagger.hilt.android.AndroidEntryPoint

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons


import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp






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
                    val openWeatherForecastViewModel:OpenWeatherForecastViewModel=viewModel()

                    // Collect the LiveData state in a Compose State
                    var weatherState by remember { mutableStateOf<Resource<WeatherResponse>>(Resource.Loading()) }
                    //Collect LiveData state of Forecast in Compose state
                    var forecatState by remember { mutableStateOf<Resource<ForecastResponse>>(Resource.Loading())}

                    // Call getCurrentWeather with the desired city
                    openWeatherMapViewModel.getCurrentWeather()
                    //Call getForecastWeather
                    openWeatherForecastViewModel.getForecastWeather()

                    // Observe the LiveData and update the weatherState
                    LaunchedEffect(openWeatherMapViewModel.weatherLiveData) {
                        openWeatherMapViewModel.weatherLiveData.observe(this@MainActivity) { resource ->
                            weatherState = resource
                        }
                    }

                    //Observe LiveData and update the forecastState
                    LaunchedEffect(openWeatherForecastViewModel.forecastLiveData){
                        openWeatherForecastViewModel.forecastLiveData.observe(this@MainActivity){ resource ->
                            forecatState =resource
                        }
                    }

                    // Display a structured UI based on the state
                    WeatherUI(weatherState,forecatState)
                    //ForecastUI(forecatState)
                }
            }
        }
    }
}

@Composable
fun ForecastUI(forecastState:Resource<ForecastResponse>){
    Log.d("Forecast2 response",forecastState.data.toString())

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
    val localContext = LocalContext.current //To access the context within a Composable function,
                            // use the LocalContext provided by Jetpack Compose
                            //we need this context to load  string values form strings.xml
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                // Handle click action, you can navigate to detailed info screen here
                // or show detailed info in a bottom sheet or dialog
            },
        shape = RoundedCornerShape(16.dp),
 
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${WeatherUtils.updateDateToToday(daily.dt)}",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = localContext.getString(R.string.sunrise)+"${WeatherUtils.updateTime(daily.sunrise)}",
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Sunset: ${WeatherUtils.updateTime(daily.sunset)}",
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Moonrise: ${WeatherUtils.updateTime(daily.moonrise)}",
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Moonset: ${WeatherUtils.updateTime(daily.moonset)}",
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Temperature: ${daily.temp.day} °C",
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Feels Like: ${daily.feelsLike.day} °C",
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Weather: ${daily.weather[0].description}",
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Clouds: ${daily.clouds}%",
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "UV Index: ${daily.uvi}",
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
    }
}

@Composable
fun WeatherUI(weatherState: Resource<WeatherResponse>, forecastState: Resource<ForecastResponse>?) {
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
                val localContext = LocalContext.current //To access the context within a Composable function, use the LocalContext provided by Jetpack Compose

                val temperature = weatherState.data?.main?.temp?.let { WeatherUtils.updateTemperature(it.toInt()) }
                val name =weatherState.data?.name
                val day=  weatherState.data?.dt?.let { WeatherUtils.updateDateToToday(it.toInt()) }
                val pressure = localContext.getString(R.string.pressure)+":"+ weatherState.data?.main?.pressure?.let { WeatherUtils.updatePressure(it) }
                val feels_like =localContext.getString(R.string.feels_like)+":"+ weatherState.data?.main?.feels_like?.let { WeatherUtils.updateTemperature(it.toInt()) }
                val wind= weatherState.data?.wind?.speed?.let { WeatherUtils.updateWind(weatherState.data?.wind?.deg.toString(), it.toInt(),localContext) }

                val icon =  weatherState.data?.weather?.get(0)?.icon


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

                Row(Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment= Alignment.CenterVertically
                ) {
                    Text(text="$wind",Modifier.padding(8.dp))

                    Text(text = " $pressure", modifier = Modifier.padding(1.dp))

                }

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

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun WeatherUISuccessPreview() {
    val successState = Resource.Success(getMockWeatherResponse())
    val successForecastState = Resource.Success(getMockForecastResponse())

    WeatherUI(successState,successForecastState)
    // ForecastUI(successForecastState)
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
fun getMockForecastResponse(): ForecastResponse {
    return ForecastResponse(

        Current (100, -18.32, 1708774497, -17.78, 95, 1037,
            1708736117, 1708772966, -17.78, 0.0, 10000,
            listOf(Weather(804, "Clouds", "пасмурно", "04n")) ,
            228,0.6,6.0),
        listOf( Daily(100,-18.32,1708754400,
            FeelsLike(-19.14,-16.77,-27.53,-19.14),
            73,0.5, 1708862520,1708824420,0.0,1036,
            1708736117, 1708772966,
            Temp(-19.14,-16.77,-27.53,-19.14,-20.0,-24.4),
            0.5, listOf(Weather(804, "Clouds", "пасмурно", "04n")) ,
            224,1.37,2.40) ),
        listOf(Hourly(99,-18.32,1708774497,-17.78, 95,
            0,1037,-21.2,0.3,10000,
            listOf(Weather(804, "Clouds", "пасмурно", "04n")),
            223,2.2,1.8)),
        56.0097, 92.79, "Asia/Krasnoyarsk", 25200
    )
}




