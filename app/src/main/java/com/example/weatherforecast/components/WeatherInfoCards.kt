package com.example.weatherforecast.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weatherforecast.R
import com.example.weatherforecast.components.DataStoreManager
import com.example.weatherforecast.theme.orange
import com.example.weatherforecast.theme.White
import com.example.weatherforecast.theme.Blue800
import com.example.weatherforecast.theme.QuickSandTypography
import com.example.weatherforecast.utils.MoonPhaseCalculator
import com.example.weatherforecast.utils.WeatherComposables
import com.example.weatherforecast.utils.WeatherFormatter

/**
 * Card displaying humidity percentage and dew point.
 */
@Composable
fun HumidityCard(humidity: Int, dewPoint: Int) {
    val context = LocalContext.current
    val switchState by DataStoreManager.tempSwitchPrefFlow(context).collectAsState(initial = false)
    val dewPointValue = WeatherFormatter.updateTemperature(dewPoint, switchState)
    Surface(
        modifier = Modifier
            .width(160.dp)
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        color = Blue800,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(3.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = context.getString(R.string.humidity),
                fontWeight = FontWeight.Medium,
                color = Color.White,
                style = QuickSandTypography.titleMedium
            )
            CustomCircularProgressIndicator(
                modifier = Modifier
                    .size(85.dp)
                    .background(Blue800),
                initialValue = humidity,
                primaryColor = orange,
                        secondaryColor = White,
                circleRadius = 80f,
                valueName = "%"
            )
            Text(
                text = "${context.getString(R.string.dew_point)} $dewPointValue",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                style = QuickSandTypography.bodyMedium
            )
        }
    }
}

/**
 * Card displaying UV index value.
 */
@Composable
fun UVIndexCard(index: Int) {
    Surface(
        modifier = Modifier
            .width(160.dp)
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        color = Blue800,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = LocalContext.current.getString(R.string.uv_index),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            CustomCircularProgressIndicator(
                modifier = Modifier
                    .size(85.dp)
                    .background(Blue800),
                initialValue = index,
                primaryColor = orange,
                        secondaryColor = White,
                circleRadius = 80f,
                minValue = 0,
                maxValue = 13
            )
            Text(
                text = WeatherFormatter.updateUVLevel(LocalContext.current, index),
                color = Color.White,
                style = QuickSandTypography.labelLarge
            )
        }
    }
}

/**
 * Card displaying atmospheric pressure.
 */
@Composable
fun PressureCard(pressure: Int) {
    val context = LocalContext.current
    val pressureValue = WeatherComposables.updatePressure(pressureValue = pressure)
    val pressureUnit = WeatherComposables.updatePressureUnit()
    Surface(
        modifier = Modifier
            .width(160.dp)
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        color = Blue800,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = context.getString(R.string.pressure),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            CustomCircularProgressIndicator(
                modifier = Modifier
                    .size(85.dp)
                    .background(Blue800),
                initialValue = pressureValue,
                primaryColor = orange,
                        secondaryColor = White,
                circleRadius = 80f,
                minValue = WeatherComposables.updateMinMaxPressureValue(minMaxPressure = 870),
                maxValue = WeatherComposables.updateMinMaxPressureValue(minMaxPressure = 1033)
            )
            Text(
                text = "$pressureValue $pressureUnit",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

/**
 * Card displaying sunrise, sunset times with dawn and dusk info.
 */
@Composable
fun SunriseSunsetCard(
    sunrise: String,
    sunset: String,
    dawn: String,
    dusk: String,
    timezone: String
) {
    val context = LocalContext.current
    Surface(
        modifier = Modifier
            .width(160.dp)
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        color = Blue800,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(all = 5.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.padding(all = 1.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = context.getString(R.string.sunrise) +" "+ "\u2600\uFE0F",
                    color = Color.White,
                    style = QuickSandTypography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(
                modifier = Modifier.padding(all = 1.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = sunrise,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    style = QuickSandTypography.bodyMedium
                )
            }
            Row(
                modifier = Modifier.padding(all = 1.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = context.getString(R.string.sunset) +" "+"\uD83C\uDF19",
                    color = Color.White,
                    style = QuickSandTypography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(
                modifier = Modifier.padding(all = 1.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = sunset,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    style = QuickSandTypography.bodyMedium
                )
            }
            Row(
                modifier = Modifier.padding(all = 1.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = context.getString(R.string.day_duration_time) + " "
                            + WeatherFormatter.calculateDayDurationElapsedDayTimeAndSunIconProgress(sunrise, sunset, timezone)[0] + "\n",
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    style = QuickSandTypography.bodyMedium
                )
            }
        }
    }
}

/**
 * Card displaying moon phase value.
 */
@Composable
fun MoonriseMoonsetCard(moonPhase: Double) {
    val localContext = LocalContext.current
    Surface(
        modifier = Modifier
            .width(160.dp)
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        color = Blue800,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(3.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.padding(1.dp)) {
                val moonPhaseIconId = MoonPhaseCalculator.getMoonPhaseIconName(localContext, moonPhase)
                AsyncImage(
                    model = moonPhaseIconId,
                    contentDescription = "MoonPhase icon",
                    modifier = Modifier
                        .size(64.dp)
                        .padding(all = 1.dp)
                )
            }
            Row(modifier = Modifier.padding(1.dp)) {
                Text(
                    text = MoonPhaseCalculator.calculateMoonPhase(localContext, moonPhase),
                    color = Color.White,
                    fontWeight = FontWeight.Light,
                    style = QuickSandTypography.titleMedium,
                    modifier = Modifier.padding(all = 10.dp)
                )
            }
        }
    }
}
