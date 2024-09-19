package com.example.weatherforecast.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.navigation.NavController
import androidx.navigation.testing.TestNavHostController


import coil.compose.AsyncImage
import com.example.weatherforecast.R
import com.example.weatherforecast.response.Daily
import com.example.weatherforecast.theme.Blue500
import com.example.weatherforecast.theme.QuickSandTypography
import com.example.weatherforecast.utils.UIUtils
import com.example.weatherforecast.utils.WeatherUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyWeatherForecast(
    navController: NavController,
    daily: Daily,
) {
    val localContext = LocalContext.current //To access the context within a Composable function,
    // use the LocalContext provided by Jetpack Compose
    //we need this context to load  string values form strings.xml
    val switchState by DataStoreManager.tempSwitchPrefFlow(localContext)
        .collectAsState(initial = false)

    val feelsLike =
        localContext.getString(R.string.feels_like) + ": " + WeatherUtils.updateTemperature(
            daily.feelsLike.day.toInt(), switchState
        )
    val feelsLikeNight =
        localContext.getString(R.string.feels_like) + ": " + WeatherUtils.updateTemperature(
            daily.feelsLike.night.toInt(), switchState
        )

    val wind = localContext.getString(R.string.wind) + ": " +
            WeatherUtils.updateWind(daily.windDeg.toString(), daily.windSpeed.toInt(), localContext)
    val sunrise =
        "${localContext.getString(R.string.sunrise)}: " + WeatherUtils.updateTime(daily.sunrise)
    val sunset =
        "${localContext.getString(R.string.sunset)}: " + WeatherUtils.updateTime(daily.sunset)
    val moonrise =
        "${localContext.getString(R.string.moonrise)}: " + WeatherUtils.updateTime(daily.moonrise)
    val moonset =
        "${localContext.getString(R.string.moonset)}: " + WeatherUtils.updateTime(daily.moonset)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "${WeatherUtils.updateDateToToday(daily.dt)}") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Blue500),
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically

            ) {
                AsyncImage(
                    model = "${UIUtils.iconurl}${daily.weather[0].icon}.png",
                    contentDescription = "Weather icon",
                    modifier = Modifier
                        .size(150.dp)
                        .padding(all = 3.dp)
                )

                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.padding(all = 1.dp)
                ) {
                    Text(
                        text = localContext.getString(R.string.day) + ": " + WeatherUtils.updateTemperature(
                            daily.temp.day.toInt(),
                            switchState
                        ),
                        fontWeight = FontWeight.Bold, color = Color.White,
                        style = QuickSandTypography.body2,
                        modifier = Modifier.padding(all = 3.dp)
                    )
                    Text(
                        text = feelsLike,
                        color = Color.White,
                        style = QuickSandTypography.subtitle2,
                        modifier = Modifier.padding(all = 3.dp)
                    )
                    Text(
                        text = daily.weather[0].description,
                        color = Color.White,
                        style = QuickSandTypography.subtitle2,
                        modifier = Modifier.padding(all = 3.dp)
                    )

                }

                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.padding(all = 1.dp)
                ) {
                    Text(
                        text = localContext.getString(R.string.night) + ": "
                                + WeatherUtils.updateTemperature(
                            daily.temp.night.toInt(),
                            switchState
                        ),
                        fontWeight = FontWeight.Bold, color = Color.White,
                        style = QuickSandTypography.body1,
                        modifier = Modifier.padding(all = 3.dp)
                    )
                    Text(
                        text = feelsLikeNight,
                        color = Color.White,
                        style = QuickSandTypography.subtitle2,
                        modifier = Modifier.padding(all = 3.dp)
                    )
                    Text(

                        text = localContext.getString(R.string.pressure) + ": "
                                + daily.pressure.toString(),
                        color = Color.White,
                        style = QuickSandTypography.subtitle2,
                        modifier = Modifier.padding(all = 3.dp)
                    )

                }

            }
            Row(
                modifier = Modifier
                    .padding(all = 1.dp)
                    .fillMaxWidth()
            ) {

                Text(
                    text = localContext.getString(R.string.humidity) + ": "
                            + daily.humidity + "%",
                    color = Color.White,
                    style = QuickSandTypography.subtitle2,
                    modifier = Modifier.padding(all = 3.dp)
                )

            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = moonrise,
                    color = Color.White,
                    style = QuickSandTypography.subtitle2,
                    modifier = Modifier.padding(all = 3.dp)
                )
                Text(
                    text = moonset,
                    color = Color.White,
                    style = QuickSandTypography.subtitle2,
                    modifier = Modifier.padding(all = 3.dp)
                )
                Text(
                    text = WeatherUtils.calculateMoonPhase(localContext, daily.moonPhase),
                    color = Color.White,
                    style = QuickSandTypography.subtitle2,
                    modifier = Modifier.padding(all = 3.dp)
                )


            }
//wind speed and directions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = wind,
                    color = Color.White,
                    style = QuickSandTypography.subtitle1,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 1.dp)
                )
            }
            //sunset sun rise
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = sunrise,
                    color = Color.White,
                    style = QuickSandTypography.h6,
                    modifier = Modifier.padding(all = 1.dp)
                )
                Text(
                    text = sunset,
                    color = Color.White,
                    style = QuickSandTypography.h6,
                    modifier = Modifier.padding(all = 1.dp)
                )

            }
        }
    }
}

@Composable
fun getMockNavController(): NavController {
    val context = LocalContext.current
    return TestNavHostController(context)
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp",
    apiLevel = 30, locale = "ru"
)

@Composable
fun DailyWeatherPreview(){
    val dailyData = UIUtils.getMockDailyWeather()
    val navController= getMockNavController()
     DailyWeatherForecast(daily = dailyData, navController = navController)
}
