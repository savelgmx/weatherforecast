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
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherforecast.R
import com.example.weatherforecast.theme.Blue800
import com.example.weatherforecast.theme.QuickSandTypography
import com.example.weatherforecast.utils.WeatherUtils

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
            UVIndexCard(index = 1)
            PressureCard(pressure = 1010)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            MoonriseMoonsetCard(moonrise = "17:00", moonset = "7:02", moonPhase = 0.43)
            SunriseSunsetCard(sunrise = "04:04", sunset = "21:39", dawn = "03:02", dusk = "22:41")
        }
    }
}

@Composable
fun HumidityCard(humidity: Int, dewPoint: Int) {
    val context= LocalContext.current
    val switchState    by DataStoreManager.tempSwitchPrefFlow(context).collectAsState(initial = false)
    val dewPointValue= WeatherUtils.updateTemperature(dewPoint,switchState)
    Surface(
        modifier = Modifier
            .width(160.dp)
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        color = (Blue800),
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(3.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                context.getString(R.string.humidity),
                fontWeight = FontWeight.Medium,
                color = Color.White,
                style= QuickSandTypography.subtitle1
            )
            HumidityShape(humidity)
            Text("$humidity%",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                style = QuickSandTypography.body2,
                color = Color.White)
            Text("${context.getString(R.string.dew_point)} $dewPointValue",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                style = QuickSandTypography.body2
            )
        }
    }
}

@Composable
fun HumidityShape(humidity: Int) {
    Canvas(modifier = Modifier
        .size(64.dp)
        .padding(all = 2.dp)
    ) {
        val height = size.height * humidity / 100f
        drawRoundRect(
            brush = Brush.horizontalGradient(
                colors = listOf(Color(0xFFFFFB07), Color(0xFFFF9800))
            ),
            topLeft = Offset(2f, size.height - height),
            size = Size(size.width, height),
            cornerRadius = CornerRadius(40f)
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
        color = (Blue800),
        elevation = 8.dp,
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(LocalContext.current.getString(R.string.uv_index), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White)
            UVIndexShape(index)
            Text("$index", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White, style = QuickSandTypography.body2)
            Text(WeatherUtils.updateUVLevel(LocalContext.current,index), color = Color.White, style = QuickSandTypography.h5)
        }
    }
}

@Composable
fun UVIndexShape(index: Int) {
    Canvas(modifier = Modifier.size(64.dp)) {
        val sweepAngle = index * 45f
        drawArc(
            color = Color.Yellow,
            startAngle = 0f,
            sweepAngle = sweepAngle,
            useCenter = true,
            style = Stroke(width = 8f, cap = StrokeCap.Square)
        )
    }
}

@Composable
fun PressureCard(pressure: Int) {
    val context= LocalContext.current
    val pressurevalue= WeatherUtils.updatePressure(pressureValue = pressure)

    Surface(
        modifier = Modifier
            .width(160.dp)
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        color = (Blue800),
        elevation = 8.dp,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween, horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(context.getString(R.string.pressure), fontSize = 16.sp, fontWeight = FontWeight.Medium,color=Color.White)
            PressureShape(pressure)
            Text("$pressurevalue", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun PressureShape(pressure: Int) {
    Canvas(modifier = Modifier
        .size(64.dp)
        .padding(all = 2.dp)) {
        val position = (pressure - 900) / 200f * size.width
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFFFFB07), Color(0xFFFF9800))
            ),
            topLeft = Offset(2f, size.height -position),
            size = Size(size.width, position),
            cornerRadius = CornerRadius(60f)
        )
    }
}

@Composable
fun SunriseSunsetCard(sunrise: String, sunset: String, dawn: String, dusk: String) {
    val context= LocalContext.current
    Surface(
        modifier = Modifier
            .width(160.dp)
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        color = (Blue800),
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(all=8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                context.getString(R.string.sunrise) +" / "+ context.getString(R.string.sunset),
                fontWeight = FontWeight.Medium,
                color = Color.White,
                style = QuickSandTypography.subtitle2
            )
            //  Spacer(modifier = Modifier.height(4.dp))

            Row    (modifier = Modifier.padding(all=3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center)         {

                Text(context.getString(R.string.sunrise)+": ",
                    color =  Color.White, style = QuickSandTypography.h5)

            }

            Row (modifier = Modifier.padding(all=3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center) {

                Text(sunrise, fontWeight = FontWeight.Bold, color =  Color.White, style = QuickSandTypography.h3)
            }

            Row(modifier = Modifier.padding(all=3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center)  {
                Text(context.getString(R.string.sunset)+": ", color = Color.White,style = QuickSandTypography.h5)

            }

            Row(modifier = Modifier.padding(all=5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center)  {
                 Text(sunset, fontWeight = FontWeight.Bold, color = Color.White, style = QuickSandTypography.h3)

            }


        } //Column 1


    }//surface
}

@Composable
fun MoonriseMoonsetCard(
    moonrise:String,
    moonset:String,
    moonPhase:Double
){
    val localContext= LocalContext.current
    Surface(
        modifier = Modifier
            .width(160.dp)
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        color = (Blue800),
        elevation = 4.dp)

    {
        Column(
            modifier = Modifier.padding(3.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row (modifier = Modifier.padding(1.dp)){
                androidx.compose.material3.Text(
                    text = localContext.getString(R.string.moonrise),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = QuickSandTypography.h6,
                    modifier = Modifier.padding(all = 1.dp)
                )
            }
            Row (modifier = Modifier.padding(1.dp)){
                androidx.compose.material3.Text(
                    text = moonrise,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = QuickSandTypography.h3,
                    modifier = Modifier.padding(all = 1.dp)
                )
            }
            Row (modifier = Modifier.padding(1.dp)){
                androidx.compose.material3.Text(
                    text = localContext.getString(R.string.moonset),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = QuickSandTypography.h6,
                    modifier = Modifier.padding(all = 3.dp)
                )
            }
            Row(modifier = Modifier.padding(1.dp)) {
                androidx.compose.material3.Text(
                    text = moonset,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = QuickSandTypography.h3,
                    modifier = Modifier.padding(all = 1.dp)
                )

            }
            Row(modifier = Modifier.padding(1.dp)){
                androidx.compose.material3.Text(
                    text = WeatherUtils.calculateMoonPhase(localContext, moonPhase),
                    color = Color.White,
                    fontWeight = FontWeight.Light,
                    style = QuickSandTypography.h6,
                    modifier = Modifier.padding(all = 1.dp)
                )

            }

        }
    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WeatherScreen()
}
