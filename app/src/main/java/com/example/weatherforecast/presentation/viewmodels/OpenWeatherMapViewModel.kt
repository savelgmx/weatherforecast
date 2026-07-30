package com.example.weatherforecast.presentation.viewmodels

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.components.DataStoreManager
import com.example.weatherforecast.data.remote.AirVisualResponse
import com.example.weatherforecast.domain.usecases.GetAirVisualDataUseCase
import com.example.weatherforecast.domain.usecases.GetDeviceCityUseCase
import com.example.weatherforecast.domain.usecases.GetWeatherUseCase
import com.example.weatherforecast.domain.models.DailyWeather
import com.example.weatherforecast.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class OpenWeatherMapViewModel @Inject constructor(
    application: Application,
    getDeviceCityUseCase: GetDeviceCityUseCase,
    getWeatherUseCase: GetWeatherUseCase,
    private val getAirVisualDataUseCase: GetAirVisualDataUseCase,
    @Named("iqAirApiKey") private val iqAirApiKey: String
) : BaseWeatherViewModel(application, getDeviceCityUseCase, getWeatherUseCase) {

    val airVisualLiveData: MutableState<AirVisualResponse?> = mutableStateOf(null)
    val weatherLiveData: MutableState<Resource<DailyWeather>> = mutableStateOf(Resource.Loading())
    val showCitySelectionDialog: MutableState<Boolean> = mutableStateOf(false)

    private var isWeatherLoaded = false
    override val stateLoaded: Boolean get() = isWeatherLoaded

    override fun onCityChanged(city: String?) {
        if (!city.isNullOrBlank()) {
            currentCity = city
            refreshWeather(city)
            showCitySelectionDialog.value = false
        } else {
            if (hasLocationPermission()) {
                viewModelScope.launch {
                    fetchAndSaveDeviceCity()
                }
            } else {
                showCitySelectionDialog.value = true
            }
        }
    }

    override fun onCityDetectionFailed() {
        showCitySelectionDialog.value = true
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
            airVisualLiveData.value = result
        } catch (e: Exception) {
            airVisualLiveData.value = null
        }
    }

    /**
     * Unified method:
     * updates both weather and air pollution data.
     */
    fun refreshWeather(city: String = currentCity) {
        isWeatherLoaded = false
        viewModelScope.launch {
            weatherLiveData.value = Resource.Loading()
            try {
                val result = fetchWeather(city, forceRefresh = true)
                weatherLiveData.value = result
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
                weatherLiveData.value = Resource.Error(null, "An error occurred: ${e.message}")
            }
        }
    }

    fun onCitySelected(cityName: String) {
        viewModelScope.launch {
            try {
                if (cityName.isNotBlank()) {
                    DataStoreManager.updateCityName(getApplication(), cityName)
                }
            } catch (e: Exception) {
                weatherLiveData.value = Resource.Error(null, "Failed to set city: ${e.message}")
            }
        }
    }

    fun dismissCitySelectionDialog() {
        showCitySelectionDialog.value = false
    }
}
