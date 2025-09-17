package com.example.weatherforecast.presentation.viewmodels

// =============================
// presentation/weathermap/WeatherMapViewModel.kt
// =============================


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class WeatherMapViewModel @Inject constructor(
    private val getWeatherMapDataUseCase: GetWeatherMapDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherMapUiState())
    val uiState: StateFlow<WeatherMapUiState> = _uiState.asStateFlow()

    fun loadWeather(city: String, layer: WeatherLayer) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, selectedLayer = layer, error = null)
            try {
                val data = getWeatherMapDataUseCase(city, layer)
                _uiState.value = _uiState.value.copy(
                    points = data.points,
                    centerLat = data.centerLat,
                    centerLon = data.centerLon,
                    isLoading = false
                )
            } catch (t: Throwable) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = t.message ?: "Unknown error")
            }
        }
    }
}

