package com.example.weatherforecast.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weatherforecast.R
import com.example.weatherforecast.data.remote.AirVisualPollution
import com.example.weatherforecast.theme.Blue800
import com.example.weatherforecast.utils.WeatherUtils

@Composable
fun AirQualityCard(pollution: AirVisualPollution) {
    val context = LocalContext.current

    // Определяем индекс и уровень AQI
    val aqiLevel = when {
        pollution.aqius <= 50 -> context.getString(R.string.aqi_good)
        pollution.aqius <= 100 -> context.getString(R.string.aqi_moderate)
        pollution.aqius <= 150 -> context.getString(R.string.aqi_unhealthy_sensitive)
        pollution.aqius <= 200 -> context.getString(R.string.aqi_unhealthy)
        pollution.aqius <= 300 -> context.getString(R.string.aqi_very_unhealthy)
        else -> context.getString(R.string.aqi_hazardous)
    }

    val aqiIconName= WeatherUtils.getAirQualityIconName(context,pollution.aqius)

    Surface(
        modifier = Modifier
            .width(160.dp)
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        color = Blue800,
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = context.getString(R.string.air_quality),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )

            AsyncImage(
                model = aqiIconName,
                contentDescription = "air quality icon",
                modifier = Modifier
                    .size(64.dp) // Define your desired width and height
                    .padding(all = 1.dp)
            )


            Text(

               // $aqiSymbol
                text = "${pollution.aqius} AQI\n$aqiLevel",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}
