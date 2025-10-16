package com.example.weatherforecast.components


import android.util.Log
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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.navigation.NavController
import com.example.weatherforecast.domain.models.WeatherMapData
import com.example.weatherforecast.presentation.viewmodels.WeatherMapViewModel
import com.example.weatherforecast.utils.WeatherLayer
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style

private const val TAG = "WeatherMapScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherMapScreen(
    city: String,
    viewModel: WeatherMapViewModel,
    navController: NavController
) {
    val mapData by viewModel.mapData.collectAsState()
    val selectedLayer by viewModel.selectedLayer.collectAsState()
    val styleUrl by viewModel.styleUrl.collectAsState()

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
                    Text(layer.displayName)
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
                        map.setStyle(Style.Builder().fromUri(styleUrl)) {
                            Log.d(TAG, "MapLibre style loaded: $styleUrl")
                            // When style first loads, center map if data already available
                            updateMapLibreContent(map, mapData)
                        }
                    }
                }
            },
            update = { mapView ->
                if (mapData != null) {
                    mapView.getMapAsync { map ->
                        map.getStyle { _ ->
                            updateMapLibreContent(map, mapData)
                        }
                    }
                }
            }
        )
    }
}

/**
 * Updates the MapLibre map camera & markers safely
 */
private fun updateMapLibreContent(map: org.maplibre.android.maps.MapLibreMap, mapData: WeatherMapData?) {
    if (mapData == null) return

    map.getStyle { _ ->
        // Remove previous markers
        map.clear()

        // Move camera to city center
        val center = LatLng(mapData.centerLat ?: 0.0, mapData.centerLon ?: 0.0)
        val camera = CameraPosition.Builder()
            .target(center)
            .zoom(8.5)
            .build()

        map.animateCamera(CameraUpdateFactory.newCameraPosition(camera), 1200)

        // Add markers — if many, they’ll overlap; center marker remains visible
        if (mapData.points.isNotEmpty()) {
            map.addMarker(
                MarkerOptions()
                    .position(center)
                    .title("Center: ${mapData.centerLat?.format(3)}, ${mapData.centerLon?.format(3)}")
            )
        }

        mapData.points.forEachIndexed { idx, point ->
            map.addMarker(
                MarkerOptions()
                    .position(LatLng(point.lat, point.lon))
                    .title(
                        "T: ${point.temperature ?: "?"}°C " +
                                "P: ${point.precipitation ?: 0.0}mm"
                    )
            )
        }

        Log.d(
            TAG,
            "Added ${mapData.points.size} markers, centered at (${mapData.centerLat}, ${mapData.centerLon})"
        )
    }
}

/**
 * Utility: format double nicely for logs / titles.
 */
private fun Double.format(digits: Int) = "%.${digits}f".format(this)
