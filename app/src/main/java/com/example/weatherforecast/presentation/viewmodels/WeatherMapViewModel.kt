package com.example.weatherforecast.presentation.viewmodels

// =============================
// presentation/weathermap/WeatherMapViewModel.kt
// =============================


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.data.repositories.WeatherMapRepository
import com.example.weatherforecast.domain.models.WeatherMapData
import com.example.weatherforecast.domain.models.WeatherPoint
import com.example.weatherforecast.domain.usecases.GetWeatherMapDataUseCase
import com.example.weatherforecast.utils.WeatherLayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    private val getWeatherMapDataUseCase: GetWeatherMapDataUseCase,
    private val repository: WeatherMapRepository
) : ViewModel() {

    private val _mapData = MutableStateFlow<WeatherMapData?>(null)
    val mapData: StateFlow<WeatherMapData?> = _mapData

    private val _selectedLayer = MutableStateFlow(WeatherLayer.Temperature)
    val selectedLayer: StateFlow<WeatherLayer> = _selectedLayer

    private val _styleUrl = MutableStateFlow(repository.getMapStyleUrl())
    val styleUrl: StateFlow<String> = _styleUrl

    fun onLayerSelected(layer: WeatherLayer) {
        _selectedLayer.value = layer
    }

    fun loadWeatherData(city: String) {
        viewModelScope.launch {
            try {
                Log.d("WeatherMapVM", "Loading $city layer=${_selectedLayer.value.tilePath}")
                val data = getWeatherMapDataUseCase(city, _selectedLayer.value)
                Log.d("WeatherMapVM", "Received center=${data.centerLat},${data.centerLon}, points=${data.points.size}")
                _mapData.value = data
            } catch (e: Exception) {
                Log.e("WeatherMapVM", "Error loading data: ${e.message}", e)
            }
        }
    }

    fun getTileUrl(layer: WeatherLayer): String = repository.getWeatherTileUrl(layer)
}

