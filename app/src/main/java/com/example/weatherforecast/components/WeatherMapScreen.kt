package com.example.weatherforecast.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.weatherforecast.presentation.viewmodels.WeatherMapViewModel
import com.example.weatherforecast.utils.WeatherLayer
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState

private const val TAG = "WeatherMapScreenDebug"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherMapScreen(
    city: String,
    viewModel: WeatherMapViewModel = hiltViewModel(),
    navController: NavController
) {
    val state by viewModel.uiState.collectAsState()

    // Trigger loading when city or layer changes
    LaunchedEffect(city, state.selectedLayer) {
        viewModel.loadWeather(city, state.selectedLayer)
    }

    // Debug logs
    LaunchedEffect(state) {
        Log.d(TAG, "City: $city")
        Log.d(TAG, "Selected layer: ${state.selectedLayer}")
        Log.d(TAG, "CenterLat: ${state.centerLat}, CenterLon: ${state.centerLon}")
        Log.d(TAG, "Points size: ${state.points.size}")
        state.points.firstOrNull()?.let { p ->
            Log.d(TAG, "First point coords: lat=${p.lat}, lon=${p.lon}")
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
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

        Spacer(modifier = Modifier.height(8.dp))

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

        Spacer(modifier = Modifier.height(8.dp))

        // Camera initial position
        val cameraPositionState = rememberCameraPositionState {
            val lat = state.centerLat ?: 0.0
            val lon = state.centerLon ?: 0.0
            val hasCenter = (lat != 0.0 || lon != 0.0)
            position = if (hasCenter) {
                CameraPosition.fromLatLngZoom(LatLng(lat, lon), 8f)
            } else {
                // fallback to first point if exists
                state.points.firstOrNull()?.let { p ->
                    CameraPosition.fromLatLngZoom(LatLng(p.lat, p.lon), 8f)
                } ?: CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 2f)
            }
        }

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            state.points.forEach { point ->
                val colorInt = when (state.selectedLayer) {
                    WeatherLayer.TEMPERATURE -> tempColor(point.temperature)
                    WeatherLayer.CLOUDS -> cloudColor(point.cloudCover)
                    WeatherLayer.PRECIPITATION -> precipColor(point.precipitation)
                }
                val fill = intToComposeColor(colorInt)

                Circle(
                    center = LatLng(point.lat, point.lon),
                    radius = 4000.0,
                    fillColor = fill,
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

// helper to convert ARGB int to Compose Color
fun intToComposeColor(argb: Int): Color {
    val a = ((argb shr 24) and 0xFF) / 255f
    val r = ((argb shr 16) and 0xFF) / 255f
    val g = ((argb shr 8) and 0xFF) / 255f
    val b = (argb and 0xFF) / 255f
    return Color(red = r, green = g, blue = b, alpha = a)
}

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
