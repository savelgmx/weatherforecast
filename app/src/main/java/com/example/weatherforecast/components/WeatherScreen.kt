package com.example.weatherforecast.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WeatherScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE1F5FE))
            .padding(16.dp)
    ) {
        Text("Погода сейчас", fontWeight = FontWeight.Bold, fontSize = 24.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            WindSpeedCard(speed = 11, windDegree = 135)
            HumidityCard(humidity = 35, dewPoint = 12)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            UVIndexCard(index = 5)
            PressureCard(pressure = 1010)
        }

        Spacer(modifier = Modifier.height(16.dp))

        SunriseSunsetCard(sunrise = "04:04", sunset = "21:39", dawn = "03:02", dusk = "22:41")
    }
}

@Composable
fun WindSpeedCard(speed: Int, windDegree: Int) {
    Surface(
        modifier = Modifier
            .width(160.dp)
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Скорость ветра", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            WindDirectionShape(windDegree = windDegree)
            Text("$speed км/ч", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colors.primary)
            Text("Юго-восточный", fontSize = 14.sp)
        }
    }
}

@Composable
fun WindDirectionShape(windDegree: Int) {
    Canvas(modifier = Modifier.size(64.dp)) {
        // Draw compass circle
        drawCircle(
            color = Color.Black,
            radius = size.minDimension*0.6f,
            style = Stroke(width = 4f)
        )

        // Draw N, E, S, W labels
        drawContext.canvas.nativeCanvas.apply {
            drawText("N", size.width / 2, 30f, android.graphics.Paint().apply { textAlign = android.graphics.Paint.Align.CENTER;textSize=36f})
            drawText("E", size.width - 20f, size.height / 2, android.graphics.Paint().apply { textAlign = android.graphics.Paint.Align.RIGHT;textSize=36f })
            drawText("S", size.width / 2, size.height - 10f, android.graphics.Paint().apply { textAlign = android.graphics.Paint.Align.CENTER;textSize=36f })
            drawText("W", 20f, size.height / 2, android.graphics.Paint().apply { textAlign = android.graphics.Paint.Align.LEFT;textSize=36f })
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
                alpha = 0.5f
            )
        }
    }
}

@Composable
fun HumidityCard(humidity: Int, dewPoint: Int) {
    Surface(
        modifier = Modifier
            .width(160.dp)
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Влажность", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            HumidityShape(humidity)
            Text("$humidity%", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colors.primary)
            Text("Точка росы $dewPoint°", fontSize = 14.sp)
        }
    }
}

@Composable
fun HumidityShape(humidity: Int) {
    Canvas(modifier = Modifier.size(64.dp)) {
        val height = size.height * humidity / 100f
        drawRoundRect(
            color = Color.Yellow,
            topLeft = Offset(0f, size.height - height),
            size = Size(size.width, height),
            cornerRadius = CornerRadius(8f)
        )
    }
}

@Composable
fun UVIndexCard(index: Int) {
    Surface(
        modifier = Modifier
            .width(160.dp)
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text("УФ-индекс", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            UVIndexShape(index)
            Text("$index", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colors.primary)
            Text("Умеренный", fontSize = 14.sp)
        }
    }
}

@Composable
fun UVIndexShape(index: Int) {
    Canvas(modifier = Modifier.size(64.dp)) {
        val sweepAngle = index * 30f
        drawArc(
            color = Color.Yellow,
            startAngle = 0f,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(width = 8f, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun PressureCard(pressure: Int) {
    Surface(
        modifier = Modifier
            .width(160.dp)
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Давление", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            PressureShape(pressure)
            Text("$pressure мбар", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colors.primary)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Низкое", fontSize = 12.sp)
                Text("Высокое", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun PressureShape(pressure: Int) {
    Canvas(modifier = Modifier.size(64.dp)) {
        val position = (pressure - 900) / 200f * size.width
        drawLine(
            color = Color.Blue,
            start = Offset(position, 0f),
            end = Offset(position, size.height),
            strokeWidth = 4f
        )
    }
}

@Composable
fun SunriseSunsetCard(sunrise: String, sunset: String, dawn: String, dusk: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Восход и закат", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            SunriseSunsetShape()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Восход", fontSize = 14.sp)
                    Text(sunrise, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colors.primary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Закат", fontSize = 14.sp)
                    Text(sunset, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colors.primary)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Рассвет", fontSize = 14.sp)
                    Text(dawn, fontSize = 14.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Закат", fontSize = 14.sp)
                    Text(dusk, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun SunriseSunsetShape() {
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(64.dp)
    ) {
        val sunSize = 16.dp.toPx()
        val width = size.width
        val height = size.height
        val sunY = height / 2

        drawLine(
            color = Color.Gray,
            start = Offset(0f, sunY),
            end = Offset(width, sunY),
            strokeWidth = 2f
        )

        val sunriseOffset = width * 0.2f
        val sunsetOffset = width * 0.8f

        drawCircle(
            color = Color.Yellow,
            radius = sunSize / 2,
            center = Offset(sunriseOffset, sunY - sunSize)
        )
        drawCircle(
            color = Color.Yellow,
            radius = sunSize / 2,
            center = Offset(sunsetOffset, sunY - sunSize)
        )

        val path = Path().apply {
            moveTo(sunriseOffset, sunY)
            quadraticBezierTo(width / 2, sunY - sunSize * 2, sunsetOffset, sunY)
        }
        drawPath(path, color = Color.Yellow, style = Stroke(width = 4f))
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WeatherScreen()
}
