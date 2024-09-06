package com.example.weatherforecast.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.weatherforecast.theme.Blue800
import com.example.weatherforecast.utils.WeatherUtils

@Composable
fun MoonPhaseCard(dailyMoonPhase:Double) {

    val context = LocalContext.current
    Surface(
        modifier = Modifier
            .width(160.dp)
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        color = (Blue800),
        elevation = 8.dp
    ) {
       WeatherUtils.calculateMoonPhase(context,dailyMoonPhase)
    }
}