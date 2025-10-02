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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.weatherforecast.presentation.viewmodels.WeatherMapViewModel
import com.example.weatherforecast.utils.WeatherLayer
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.maps.Style.OnStyleLoaded
import org.maplibre.android.style.layers.RasterLayer
import org.maplibre.android.style.sources.RasterSource
import org.maplibre.android.style.sources.TileSet

private const val TAG = "WeatherMapScreenDebug"
private const val SOURCE_ID = "weather-source"
private const val LAYER_ID = "weather-layer"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherMapScreen(
    city: String,
    viewModel: WeatherMapViewModel = hiltViewModel(),
    navController: NavController
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val mapState = remember { mutableStateOf<MapLibreMap?>(null) }

    // Trigger loading when city or layer changes
    LaunchedEffect(city, state.selectedLayer) {
        viewModel.loadWeather(city, state.selectedLayer)
    }

    // Debug logs
    LaunchedEffect(state) {
        Log.d(TAG, "City: $city, Layer: ${state.selectedLayer}, TileUrl: ${state.tileUrl}")
    }

    // Update layers when tileUrl changes
    LaunchedEffect(state.tileUrl) {
        mapState.value?.getStyle(object : OnStyleLoaded {
            override fun onStyleLoaded(style: Style) {
                style.removeLayer(LAYER_ID)
                style.removeSource(SOURCE_ID)
                state.tileUrl?.let { url ->
                    val tileSet = TileSet("2.1.0", url)
                    val source = RasterSource(SOURCE_ID, tileSet, 256)
                    style.addSource(source)
                    style.addLayer(RasterLayer(LAYER_ID, SOURCE_ID))
                }
            }
        })
    }
    // Update camera when center changes
    LaunchedEffect(state.centerLat, state.centerLon) {
        mapState.value?.let { map ->
            val lat = state.centerLat ?: 40.7128
            val lon = state.centerLon ?: -74.0060
            map.setCameraPosition(
                CameraPosition.Builder()
                    .target(LatLng(lat, lon))
                    .zoom(8.0)
                    .build()
            )
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

        AndroidView(
            factory = { ctx ->
                MapLibre.getInstance(ctx)
                val mapView = MapView(ctx)
                mapView.getMapAsync { mapLibreMap ->
                    mapState.value = mapLibreMap
                    mapLibreMap.setStyle("https://demotiles.maplibre.org/style.json") { style ->
                        // Initial layer setup
                        state.tileUrl?.let { url ->
                            val tileSet = TileSet("2.1.0", listOf(url).toString())
                            val source = RasterSource(SOURCE_ID, tileSet, 256)
                            style.addSource(source)
                            style.addLayer(RasterLayer(LAYER_ID, SOURCE_ID))
                        }
                        // Initial camera
                        val lat = state.centerLat ?: 40.7128
                        val lon = state.centerLon ?: -74.0060
                        mapLibreMap.setCameraPosition(
                            CameraPosition.Builder()
                                .target(LatLng(lat, lon))
                                .zoom(8.0)
                                .build()
                        )
                    }
                }
                mapView
            },
            modifier = Modifier.fillMaxSize()
            // No update block needed; use LaunchedEffects
        )
    }
}