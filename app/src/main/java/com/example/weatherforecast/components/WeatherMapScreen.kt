package com.example.weatherforecast.components


import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.layers.RasterLayer
import org.maplibre.android.style.sources.RasterSource
import org.maplibre.android.style.sources.TileSet

private const val TAG = "WeatherMapScreen"
private const val WEATHER_LAYER_ID = "weather-layer"
private const val WEATHER_SOURCE_ID = "weather-source"

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
    var mapViewState by remember { mutableStateOf<MapView?>(null) }

    // ⚠ ДЕФЕКТ 2 (исправление): Сохраняем ссылки на MapLibreMap и Style,
    // полученные из factory. В update-блоке используем их напрямую, чтобы
    // НЕ вызывать `map.getStyle { }` повторно — этот callback может не
    // сработать, если стиль уже загружен через setStyle().
    var mapRef by remember { mutableStateOf<org.maplibre.android.maps.MapLibreMap?>(null) }
    var styleRef by remember { mutableStateOf<Style?>(null) }

    // ⚠ Флаг инициализации камеры+маркеров — предотвращает повторный
    // animateCamera при переключении слоёв (ломало зум) и SIGSEGV
    // от многократного вызова map.clear() в GLThread.
    var isMapInitialized by remember { mutableStateOf(false) }

    // ⚠ Счётчик для принудительного обновления слоя через LaunchedEffect.
    // Каждый вызов onLayerSelected инкрементит его, триггеря перезагрузку.
    var layerVersion by remember { mutableStateOf(0) }

    // Lifecycle-aware MapView management — fixes ML-CRIT-001 (previous review finding)
    DisposableEffect(Unit) {
        onDispose {
            mapViewState?.onDestroy()
            mapViewState = null
        }
    }

    // When city or selectedLayer changes, reload data
    LaunchedEffect(city, selectedLayer) {
        viewModel.loadWeatherData(city)
    }

    // Reset camera+markers flag on city change (не на selectedLayer!)
    LaunchedEffect(city) {
        isMapInitialized = false
    }

    // ⚠ Переключение погодного слоя: запускается при изменении selectedLayer
    // ИЛИ styleRef. Выполняется на Main (coroutine), НЕ в GLThread.
    LaunchedEffect(styleRef, selectedLayer, layerVersion) {
        val style = styleRef ?: return@LaunchedEffect
        updateWeatherTileLayer(style, selectedLayer, viewModel)
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
                    onClick = {
                        viewModel.onLayerSelected(layer)
                        layerVersion++
                    },
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

        // MapLibre MapView + legend overlay
        val context = LocalContext.current
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    MapView(ctx).apply {
                        mapViewState = this
                        getMapAsync { map ->
                            // ⚠ ДЕФЕКТ 2 (исправление): Сохраняем ссылки для update-блока,
                            // чтобы не вызывать map.getStyle() повторно.
                            mapRef = map
                            map.setStyle(Style.Builder().fromUri(styleUrl)) { style ->
                                Log.d(TAG, "MapLibre style loaded: $styleUrl")
                                styleRef = style
                                // ⚠ Слой добавляется через LaunchedEffect(styleRef, selectedLayer),
                                // а НЕ здесь — чтобы избежать race condition с GLThread.
                            }
                        }
                    }
                },
                // ⚠ ДЕФЕКТ 2 (исправление): Используем сохранённые mapRef/styleRef
                // из factory, без getMapAsync/getStyle — эти callback'и могут не
                // сработать повторно после первичной загрузки стиля через setStyle().
                // ⚠ update НЕ трогает стиль (addSource/addLayer) — все манипуляции
                // со стилем идут через LaunchedEffect(styleRef, selectedLayer) выше,
                // чтобы избежать SIGSEGV из GLThread.
                // Здесь только камера + маркеры.
                update = { _ ->
                    val map = mapRef ?: return@AndroidView
                    if (mapData != null && !isMapInitialized) {
                        isMapInitialized = true
                        initMapCameraAndMarkers(map, mapData!!)
                    }
                }
            )

            // Legend overlay — bottom-right corner
            WeatherLegend(
                layer = selectedLayer,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 8.dp, bottom = 8.dp)
            )
        }
    }
}

/**
 * Section 1: Always update weather tile raster layer on top of the base map.
 * Tile URL changes when selectedLayer changes — so this runs on every recomposition.
 */
private fun updateWeatherTileLayer(
    style: Style,
    selectedLayer: WeatherLayer,
    viewModel: WeatherMapViewModel
) {
    // Remove previous weather layer/source (idempotent — only if exists)
    if (style.getLayer(WEATHER_LAYER_ID) != null) {
        style.removeLayer(WEATHER_LAYER_ID)
    }
    if (style.getSource(WEATHER_SOURCE_ID) != null) {
        style.removeSource(WEATHER_SOURCE_ID)
    }

    // Build tile URL and add raster source + layer on TOP of the style stack
    val tileUrl = viewModel.getTileUrl(selectedLayer)
    Log.d(TAG, "Weather tile URL: $tileUrl")
    val source = RasterSource(WEATHER_SOURCE_ID, TileSet("tiles", tileUrl), 256)
    style.addSource(source)

    val rasterLayer = RasterLayer(WEATHER_LAYER_ID, WEATHER_SOURCE_ID)
    rasterLayer.setProperties(PropertyFactory.rasterOpacity(0.7f))

    // ⚠ ДЕФЕКТ 3: Раньше здесь была цепочка `addLayerBelow("waterway-label")`
    // → `addLayerBelow("road-label")` → `addLayer(rasterLayer)`.
    // MapTiler streets не содержит `"waterway-label"`, fallback на `"road-label"`
    // вставлял слой ПОД дорогами/зданиями/POI — оверлей был перекрыт.
    // Исправление: добавляем на САМЫЙ ВЕРХ стопки слоёв.
    style.addLayer(rasterLayer)
    Log.d(TAG, "Added weather layer '$WEATHER_LAYER_ID' for ${selectedLayer.displayName}")
}

/**
 * Section 2: Animate camera to city center and add markers.
 * Called ONLY once per city (tracked by isMapInitialized).
 * NO map.clear() — deprecated native call caused SIGSEGV in GLThread.
 */
private fun initMapCameraAndMarkers(
    map: org.maplibre.android.maps.MapLibreMap,
    mapData: WeatherMapData
) {
    val center = LatLng(mapData.centerLat ?: 0.0, mapData.centerLon ?: 0.0)
    val camera = CameraPosition.Builder()
        .target(center)
        .zoom(8.5)
        .build()

    map.animateCamera(CameraUpdateFactory.newCameraPosition(camera), 1200)

    // Add markers
    if (mapData.points.isNotEmpty()) {
        map.addMarker(
            MarkerOptions()
                .position(center)
                .title("Center: ${mapData.centerLat?.format(3)}, ${mapData.centerLon?.format(3)}")
        )
    }

    mapData.points.forEachIndexed { _, point ->
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
        "Camera animated + ${mapData.points.size} markers added, centered at (${mapData.centerLat}, ${mapData.centerLon})"
    )
}

/**
 * Utility: format double nicely for logs / titles.
 */
private fun Double.format(digits: Int) = "%.${digits}f".format(this)