package com.example.weatherforecast.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.weatherforecast.R
import com.example.weatherforecast.theme.Blue800
import com.example.weatherforecast.theme.QuickSandTypography
import kotlinx.coroutines.delay
import java.util.Calendar
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

@Composable
fun SunriseSunsetArcCard(sunrise: String, sunset: String) {
    val context = LocalContext.current

    val sunriseParts = sunrise.split(":")
    val sunriseHour = sunriseParts[0].toInt()
    val sunriseMinute = sunriseParts[1].toInt()
    val sunriseMinutes = sunriseHour * 60 + sunriseMinute

    val sunsetParts = sunset.split(":")
    val sunsetHour = sunsetParts[0].toInt()
    val sunsetMinute = sunsetParts[1].toInt()
    val sunsetMinutes = sunsetHour * 60 + sunsetMinute

    val totalMinutes = sunsetMinutes - sunriseMinutes

    val currentMinutes = remember { mutableStateOf(getCurrentMinutes()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentMinutes.value = getCurrentMinutes()
            delay(60000L) // Update every minute
        }
    }

    var elapsedMinutes = currentMinutes.value - sunriseMinutes
    var progress = 0f
    if (totalMinutes > 0) {
        progress = max(0f, min(1f, elapsedMinutes.toFloat() / totalMinutes))
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 20.dp)
            .height(200.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color(Blue800.value),  // Dark blue background (Blue800)
        elevation = 16.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Sun arc graphic
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(all=10.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Filled arc sector from sunrise to current
                    drawArc(
                        color = Color(0xFF64B5F6),
                        startAngle = 180f,
                        sweepAngle = 180f * progress,
                        useCenter = true,
                        style = Fill,
                        topLeft = Offset(0f, 0f),
                        size = Size(size.width, size.height * 2)
                    )
                    drawArc(
                        color = Color(0xFF64B5F6),
                        startAngle = 0f,
                        sweepAngle = -180f,
                        useCenter = false,
                        style = Stroke(width = 4.dp.toPx()),
                        topLeft = Offset(0f, 0f),
                        size = Size(size.width, size.height * 2)
                    )

                    // Horizontal connecting line (fixed the typo, assuming it's from 0 to width)
                    drawLine(
                        color = Color(0xFFFFFFFF),  // white
                        start = Offset(0f, size.height - 10f),
                        end = Offset(size.width, size.height - 10f),
                        strokeWidth = 4.dp.toPx()
                    )

                    // Yellow sun at calculated position
                    val centerX = size.width / 2
                    val centerY = size.height
                    val radiusX = size.width / 2
                    val radiusY = size.height
                    val theta = 180f - 180f * progress
                    val angleRad = (theta * PI / 180).toDouble()
                    val sunX = centerX + radiusX * cos(angleRad).toFloat()
                    val sunY = centerY - radiusY * sin(angleRad).toFloat()
                    drawCircle(
                        color = Color.Yellow,
                        radius = 12.dp.toPx(),
                        center = Offset(sunX, 0f)
                    )
                }
            }

            // Bottom labels: sunrise left,sunset right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        context.getString(R.string.sunrise) + ":",
                        color = Color.White,
                        style = QuickSandTypography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(sunrise, color = Color.White, style = QuickSandTypography.bodyMedium)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        context.getString(R.string.sunset) + ":",
                        color = Color.White,
                        style = QuickSandTypography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(sunset, color = Color.White, style = QuickSandTypography.bodyMedium)
                }
            }
        }
    }
}

private fun getCurrentMinutes(): Int {
    val cal = Calendar.getInstance()
    return cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
}