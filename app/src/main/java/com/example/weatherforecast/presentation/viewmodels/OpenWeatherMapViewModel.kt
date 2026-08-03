package com.example.weatherforecast.presentation.viewmodels

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.data.remote.AirVisualResponse
import com.example.weatherforecast.data.repositories.SettingsRepository
import com.example.weatherforecast.domain.usecases.GetAirVisualDataUseCase
import com.example.weatherforecast.domain.usecases.GetDeviceCityUseCase
import com.example.weatherforecast.domain.usecases.GetWeatherUseCase
import com.example.weatherforecast.domain.models.DailyWeather
import com.example.weatherforecast.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class OpenWeatherMapViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    getDeviceCityUseCase: GetDeviceCityUseCase,
    getWeatherUseCase: GetWeatherUseCase,
    settingsRepository: SettingsRepository,
    private val getAirVisualDataUseCase: GetAirVisualDataUseCase,
    @Named("iqAirApiKey") private val iqAirApiKey: String
) : BaseWeatherViewModel(
    application, savedStateHandle, getDeviceCityUseCase, getWeatherUseCase, settingsRepository
) {

    // StateFlows (review item 5) — external reads are immutable StateFlow, all writes
    // go through the private MutableStateFlow so state cannot be mutated from the UI.
    private val _airVisualLiveData = MutableStateFlow<AirVisualResponse?>(null)
    val airVisualLiveData: StateFlow<AirVisualResponse?> = _airVisualLiveData.asStateFlow()

    private val _weatherLiveData = MutableStateFlow<Resource<DailyWeather>>(Resource.Loading())
    val weatherLiveData: StateFlow<Resource<DailyWeather>> = _weatherLiveData.asStateFlow()

    // PRESERVES SavedStateHandle restore: the dialog visibility survives process
    // death / config change via the initial value read from the SavedStateHandle.
    private val _showCitySelectionDialog = MutableStateFlow<Boolean>(
        savedStateHandle.get<Boolean>(KEY_SHOW_CITY_SELECTION_DIALOG) ?: false
    )
    val showCitySelectionDialog: StateFlow<Boolean> = _showCitySelectionDialog.asStateFlow()

    private var isWeatherLoaded = false
    override val stateLoaded: Boolean get() = isWeatherLoaded

    override fun onCityChanged(city: String?) {
        if (!city.isNullOrBlank()) {
            currentCity = city
            refreshWeather(city)
            setShowCitySelectionDialog(false)
        } else {
            if (hasLocationPermission()) {
                viewModelScope.launch {
                    fetchAndSaveDeviceCity()
                }
            } else {
                setShowCitySelectionDialog(true)
            }
        }
    }

    override fun onCityDetectionFailed() {
        setShowCitySelectionDialog(true)
    }

    /*
     * Get current weather.
     */
    private suspend fun fetchWeather(city: String, forceRefresh: Boolean = false): Resource<DailyWeather> {
        return getWeatherUseCase.getCurrentWeather(city, forceRefresh)
    }

    /**
     * Get air quality (AQI) for coordinates
     * that are already in WeatherResponse.
     */
    private suspend fun fetchAirVisualData(lat: Double, lon: Double) {
        try {
            val result = getAirVisualDataUseCase(
                lat,
                lon,
                iqAirApiKey
            )
            _airVisualLiveData.value = result
        } catch (e: Exception) {
            _airVisualLiveData.value = null
        }
    }

    /**
     * Unified method:
     * updates both weather and air pollution data.
     */
    fun refreshWeather(city: String = currentCity) {
        isWeatherLoaded = false
        viewModelScope.launch {
            _weatherLiveData.value = Resource.Loading()
            try {
                val result = fetchWeather(city, forceRefresh = true)
                _weatherLiveData.value = result
                if (result is Resource.Success) {
                    isWeatherLoaded = true
                    currentCity = city

                    // uses latitude / longitude from DailyWeather
                    val lat = result.data?.latitude
                    val lon = result.data?.longitude
                    if (lat != null && lon != null) {
                        fetchAirVisualData(lat, lon)
                    }
                }
            } catch (e: Exception) {
                _weatherLiveData.value = Resource.Error(null, "An error occurred: ${e.message}")
            }
        }
    }

    fun onCitySelected(cityName: String) {
        viewModelScope.launch {
            try {
                if (cityName.isNotBlank()) {
                    settingsRepository.updateCityName(cityName)
                }
            } catch (e: Exception) {
                _weatherLiveData.value = Resource.Error(null, "Failed to set city: ${e.message}")
            }
        }
    }

    fun dismissCitySelectionDialog() {
        setShowCitySelectionDialog(false)
    }

    private fun setShowCitySelectionDialog(value: Boolean) {
        _showCitySelectionDialog.value = value
        savedStateHandle[KEY_SHOW_CITY_SELECTION_DIALOG] = value
    }

    companion object {
        const val KEY_SHOW_CITY_SELECTION_DIALOG = "showCitySelectionDialog"
    }
}
