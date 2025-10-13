package com.example.weatherforecast.presentation.viewmodels

// =============================
// presentation/weathermap/WeatherMapViewModel.kt
// =============================


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.BuildConfig
import com.example.weatherforecast.domain.models.WeatherMapData
import com.example.weatherforecast.domain.models.WeatherPoint
import com.example.weatherforecast.domain.usecases.GetWeatherMapDataUseCase
import com.example.weatherforecast.utils.WeatherLayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WeatherMapUiState(
    val city: String = "",
    val points: List<WeatherPoint> = emptyList(),
    val selectedLayer: WeatherLayer = WeatherLayer.CLOUDS,
    val centerLat: Double? = null,
    val centerLon: Double? = null,
    val styleUrl: String? = null,
    val tileUrlTemplate: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class WeatherMapViewModel @Inject constructor(
    private val getWeatherMapDataUseCase: GetWeatherMapDataUseCase
) : ViewModel() {

    private val _mapData = MutableStateFlow<WeatherMapData?>(null)
    val mapData: StateFlow<WeatherMapData?> = _mapData.asStateFlow()

    private val _selectedLayer = MutableStateFlow(WeatherLayer.Precipitation)
    val selectedLayer: StateFlow<WeatherLayer> = _selectedLayer.asStateFlow()

    fun onLayerSelected(layer: WeatherLayer) {
        _selectedLayer.value = layer
    }

    fun loadWeatherData(city: String) {
        viewModelScope.launch {
            try {
                val result = getWeatherMapDataUseCase(city, _selectedLayer.value)
                _mapData.value = result
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

