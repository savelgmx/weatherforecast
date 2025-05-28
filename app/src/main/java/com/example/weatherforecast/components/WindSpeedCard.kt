package com.example.weatherforecast.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherforecast.R
import com.example.weatherforecast.theme.Blue800
import com.example.weatherforecast.theme.QuickSandTypography
import com.example.weatherforecast.utils.WeatherUtils

@Composable
fun WindSpeedCard(speed: Int, windDegree: Int) {
    val context = LocalContext.current
    val selectedWindOptions by DataStoreManager.windPrefFlow(context).collectAsState(initial = 0)
    Surface(
        modifier = Modifier
            .width(160.dp)
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        color = (Blue800),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(3.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(" ${context.getText(R.string.wind_speed)}",
                fontWeight = FontWeight.Medium,
                color = Color.White,
                style= QuickSandTypography.titleMedium
            )
            WindDirectionShape(windDegree = windDegree)
            Text("${WeatherUtils.convertWindSpeed(speed,selectedWindOptions)} ${WeatherUtils.selectionWindSignature(selection = selectedWindOptions)}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                style = QuickSandTypography.headlineSmall,
                color = Color.White
            )
            Text(
                WeatherUtils.updateWind(windDirection = windDegree.toString(), windSpeed = speed, context =context ),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                style = QuickSandTypography.headlineSmall,
                color = Color.White
            )
        }
    }
}

@Composable
fun WindDirectionShape(windDegree: Int) {
    Canvas(modifier = Modifier
        .size(64.dp)
        .padding(all = 6.dp)) {
        // Draw compass circle
        drawCircle(
            color = Color.White,
            radius = size.minDimension*0.6f,
            style = Stroke(width = 2f)
        )

        // Draw N, E, S, W labels
        drawContext.canvas.nativeCanvas.apply {
            val paint = android.graphics.Paint().apply {
                textSize = 26f
                color = android.graphics.Color.WHITE
            }
            paint.textAlign = android.graphics.Paint.Align.CENTER
            drawText("N", size.width / 2, 15f, paint)
            paint.textAlign = android.graphics.Paint.Align.RIGHT
            drawText("E", size.width - 2f, size.height / 2, paint)
            paint.textAlign = android.graphics.Paint.Align.CENTER
            drawText("S", size.width / 2, size.height - 10f, paint)
            paint.textAlign = android.graphics.Paint.Align.LEFT
            drawText("W", 10f, size.height / 2, paint)
        }

        // Define points
        val W = size.width
        val H = size.height
        val topCenter = Offset(W / 2, H / 4)
        val rightMiddle = Offset(W / 2 + 10, H / 2)
        val bottomCenter = Offset(W / 2, H * 3 / 4)
        val leftMiddle = Offset(W / 2 - 10, H / 2)

        // Define topPath (upper half of arrow)
        val topPath = Path().apply {
            moveTo(topCenter.x, topCenter.y)
            lineTo(rightMiddle.x, rightMiddle.y)
            lineTo(leftMiddle.x, leftMiddle.y)
            close()
        }

        // Define bottomPath (lower half of arrow)
        val bottomPath = Path().apply {
            moveTo(rightMiddle.x, rightMiddle.y)
            lineTo(bottomCenter.x, bottomCenter.y)
            lineTo(leftMiddle.x, leftMiddle.y)
            close()
        }

        rotate(degrees = windDegree.toFloat(), pivot = Offset(size.width / 2, size.height / 2)) {
            drawPath(
                path = topPath,
                color = Color.Blue,
                style = Fill
            )
            drawPath(
                path = bottomPath,
                color = Color.Red,
                style = Fill
            )
        }
    }
}
