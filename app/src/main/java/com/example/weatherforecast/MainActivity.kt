package com.example.weatherforecast


import android.content.pm.PackageManager
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.weatherforecast.utils.UIUtils
import android.Manifest


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val PERMISSION_REQUEST_CODE = 123

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

                    // Check and request permission if not granted
                    if (ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            PERMISSION_REQUEST_CODE
                        )
                    } else {
                        // Permission already granted, proceed with location-related operations
                    // Call getCurrentWeather with the desired city
                    openWeatherMapViewModel.getCurrentWeather()
                    //Call getForecastWeather
                    openWeatherForecastViewModel.getForecastWeather()
                    }

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
                    UIUtils.WeatherUI(weatherState,forecatState)
                }
            }
        }
    }
}




@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun WeatherUISuccessPreview() {
    val successState = Resource.Success(getMockWeatherResponse())
    val successForecastState = Resource.Success(getMockForecastResponse())

    UIUtils.WeatherUI(successState,successForecastState)
    UIUtils.ForecastUI(successForecastState)
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
            73,0.5, 1708862520,1708824420,1036,
            1708736117, 1708772966,
            Temp(-19.14,-16.77,-27.53,-19.14,-20.0,-24.4),
            0.5, listOf(Weather(804, "Clouds", "пасмурно", "04n")) ,
            224,1.37,2.40) ),
        listOf(Hourly(99,-18.32,1708774497,-17.78, 95,
            1037,-21.2,0.0,10000,
            listOf(Weather(804, "Clouds", "пасмурно", "04n")),
            223,2.2,1.8)),
        56.0097, 92.79, "Asia/Krasnoyarsk", 25200
    )
}




