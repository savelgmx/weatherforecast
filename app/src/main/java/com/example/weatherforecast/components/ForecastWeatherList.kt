package com.example.weatherforecast.components


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.weatherforecast.R
import com.example.weatherforecast.domain.models.DailyWeather
import com.example.weatherforecast.presentation.viewmodels.SettingsViewModel
import com.example.weatherforecast.theme.AppShapes
import com.example.weatherforecast.theme.Blue600
import com.example.weatherforecast.theme.Blue700
import com.example.weatherforecast.theme.QuickSandTypography
import com.example.weatherforecast.utils.UIUtils
import com.example.weatherforecast.utils.WeatherFormatter

@Composable
fun ForecastWeatherList(
    dailyForecastList: List<DailyWeather>,
    navController: NavController
) {
    val dailyForecast = dailyForecastList
    val count = dailyForecast.size


    Column(
        modifier = Modifier.fillMaxSize()
            .padding(all=16.dp)
            .border(width = 3.dp, color = Blue600, shape = AppShapes.large),
    ) {


        for (index in 0 until count) {
            dailyForecast.getOrNull(index)?.let { daily ->
            ClickableDayForecastItem(index = index, daily = daily, navController = navController)
            }
        }
    }
}
@Composable
fun ClickableDayForecastItem(
    index: Int,
    daily: DailyWeather,
    navController: NavController,
    // Review item 5: temperature preference via the shared SettingsViewModel.
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
           // .padding(all = 1.dp)
            .clickable {
                navController.navigate("detail/$index")
            }  ,       shape = AppShapes.large

    ) {

        val localContext = LocalContext.current
        val switchState by settingsViewModel.tempSwitch.collectAsStateWithLifecycle()

        val icon = daily.icon
        val localIconName = icon.replace("-", "_")
        val drawableId = localContext.resources.getIdentifier(localIconName, "drawable",localContext. packageName)
        val imageModel = if (drawableId != 0) drawableId else R.drawable.default_icon


        Column(modifier = Modifier
            .padding(all=1.dp)
            .background(Blue700)
        )
        {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically

            ) {

                Text(

                    text = WeatherFormatter.updateDateToToday(daily.dt.toInt()),
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    style = QuickSandTypography.bodyLarge,
                    modifier = Modifier
                        .padding(all = 3.dp)
                )
                AsyncImage(
                    model = imageModel,
                    contentDescription = "Weather icon",
                    modifier = Modifier
                        .size(40.dp)// Define your desired width and height
                        .padding(all = 3.dp)
                )

                Text(
                    text = "${WeatherFormatter.updateTemperature(daily.tempMax.toInt(), switchState)}/${WeatherFormatter.updateTemperature(
                        daily.tempMin.toInt(),
                        switchState
                    )}",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    style = QuickSandTypography.bodyLarge,
                    modifier = Modifier.padding(all =3.dp)
                )
            }

        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun ForecastUISuccessPreview() {
    ForecastWeatherList(dailyForecastList = emptyList(), navController = rememberNavController())
}
