package com.example.weatherforecast.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.example.weatherforecast.response.Daily
import com.example.weatherforecast.theme.AppShapes
import com.example.weatherforecast.theme.Blue500
import com.example.weatherforecast.theme.Blue700
import com.example.weatherforecast.theme.QuickSandTypography
import com.example.weatherforecast.utils.UIUtils
import com.example.weatherforecast.utils.WeatherUtils

@Composable
fun DailyWeatherCard(
    daily: Daily
)
{

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

    val icon = daily.weather[0].icon
    val localIconName = icon.replace("-", "_")
    val drawableId = localContext.resources.getIdentifier(localIconName, "drawable",localContext. packageName)
    val imageModel = if (drawableId != 0) drawableId else R.drawable.default_icon

    Box(
        modifier = (Modifier.background(
            Blue500,
            shape = AppShapes.large
        )
                )
            .fillMaxWidth()
            .padding(all = 20.dp)) {

        Column(
            modifier = (Modifier.background(
                Blue700,
                shape = AppShapes.large
            )
                    )
                .fillMaxWidth()
                .padding(all = 2.dp)
        ) {


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height = 4.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically

            ) {

            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height = 160.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically

            ) {



                Column {
                    AsyncImage(
                        model = imageModel, //"${UIUtils.iconurl}${daily.weather[0].icon}.png",
                        contentDescription = "Weather icon",
                        modifier = Modifier
                            .size(150.dp)
                            .padding(all = 3.dp)
                    )
                }//column# weather icon

                Column {

                    Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.Top) {
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

                    }

                    Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.Top) {
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
                    }

                    Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.Top)  {

                        Text(
                            text = daily.weather[0].description,
                            color = Color.White,
                            style = QuickSandTypography.subtitle2,
                            modifier = Modifier.padding(all = 3.dp)
                        )

                    }

                }

            }
        }

    }


}


@Preview(showBackground = true, device = "spec:width=720dp,height=1440dp",
    apiLevel = 33, locale = "ru "
)

@Composable
fun DailyWeatherCardPreview(){
    val dailyData = UIUtils.getMockDailyWeather()
    DailyWeatherCard(daily = dailyData)
}
