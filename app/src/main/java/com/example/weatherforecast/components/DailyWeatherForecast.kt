package com.example.weatherforecast.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.testing.TestNavHostController
import com.example.weatherforecast.R
import com.example.weatherforecast.response.Daily
import com.example.weatherforecast.theme.Blue500
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

    localContext.getString(R.string.feels_like) + ": " + WeatherUtils.updateTemperature(
        daily.feelsLike.day.toInt(), switchState
    )
    localContext.getString(R.string.feels_like) + ": " + WeatherUtils.updateTemperature(
        daily.feelsLike.night.toInt(), switchState
    )

    localContext.getString(R.string.wind) + ": " +
            WeatherUtils.updateWind(daily.windDeg.toString(), daily.windSpeed.toInt(), localContext)
    val sunrise = WeatherUtils.updateTime(daily.sunrise)
    val sunset = WeatherUtils.updateTime(daily.sunset)
    val moonrise = WeatherUtils.updateTime(daily.moonrise)
    val moonset = WeatherUtils.updateTime(daily.moonset)
    val moonPhase = daily.moonPhase

    val timeOfDawn=daily.sunrise
    val timeOfDusk=daily.sunset
    val timeOfDawnAndDusk= WeatherUtils.calculateDawnAndDusk(timeOfDawn,timeOfDusk)//it returns array of two elements


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = WeatherUtils.updateDateToToday(daily.dt)) },
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
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Blue500),
        ) {
            item{

                DailyWeatherCard(daily = daily)

            }//item1

            item {


                Row(
                    modifier = Modifier
                        .padding(all = 3.dp)
                        .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround
                ) {

                    HumidityCard(humidity = daily.humidity, dewPoint =daily.dewPoint.toInt() )
                    WindSpeedCard(speed = daily.windSpeed.toInt(), windDegree = daily.windDeg)

                }
            }// item 2
            item{
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 5.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {

                    UVIndexCard(index = daily.uvi.toInt())
                    PressureCard(pressure = daily.pressure)

                }

            }//item3
            item{
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 3.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
 /*                   SunriseSunsetCard(
                        sunrise =sunrise ,
                        sunset = sunset,
                        dawn = sunrise, dusk = sunset)
*/
                    timeOfDawnAndDusk[0]?.let {
                        timeOfDawnAndDusk[1]?.let { it1 ->
                            SunriseSunsetCard(sunrise = sunrise, sunset = sunset,
                                dawn = it, dusk = it1
                            )
                        }
                    }

                    MoonriseMoonsetCard(moonrise = moonrise, moonset =moonset , moonPhase =moonPhase )

                }
                //sunset sun rise


            }//4
        }
    }
}

@Composable
fun getMockNavController(): NavController {
    val context = LocalContext.current
    return TestNavHostController(context)
}

@Preview(showBackground = true, device = "spec:width=720dp,height=1440dp",
    apiLevel = 30, locale = "ru "
)

@Composable
fun DailyWeatherPreview(){
    val dailyData = UIUtils.getMockDailyWeather()
    val navController= getMockNavController()
    DailyWeatherForecast(daily = dailyData, navController = navController)
}
