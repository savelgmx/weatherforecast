package com.example.weatherforecast.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.weatherforecast.R
import com.example.weatherforecast.response.WeatherResponse
import com.example.weatherforecast.theme.AppShapes
import com.example.weatherforecast.theme.Blue300
import com.example.weatherforecast.theme.Blue700
import com.example.weatherforecast.theme.QuickSandTypography
import com.example.weatherforecast.utils.Resource
import com.example.weatherforecast.utils.UIUtils
import com.example.weatherforecast.utils.WeatherUtils

@Composable
fun CurrentWeatherCard(
    weatherState: Resource<WeatherResponse>
){

    Box(
        modifier =(Modifier.background(Blue300,
            shape = AppShapes.large)
                )
            .fillMaxWidth()
            .padding(all = 20.dp)) {

        Column(
            modifier =(Modifier.background(Blue700,
                shape = AppShapes.large)
                    )
                .fillMaxWidth()
                .padding(all = 2.dp)
        ) {
            when (weatherState) {
                is Resource.Success -> {
                    val localContext =
                        LocalContext.current //To access the context within a Composable function, use the LocalContext provided by Jetpack Compose
                    val switchState by DataStoreManager.tempSwitchPrefFlow(localContext).collectAsState(initial = false)

                    val temperature = weatherState.data?.main?.temp?.let {
                        WeatherUtils.updateTemperature(it.toInt(),switchState)
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
                            WeatherUtils.updateTemperature(it.toInt(), switchState)
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
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = name!!, color = Color.White,
                            style = QuickSandTypography.h5,
                            modifier = Modifier.padding(start = 1.dp)
                        )

                        Text(
                            text = day!!, color = Color.White,
                            style = QuickSandTypography.h5,
                            modifier = Modifier.padding(end = 1.dp)
                        )
                    }

                    // Row 2: Temperature with Weather Icon

                    val icon = weatherState.data?.weather?.get(0)?.icon
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = " $temperature",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = QuickSandTypography.h3,
                            modifier = Modifier.padding(start = 8.dp)
                        )

                        AsyncImage(
                            model = "${UIUtils.iconurl}$icon.png",
                            contentDescription = "Weather icon",
                            modifier = Modifier
                                .size(40.dp) // Define your desired width and height
                                .padding(all = 1.dp)
                        )

                        Text(
                            text = " $feels_like",
                            color = Color.White,
                            style = QuickSandTypography.h5,
                            modifier = Modifier.padding(1.dp)
                        )
                    }



                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$wind",
                            color = Color.White,
                            style = QuickSandTypography.h6,
                            modifier = Modifier.padding(1.dp)
                        )

                    }
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = " $pressure",
                            color = Color.White,
                            style = QuickSandTypography.h6,
                            modifier = Modifier.padding(1.dp)
                        )

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

        } //colum
    }
}
@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun WeatherUISuccessPreview() {
    val successState = Resource.Success(UIUtils.getMockWeatherCard())
    CurrentWeatherCard( successState)
}

