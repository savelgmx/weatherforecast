package com.example.weatherforecast.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
            WeatherCard(title = "Скорость ветра", value = "11 км/ч", description = "Легкий · Юго-восточ...", extraInfo = null)
            WeatherCard(title = "Влажность", value = "35%", description = "Точка росы 12°", extraInfo = "100")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            WeatherCard(title = "УФ-индекс", value = "5", description = "Умеренный", extraInfo = "выше 11")
            WeatherCard(title = "Давление", value = "1 010", description = "мбар", extraInfo = null)
        }

        Spacer(modifier = Modifier.height(16.dp))

        SunriseSunsetCard(sunrise = "04:04", sunset = "21:39", dawn = "03:02", dusk = "22:41")
    }
}

@Composable
fun WeatherCard(title: String, value: String, description: String, extraInfo: String?) {
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
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colors.primary)
            Text(description, fontSize = 14.sp)
            if (extraInfo != null) {
                Text(extraInfo, fontSize = 12.sp, color = Color.Gray)
            }
        }
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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WeatherScreen()
}
