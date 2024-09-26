package com.example.weatherforecast.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun SinusoidalShape() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val path = Path()
        val amplitude = size.height / 2  // Smaller amplitude for the wave
        val frequency = 3 * PI / size.width  // Higher frequency for more waves
        val centerY = size.height / 2  // Y-coordinate of the centerline (y = 0)

        // Draw the sinusoid
        for (x in 0..size.width.toInt()) {
            val y = -sin(frequency * x) * amplitude  // Negate the Y-coordinate for inversion
            if (x == 0) {
                path.moveTo(x.toFloat(), centerY - y.toFloat())
            } else {
                path.lineTo(x.toFloat(), centerY - y.toFloat())
            }
        }

        // Close the path to create a filled area under the curve
        path.lineTo(size.width, centerY)
        path.lineTo(0f, centerY)
        path.close()

        // Draw the filled sinusoidal shape with a gradient
        drawPath(
            path = path,
            brush = Brush.horizontalGradient(
                colors = listOf(Color(0xFF00B7FF), Color(0xFF051233))
            ),
            alpha = 0.6f // Set the alpha to match the transparency
        )

        // Draw the outline of the sinusoidal shape
        drawPath(path, color = Color.Blue.copy(alpha = 0.8f), style = Stroke(width = 4.dp.toPx()))

        // Draw the horizontal line on the X-axis (y = 0)
        drawLine(
            color = Color.Blue,
            start = androidx.compose.ui.geometry.Offset(0f, centerY),
            end = androidx.compose.ui.geometry.Offset(size.width, centerY),
            strokeWidth = 2.dp.toPx()
        )
    }
}

@Preview(showBackground = true,device = "spec:width=411dp,height=891dp")
@Composable
fun SinusPreview() {
    SinusoidalShape()
}
