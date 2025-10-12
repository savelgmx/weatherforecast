package com.example.weatherforecast.components

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.weatherforecast.presentation.viewmodels.WeatherMapViewModel
import com.example.weatherforecast.utils.WeatherLayer
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.maps.Style.OnStyleLoaded
import org.maplibre.android.style.layers.RasterLayer
import org.maplibre.android.style.sources.RasterSource
import org.maplibre.android.style.sources.TileSet

private const val TAG = "WeatherMapScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherMapScreen(
    city: String,
    viewModel: WeatherMapViewModel = hiltViewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Запуск загрузки данных при появлении экрана / смене слоя
    LaunchedEffect(city, uiState.selectedLayer) {
        viewModel.loadWeather(city, uiState.selectedLayer)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Weather Map: $city") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack, contentDescription = "Back")
                }
            })

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WeatherLayer.values().forEach { layer ->
                Button(
                    onClick = { viewModel.loadWeather(city, layer) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.selectedLayer == layer) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text(layer.name)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.fillMaxSize()) {
            // AndroidView + MapView
            var mapViewRef by remember { mutableStateOf<MapView?>(null) }

            AndroidView(
                factory = { ctx: Context ->
                    // Создаём MapView и forward lifecycle через observer
                    val mv = MapView(ctx)
                    mv.onCreate(null)
                    mapViewRef = mv
                    // Установим первоначальный стиль и слой как только будет готова карта —
                    // делаем это через getMapAsync ниже
                    mv
                },
                update = { mv ->
                    // ensure map instance exists and style/tile are applied
                    mv.getMapAsync { map ->
                        // Если в uiState нет styleUrl — ничего не делаем
                        val styleUrl = uiState.styleUrl
                        val tileTemplate = uiState.tileUrlTemplate

                        if (!styleUrl.isNullOrBlank()) {
                            map.setStyle(styleUrl) { style ->
                                // Add or update weather raster layer if tileTemplate available
                                tileTemplate?.let { template ->
                                    try {
                                        // remove previous if exists
                                        style.removeLayer("weather-layer")
                                    } catch (_: Exception) { /* ignore */ }

                                    try {
                                        style.removeSource("weather-source")
                                    } catch (_: Exception) { /* ignore */ }

                                    val tileSet = TileSet("2.1.0", template)
                                    val source = RasterSource("weather-source", tileSet, 256)
                                    style.addSource(source)
                                    val layer = RasterLayer("weather-layer", "weather-source")
                                    // opcional: layer.setProperties(...) to set opacity
                                    style.addLayerAbove(layer, "waterway-label")
                                }

                                // Move camera to center if available
                                val lat = uiState.centerLat ?: uiState.points.firstOrNull()?.lat ?: 0.0
                                val lon = uiState.centerLon ?: uiState.points.firstOrNull()?.lon ?: 0.0
                                val pos = LatLng(lat, lon)
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 6.0))
                            }
                        } else {
                            // no style specified yet — fallback: nothing
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Forward MapView lifecycle events to the MapView
            DisposableEffect(lifecycleOwner, mapViewRef) {
                val mapView = mapViewRef
                val lifecycle = lifecycleOwner.lifecycle
                val observer = LifecycleEventObserver { _, event ->
                    if (mapView == null) return@LifecycleEventObserver
                    when (event) {
                        Lifecycle.Event.ON_START -> mapView.onStart()
                        Lifecycle.Event.ON_RESUME -> mapView.onResume()
                        Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                        Lifecycle.Event.ON_STOP -> mapView.onStop()
                        Lifecycle.Event.ON_DESTROY -> {
                            mapView.onDestroy()
                        }
                        else -> {}
                    }
                }
                lifecycle.addObserver(observer)
                onDispose {
                    lifecycle.removeObserver(observer)
                    // Ensure final destroy
                    try {
                        mapViewRef?.onDestroy()
                    } catch (_: Exception) { }
                }
            }
        }
    }
}
