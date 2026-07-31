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
import com.example.weatherforecast.domain.models.DailyWeather
import com.example.weatherforecast.domain.models.HourlyWeather
import com.example.weatherforecast.theme.AppShapes
import com.example.weatherforecast.theme.Blue600
import com.example.weatherforecast.theme.Blue700
import com.example.weatherforecast.theme.QuickSandTypography
import com.example.weatherforecast.utils.Resource
import com.example.weatherforecast.utils.WeatherComposables
import com.example.weatherforecast.utils.WeatherFormatter

@Composable
fun CurrentWeatherCard(
    weatherState: Resource<DailyWeather>,
    filteredCurrentWeatherList: List<HourlyWeather>
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
                    val firstHourly = filteredCurrentWeatherList.firstOrNull()
                    if (firstHourly != null) {
                        val localContext = LocalContext.current
                        val switchState by DataStoreManager.tempSwitchPrefFlow(localContext).collectAsState(initial = false)
                        val windSpeedUnits by DataStoreManager.windPrefFlow(localContext).collectAsState(initial = 0)

                        val temperature = WeatherFormatter.updateTemperature(firstHourly.temp.toInt(), switchState)
                        val pressure = WeatherComposables.updatePressure(firstHourly.pressure.toInt())
                    val feels_like = localContext.getString(R.string.feels_like) + " :" +
                                WeatherFormatter.updateTemperature(firstHourly.feelsLike.toInt(), switchState)
                    val wind = WeatherComposables.updateWind(
                            firstHourly.windDeg.toString(),
                            firstHourly.windSpeed.toInt(),
                        localContext
                    ) + ":  " +
                                WeatherFormatter.convertWindSpeed(firstHourly.windSpeed.toInt(), windSpeedUnits) +
                            " " + WeatherComposables.selectionWindSignature(selection = windSpeedUnits)
                        val icon = firstHourly.icon
                        val tempMax = weatherState.data?.tempMax?.let { WeatherFormatter.updateTemperature(it.toInt(), switchState) }
                        val tempMin = weatherState.data?.tempMin?.let { WeatherFormatter.updateTemperature(it.toInt(), switchState) }
                        val day = weatherState.data?.dt?.let { WeatherFormatter.updateDateToToday(it.toInt()) }
                        val now = localContext.getString(R.string.now)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                                text = day ?: "N/A",
                                color = Color.White,
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
                            }
                        Column {
                                val localIconName = icon.replace("-", "_")
                                val drawableId = localIconName.let {
                                    localContext.resources.getIdentifier(it, "drawable", localContext.packageName)
                                }
                                val imageModel = if (drawableId != 0) drawableId else R.drawable.default_icon
                            AsyncImage(
                                    model = imageModel,
                                contentDescription = "Weather icon",
                                modifier = Modifier
                                        .size(70.dp)
                                    .padding(all = 1.dp)
                            )
                            }
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                        text = "max: ${tempMax ?: "N/A"}  min: ${tempMin ?: "N/A"}",
                                    color = Color.White,
                                    style = QuickSandTypography.labelLarge,
                                    modifier = Modifier.padding(3.dp)
                                )
                                }
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
                                        text = "$pressure  ${WeatherComposables.updatePressureUnit()}",
                                    color = Color.White,
                                    style = QuickSandTypography.titleSmall,
                                    modifier = Modifier.padding(1.dp)
                                )
                                }
                    }
                        }
                    } else {
                        Text(
                            text = "No current weather data available",
                            color = Color.White,
                            modifier = Modifier.padding(10.dp)
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
        }
    }
}
