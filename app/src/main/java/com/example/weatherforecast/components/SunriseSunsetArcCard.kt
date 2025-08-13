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
import androidx.compose.foundation.layout.width
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
import com.example.weatherforecast.theme.QuickSandTypography


@Composable
fun SunriseSunsetArcCard(sunrise: String, sunset: String) {
    val context = LocalContext.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 20.dp)
            .height(200.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF1A237E),  // Dark blue background (Blue800)
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Sun arc graphic
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Filled arc sector on left half
                    drawArc(
                        color = Color(0xFF64B5F6),
                        startAngle = -180f,
                        sweepAngle = 90f,
                        useCenter = true,
                        style = Fill,
                        topLeft = Offset(0f, 0f),
                        size = Size(size.width, size.height * 2)
                    )

                    // Full arc outline
                    drawArc(
                        color = Color(0xFF64B5F6),
                        startAngle = 0f,
                        sweepAngle = -180f,
                        useCenter = false,
                        style = Stroke(width = 4.dp.toPx()),
                        topLeft = Offset(0f, 0f),
                        size = Size(size.width, size.height * 2)
                    )

                    // Horizontal connecting line
                    drawLine(
                        color = Color(0xFFFFFFFF),  // white
                        start = Offset(size.width, size.height - 10f),
                        end = Offset(size.width , size.height - 10f),
                        strokeWidth = 4.dp.toPx()
                    )


                    // Yellow sun at top center
                    drawCircle(
                        color = Color.Yellow,
                        radius = 12.dp.toPx(),
                        center = Offset(size.width / 2, 0f)
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
