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
import java.lang.Math.toRadians
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.sin

/**
 * Composable function to display a card showing sunrise and sunset times with a dynamic arc graphic
 * representing the sun's position based on current time.
 *
 * @param sunrise Sunrise time in "HH:MM" format.
 * @param sunset Sunset time in "HH:MM" format.
 */
@Composable
fun SunriseSunsetArcCard(sunrise: String, sunset: String) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
    val currentMinute = calendar.get(Calendar.MINUTE)
    val currentMinutes = currentHour * 60 + currentMinute

    val sunriseParts = sunrise.split(":")
    val sunriseHour = sunriseParts[0].toInt()
    val sunriseMinute = sunriseParts[1].toInt()
    val sunriseMinutes = sunriseHour * 60 + sunriseMinute

    val sunsetParts = sunset.split(":")
    val sunsetHour = sunsetParts[0].toInt()
    val sunsetMinute = sunsetParts[1].toInt()
    val sunsetMinutes = sunsetHour * 60 + sunsetMinute

    // Calculate total day length in minutes and elapsed time since sunrise
    val totalMinutes = sunsetMinutes - sunriseMinutes
    val elapsedMinutes = currentMinutes - sunriseMinutes
    // Compute progress as a fraction (0 to 1) of the day, clamped between 0 and 1
    val progress = if (totalMinutes > 0) (elapsedMinutes.coerceIn(0, totalMinutes).toFloat() / totalMinutes) else 0f

    // Calculate day duration
    val dayHours = totalMinutes / 60
    val dayMinutes = totalMinutes % 60
    val dayDuration = "$dayHours:$dayMinutes"

    // Calculate elapsed day time
    val elapsedHours = elapsedMinutes.coerceIn(0, totalMinutes) / 60
    val elapsedMins = elapsedMinutes.coerceIn(0, totalMinutes) % 60
    val elapsedDayTime = "$elapsedHours :$elapsedMins"

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
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Main row: left column for sunrise/sunset, right box for arc
            Row(
                modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
                // Column on left with two rows for sunrise and sunset times
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.height(100.dp)
            ) {
                Column {
                    Text(
                        context.getString(R.string.sunrise) + ":",
                        color = Color.White,
                        style = QuickSandTypography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(sunrise, color = Color.White, style = QuickSandTypography.bodyMedium)
                }
                Column {
                    Text(
                        context.getString(R.string.sunset) + ":",
                        color = Color.White,
                        style = QuickSandTypography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(sunset, color = Color.White, style = QuickSandTypography.bodyMedium)
                }
            }

            // Sun arc graphic on right
            Box(modifier = Modifier
                .weight(1f)
                .height(100.dp)
                .padding(start = 16.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Filled arc sector from left based on progress
                    drawArc(
                        color = Color(0xFF64B5F6),
                        startAngle = 180f,
                        sweepAngle = 180f * progress,
                        useCenter = true,
                        style = Fill,
                        topLeft = Offset(0f, 0f),
                        size = Size(size.width, size.height * 2)
                    )

                    // Full half-circle arc outline (ensured as semicircle by sizing)
                    drawArc(
                        color = Color(0xFF64B5F6),
                        startAngle = 0f,
                        sweepAngle = -180f,
                        useCenter = false,
                        style = Stroke(width = 4.dp.toPx()),
                        topLeft = Offset(0f, 0f),
                        size = Size(size.width, size.height * 2)
                    )

                    // Horizontal horizon line
                    drawLine(
                        color = Color(0xFF64B5F6),
                        start = Offset(0f, size.height - 7f),
                        end = Offset(size.width, size.height - 7f),
                        strokeWidth = 5.dp.toPx()
                    )

                    // Dynamic sun positioning:
                    // Center of the arc oval is at (width/2, height), with radii rx = width/2, ry = height.
                    // Theta starts at 180° (left) and increases to 360° (right) as progress goes from 0 to 1.
                    // This maps to moving clockwise from left bottom, up to top (270°), down to right bottom.
                    // Using trigonometric functions: x = cx + rx * cos(theta), y = cy + ry * sin(theta).
                    // Since screen y increases downward, negative sin moves upward.
                    // This ensures the sun follows exactly along the arc path.
                    val cx = size.width / 2f
                    val cy = size.height
                    val rx = size.width / 2f
                    val ry = size.height
                    val theta = 180f + 180f * progress
                    val radians = toRadians(theta.toDouble())
                    val sunX = cx + rx * cos(radians).toFloat()
                    val sunY = cy + ry * sin(radians).toFloat()
                    drawCircle(
                        color = Color.Yellow,
                        radius = 12.dp.toPx(),
                        center = Offset(sunX, sunY)
                    )
                }
            }
        }

            // Bottom row with day duration and elapsed day time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        context.getString(R.string.day_duration_time),
                        color = Color.White,
                        style = QuickSandTypography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(dayDuration, color = Color.White, style = QuickSandTypography.bodyMedium)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        context.getString(R.string.elapsed_day_time),
                        color = Color.White,
                        style = QuickSandTypography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(elapsedDayTime, color = Color.White, style = QuickSandTypography.bodyMedium)
                }
            }
        }
    }
}