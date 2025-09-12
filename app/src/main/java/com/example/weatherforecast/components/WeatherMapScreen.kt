package com.example.weatherforecast.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.weatherforecast.theme.Blue800
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherMapScreen(
    latitude: Double,
    longitude: Double,
    apiKey: String
) {
    val cityLocation = LatLng(latitude, longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cityLocation, 7f)
    }

    var selectedLayer by remember { mutableStateOf(WeatherMapType.TEMPERATURE) }
    var weatherPoint by remember { mutableStateOf<WeatherPoint?>(null) }

    // Асинхронная загрузка данных
    LaunchedEffect(latitude, longitude) {
        weatherPoint = fetchVisualCrossingWeather(latitude, longitude, apiKey)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            // Рисуем один Circle на координатах города
            weatherPoint?.let { point ->
                val color = when (selectedLayer) {
                    WeatherMapType.TEMPERATURE -> point.tempColor()
                    WeatherMapType.CLOUDS -> point.cloudColor()
                    WeatherMapType.PRECIPITATION -> point.precipColor()
                    else -> {}
                }
                Circle(
                    center = LatLng(point.lat, point.lon),
                    radius = 20000.0, // 20 км
                    fillColor = Blue800,
                    strokeColor = Color.Transparent
                )
            }
        }

        // Переключатель слоёв
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedLayer == WeatherMapType.CLOUDS,
                onClick = { selectedLayer = WeatherMapType.CLOUDS },
                label = { Text("Облака") }
            )
            FilterChip(
                selected = selectedLayer == WeatherMapType.PRECIPITATION,
                onClick = { selectedLayer = WeatherMapType.PRECIPITATION },
                label = { Text("Осадки") }
            )
            FilterChip(
                selected = selectedLayer == WeatherMapType.TEMPERATURE,
                onClick = { selectedLayer = WeatherMapType.TEMPERATURE },
                label = { Text("Температура") }
            )
        }
    }
}

enum class WeatherMapType {
    CLOUDS, PRECIPITATION, TEMPERATURE
}

data class WeatherPoint(
    val lat: Double,
    val lon: Double,
    val temp: Double,
    val clouds: Double,
    val precip: Double
)

// Цвета для слоёв
fun WeatherPoint.tempColor() = when {
    temp < 0 -> Color(0x550099FF)
    temp < 10 -> Color(0x5500FFFF)
    temp < 20 -> Color(0x55FFFF00)
    else -> Color(0x55FF3300)
}

fun WeatherPoint.cloudColor() = when {
    clouds < 20 -> Color(0x5500FF00)
    clouds < 50 -> Color(0x55FFFF00)
    else -> Color(0x55FF0000)
}

fun WeatherPoint.precipColor() = when {
    precip < 1 -> Color(0x5500FF00)
    precip < 5 -> Color(0x55FFFF00)
    else -> Color(0x55FF0000)
}

// Асинхронная загрузка с Visual Crossing
suspend fun fetchVisualCrossingWeather(
    lat: Double,
    lon: Double,
    apiKey: String
): WeatherPoint? = withContext(Dispatchers.IO) {
    try {
        val url = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/$lat,$lon?unitGroup=metric&key=$apiKey&include=days"
        val response = URL(url).readText()
        val json = JSONObject(response)
        val day = json.getJSONArray("days").getJSONObject(0)
        val temp = day.getDouble("temp")
        val clouds = day.optDouble("cloudcover", 0.0)
        val precip = day.optDouble("precip", 0.0)
        WeatherPoint(lat, lon, temp, clouds, precip)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
