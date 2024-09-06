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
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
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
                style= QuickSandTypography.subtitle2
            )
            WindDirectionShape(windDegree = windDegree)
            Text("${WeatherUtils.convertWindSpeed(speed,selectedWindOptions)} ${WeatherUtils.selectionWindSignature(selection = selectedWindOptions)}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                style = QuickSandTypography.h4,
                color = Color.White
            )
            Text(
                WeatherUtils.updateWind(windDirection = windDegree.toString(), windSpeed = speed, context =context ),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                style = QuickSandTypography.h4,
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
            drawText("N", size.width / 2, 15f, android.graphics.Paint().apply { textAlign = android.graphics.Paint.Align.CENTER;textSize=26f;(Color.White)})
            drawText("E", size.width - 2f, size.height / 2, android.graphics.Paint().apply { textAlign = android.graphics.Paint.Align.RIGHT;textSize=26f;(Color.White) })
            drawText("S", size.width / 2, size.height - 10f, android.graphics.Paint().apply { textAlign = android.graphics.Paint.Align.CENTER;textSize=26f;(Color.White) })
            drawText("W", 10f, size.height / 2, android.graphics.Paint().apply { textAlign = android.graphics.Paint.Align.LEFT;textSize=26f;(Color.White) })
        }

        // Draw wind direction arrow
        val arrowPath = Path().apply {
            moveTo(size.width / 2, size.height / 4)
            lineTo(size.width / 2 + 10, size.height / 2)
            lineTo(size.width / 2, size.height * 3 / 4)
            lineTo(size.width / 2 - 10, size.height / 2)
            close()
        }

        rotate(degrees = windDegree.toFloat(), pivot = Offset(size.width / 2, size.height / 2)) {
            drawPath(
                path = arrowPath,
                color = Color.Blue,
                style = Stroke(width = 1f)
            )
            drawPath(
                path = arrowPath,
                color = Color.Red,
                alpha = 1.0f,
                blendMode = BlendMode.Color
            )
        }
    }
}
