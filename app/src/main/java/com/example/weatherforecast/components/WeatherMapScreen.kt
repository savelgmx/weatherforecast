package com.example.weatherforecast.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.weatherforecast.domain.models.WeatherMapData
import com.example.weatherforecast.presentation.viewmodels.WeatherMapViewModel
import com.example.weatherforecast.utils.WeatherLayer
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView

@Composable
fun WeatherMapScreen(
    city: String,
    viewModel: WeatherMapViewModel = hiltViewModel(),
    navController: NavController
) {
    val mapData by viewModel.mapData.collectAsState()
    val selectedLayer by viewModel.selectedLayer.collectAsState()

    // When city or selectedLayer changes, reload data
    LaunchedEffect(city, selectedLayer) {
        viewModel.loadWeatherData(city)
    }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("WeatherMap: $city")},
            navigationIcon = {
                IconButton(onClick = {navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack, contentDescription ="Back"
                    )
                }
            }
        )
        // Layer selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WeatherLayer.values().forEach { layer ->
                Button(
                    onClick = { viewModel.onLayerSelected(layer) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (layer == selectedLayer)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(layer.name)
                }
            }
        }

        // MapLibre MapView
        val context = LocalContext.current
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                MapView(ctx).apply {
                    getMapAsync { map ->
                        map.setStyle("https://demotiles.maplibre.org/style.json")  // ✅ correct style constant
                    }
                }
            },
            update = { mapView ->
                mapView.getMapAsync { map ->
                    updateMapLibreContent(map, mapData)
                }
            }
        )
    }
}

/**
 * Updates the MapLibre map camera & markers safely
 */
private fun updateMapLibreContent(map: org.maplibre.android.maps.MapLibreMap, mapData: WeatherMapData?) {
    val center = if (mapData?.centerLat != null && mapData.centerLon != null) {
        LatLng(mapData.centerLat, mapData.centerLon)
    } else {
        LatLng(0.0, 0.0)
    }

    val camera = CameraPosition.Builder()
        .target(center)
        .zoom(6.0)
        .build()

    map.cameraPosition = camera

    // Clear old markers first
    map.clear()

    // Add weather point markers
    mapData?.points?.forEach { point ->
        val marker = MarkerOptions()
            .position(LatLng(point.lat, point.lon))
            .title((point.temperature ?: "Point").toString()) // use .name not .label
        map.addMarker(marker)
    }
}
