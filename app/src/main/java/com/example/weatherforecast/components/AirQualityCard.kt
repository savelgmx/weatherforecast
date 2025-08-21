package com.example.weatherforecast.components

import androidx.compose.foundation.background
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
import com.example.weatherforecast.R
import com.example.weatherforecast.data.remote.AirVisualPollution
import com.example.weatherforecast.presentation.ui.theme.orange
import com.example.weatherforecast.presentation.ui.theme.white
import com.example.weatherforecast.theme.Blue800

@Composable
fun AirQualityCard(pollution: AirVisualPollution) {
    val context = LocalContext.current

    // Определяем индекс и уровень AQI
    val (aqiLevel, symbolIndex) = when {
        pollution.aqius <= 50 -> context.getString(R.string.aqi_good) to 0
        pollution.aqius <= 100 -> context.getString(R.string.aqi_moderate) to 1
        pollution.aqius <= 150 -> context.getString(R.string.aqi_unhealthy_sensitive) to 2
        pollution.aqius <= 200 -> context.getString(R.string.aqi_unhealthy) to 3
        pollution.aqius <= 300 -> context.getString(R.string.aqi_very_unhealthy) to 4
        else -> context.getString(R.string.aqi_hazardous) to 5
    }

    val aqiSymbols = context.resources.getStringArray(R.array.aqi_symbols_array)
    val aqiSymbol = aqiSymbols[symbolIndex]

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
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )

            CustomCircularProgressIndicator(
                modifier = Modifier
                    .size(85.dp)
                    .background(Blue800),
                initialValue = pollution.aqius,
                primaryColor = orange,
                secondaryColor = white,
                circleRadius = 80f,
                minValue = 0,
                maxValue = 500
            )

            Text(
                text = "${pollution.aqius} AQI\n$aqiLevel\n$aqiSymbol",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}
