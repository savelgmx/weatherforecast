package com.example.weatherforecast.presentation.viewmodels

// =============================
// presentation/weathermap/WeatherMapViewModel.kt
// =============================


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.BuildConfig
import com.example.weatherforecast.domain.models.WeatherPoint
import com.example.weatherforecast.domain.usecases.GetWeatherMapDataUseCase
import com.example.weatherforecast.utils.WeatherLayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherMapViewModel @Inject constructor(
    private val getWeatherMapDataUseCase: GetWeatherMapDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherMapUiState())
    val uiState: StateFlow<WeatherMapUiState> = _uiState.asStateFlow()

    fun loadWeather(city: String, layer: WeatherLayer) {
        viewModelScope.launch {
            try {
                val mapData = getWeatherMapDataUseCase(city, layer)
                val tileUrl =
                    "https://api.maptiler.com/tiles/${layer.tilePath}/256/{z}/{x}/{y}.png?key=${BuildConfig.MAPTILER_API_KEY}"

                _uiState.value = WeatherMapUiState(
                    selectedLayer = layer,
                    tileUrl = tileUrl,
                    centerLat = mapData.centerLat ?: 0.0,
                    centerLon = mapData.centerLon ?: 0.0,
                    points = mapData.points
                )
            } catch (e: Exception) {
                Log.e("WeatherMapViewModel", "Error loading weather map: ${e.message}")
            }
        }
    }
}

data class WeatherMapUiState(
    val selectedLayer: WeatherLayer = WeatherLayer.Temperature,
    val tileUrl: String? = null,
    val centerLat: Double = 0.0,
    val centerLon: Double = 0.0,
    val points: List<WeatherPoint> = emptyList()
)
