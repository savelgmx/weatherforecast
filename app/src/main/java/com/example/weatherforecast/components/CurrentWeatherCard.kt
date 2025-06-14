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
import com.example.weatherforecast.response.Hourly
import com.example.weatherforecast.response.WeatherResponse
import com.example.weatherforecast.theme.AppShapes
import com.example.weatherforecast.theme.Blue600
import com.example.weatherforecast.theme.Blue700
import com.example.weatherforecast.theme.QuickSandTypography
import com.example.weatherforecast.utils.Resource
import com.example.weatherforecast.utils.UIUtils
import com.example.weatherforecast.utils.WeatherUtils

@Composable
fun CurrentWeatherCard(
    weatherState: Resource<WeatherResponse>,
    filteredCurrentWeatherList: List<Hourly>
) {

    Box(
        modifier = (Modifier.background(
            Blue600,
            shape = AppShapes.large
        )
                )
            .fillMaxWidth()
            .padding(all = 20.dp)
    ) {

        Column(
            modifier = (Modifier.background(
                Blue700,
                shape = AppShapes.large
            )
                    )
                .fillMaxWidth()
                .padding(all = 2.dp)
        ) {
            when (weatherState) {
                is Resource.Success -> {
                    val localContext =
                        LocalContext.current //To access the context within a Composable function, use the LocalContext provided by Jetpack Compose
                    val switchState by DataStoreManager.tempSwitchPrefFlow(localContext)
                        .collectAsState(initial = false)
                    val windSpeedUnits by DataStoreManager.windPrefFlow(localContext)
                        .collectAsState(initial = 0)

                    val temperature = WeatherUtils.updateTemperature(
                        filteredCurrentWeatherList[0].temp.toInt(),
                        switchState
                    )
                    val tempMax = weatherState.data?.main?.temp_max?.let {
                        WeatherUtils.updateTemperature(it.toInt(), switchState)
                    }
                    val tempMin = weatherState.data?.main?.temp_min?.let {
                        WeatherUtils.updateTemperature(it.toInt(), switchState)
                    }

                    weatherState.data?.name
                    val day =
                        weatherState.data?.dt?.let { WeatherUtils.updateDateToToday(it.toInt()) }

                    val pressure =
                        WeatherUtils.updatePressure(filteredCurrentWeatherList[0].pressure)
                    val pressureUnit = WeatherUtils.updatePressureUnit()
                    val feels_like = localContext.getString(R.string.feels_like) + " :" +
                            WeatherUtils.updateTemperature(
                                filteredCurrentWeatherList[0].feelsLike.toInt(),
                                switchState
                            )

                    val now=localContext.getString(R.string.now)

                    val wind = WeatherUtils.updateWind(
                        filteredCurrentWeatherList[0].windDeg.toString(),
                        filteredCurrentWeatherList[0].windSpeed.toInt(),
                        localContext
                    ) + ":  " +
                            WeatherUtils.convertWindSpeed(
                                filteredCurrentWeatherList[0].windSpeed.toInt(),
                                windSpeedUnits
                            ) +
                            " " + WeatherUtils.selectionWindSignature(selection = windSpeedUnits)


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = day!!, color = Color.White,
                            style = QuickSandTypography.headlineMedium,
                            modifier = Modifier.padding(end = 1.dp)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = now,
                            color=Color.White,
                            style = QuickSandTypography.labelLarge,
                            modifier = Modifier.padding(start = 10.dp)
                        )

                        Text(
                            text = feels_like,
                            color = Color.White,
                            style = QuickSandTypography.titleMedium,
                            modifier = Modifier.padding(1.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    )
                    {


                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ) {
                            Text(
                                text = " $temperature",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = QuickSandTypography.headlineLarge,
                                modifier = Modifier.padding(start = 3.dp)
                            )

                        } //column#1 inside temperature


                        Column {
                            val icon =
                                filteredCurrentWeatherList[0].weather[0].icon //weatherState.data?.weather?.get(0)?.icon
                            val localIconName = icon.replace("-", "_")
                            val drawableId = localContext.resources.getIdentifier(
                                localIconName,
                                "drawable",
                                localContext.packageName
                            )
                            val imageModel =
                                if (drawableId != 0) drawableId else R.drawable.default_icon

                            AsyncImage(
                                model = imageModel,// "${UIUtils.iconurl}$icon.png",
                                contentDescription = "Weather icon",
                                modifier = Modifier
                                    .size(70.dp) // Define your desired width and height
                                    .padding(all = 1.dp)
                            )

                        } //column#2 weather icon


                        Column {


                            // Row 2: Temperature with Weather Icon

                            /*
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.Start,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Text(
                                                                text = "Now ",
                                                                color=Color.White,
                                                                style = QuickSandTypography.labelLarge,
                                                                modifier = Modifier.padding(start = 2.dp)
                                                            )

                                                            Text(
                                                                text = feels_like,
                                                                color = Color.White,
                                                                style = QuickSandTypography.titleMedium,
                                                                modifier = Modifier.padding(1.dp)
                                                            )
                                                        }
                            */
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "max: $tempMax  min: $tempMin",
                                    color = Color.White,
                                    style = QuickSandTypography.labelLarge,
                                    modifier = Modifier.padding(3.dp)
                                )
                            }//t max t min


                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = wind,
                                    color = Color.White,
                                    style = QuickSandTypography.titleMedium,
                                    modifier = Modifier.padding(1.dp)
                                )

                            }
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$pressure  $pressureUnit",
                                    color = Color.White,
                                    style = QuickSandTypography.titleSmall,
                                    modifier = Modifier.padding(1.dp)
                                )

                            }//Column#3

                        }//row


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
    val filteredCurrentWeatherList=UIUtils.getMockHourlylist()
    CurrentWeatherCard(successState, filteredCurrentWeatherList)
}

