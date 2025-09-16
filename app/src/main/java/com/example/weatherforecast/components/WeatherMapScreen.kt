package com.example.weatherforecast.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.weatherforecast.presentation.viewmodels.WeatherMapViewModel
import com.example.weatherforecast.utils.WeatherLayer
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherMapScreen(
    city: String,
    viewModel: WeatherMapViewModel = hiltViewModel(),
    navController: NavController
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(city, state.selectedLayer) {
        viewModel.loadWeather(city, state.selectedLayer)
    }

    Column {
        TopAppBar(
            title = { Text("Weather Map: $city") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WeatherLayer.values().forEach { layer ->
                Button(
                    onClick = { viewModel.loadWeather(city, layer) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.selectedLayer == layer) Color.Gray else Color.LightGray
                    )
                ) {
                    Text(layer.name)
                }
            }
        }

        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(LatLng(52.0, 5.0), 6f)
        }

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            state.points.forEach { point ->
                val color = when (state.selectedLayer) {
                    WeatherLayer.TEMPERATURE -> tempColor(point.temperature)
                    WeatherLayer.CLOUDS -> cloudColor(point.cloudCover)
                    WeatherLayer.PRECIPITATION -> precipColor(point.precipitation)
                }
                Circle(
                    center = LatLng(point.lat, point.lon),
                    radius = 5000.0,
                    fillColor = Color(0x0000ff),//perfect blue
                    strokeColor = Color.Black,
                    strokeWidth = 1f
                )
            }
        }
    }
}
/*
White = 0xffffff
Black = 0x000000
A "perfect" Blue = 0x0000ff
A "prefect" Red = 0xff0000
A "middle" Gray = 0x7a7a7a
Aqua = 0x00ffff
Gold = 0xffd700
Indigo = 0x4b0082
 */


// Color helpers
fun tempColor(temp: Double?): Int = when {
    temp == null -> 0x55000000
    temp < 0 -> 0x5500FFFF
    temp < 15 -> 0x550000FF
    temp < 25 -> 0x5500FF00
    else -> 0x55FF0000
}

fun cloudColor(cloud: Double?): Int = when {
    cloud == null -> 0x55000000
    cloud < 30 -> 0x55FFFF00
    cloud < 70 -> 0x55AAAAAA
    else -> 0xFF555555.toInt()
}

fun precipColor(precip: Double?): Int = when {
    precip == null -> 0x55000000
    precip == 0.0 -> 0x55FFFF00
    precip < 5 -> 0x550000FF
    else -> 0x55FF00FF
}
