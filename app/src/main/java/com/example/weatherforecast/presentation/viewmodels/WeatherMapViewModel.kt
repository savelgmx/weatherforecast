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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherMapViewModel @Inject constructor(
    private val getWeatherMapDataUseCase: GetWeatherMapDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherMapUiState())
    val uiState: StateFlow<WeatherMapUiState> = _uiState

    fun loadWeather(city: String, layer: WeatherLayer) {
        viewModelScope.launch {
            try {
                val points = getWeatherMapDataUseCase(city, layer)
                _uiState.value = _uiState.value.copy(
                    city = city,
                    selectedLayer = layer,
                    points = points,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}

data class WeatherMapUiState(
    val city: String = "",
    val selectedLayer: WeatherLayer = WeatherLayer.TEMPERATURE,
    val points: List<WeatherPoint> = emptyList(),
    val error: String? = null
)
