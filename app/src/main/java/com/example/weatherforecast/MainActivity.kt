package com.example.weatherforecast

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherforecast.response.WeatherResponse
import com.example.weatherforecast.ui.theme.WeatherforecastTheme
import com.example.weatherforecast.ui.viewmodel.OpenWeatherMapViewModel
import com.example.weatherforecast.utils.Resource
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
                    val openWeatherMapViewModel: OpenWeatherMapViewModel = viewModel()

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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (weatherState) {
            is Resource.Success -> {
                val temperature = weatherState.data?.main?.temp
                Greeting("Temperature: $temperature")
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
fun WeatherUIPreview() {
    val loadingState = Resource.Loading<WeatherResponse>()
    WeatherUI(loadingState)
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = name,
        modifier = modifier
    )
}

