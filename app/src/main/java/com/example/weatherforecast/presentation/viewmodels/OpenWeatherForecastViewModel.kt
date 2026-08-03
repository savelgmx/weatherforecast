package com.example.weatherforecast.presentation.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.data.repositories.SettingsRepository
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

/**
 * ViewModel для управления прогнозом погоды.
 */
@HiltViewModel
class OpenWeatherForecastViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    getDeviceCityUseCase: GetDeviceCityUseCase,
    getWeatherUseCase: GetWeatherUseCase,
    settingsRepository: SettingsRepository
) : BaseWeatherViewModel(
    application, savedStateHandle, getDeviceCityUseCase, getWeatherUseCase, settingsRepository
) {

    // StateFlow (review item 5) — external reads immutable, writes via private MutableStateFlow.
    private val _forecastLiveData = MutableStateFlow<Resource<List<DailyWeather>>>(Resource.Loading())
    val forecastLiveData: StateFlow<Resource<List<DailyWeather>>> = _forecastLiveData.asStateFlow()
    private var isForecastLoaded = false
    override val stateLoaded: Boolean get() = isForecastLoaded

    override fun onCityChanged(city: String?) {
        if (!city.isNullOrBlank()) {
            currentCity = city
            getForecast(city, forceRefresh = true)
        } else {
            if (hasLocationPermission()) {
                viewModelScope.launch {
                    fetchAndSaveDeviceCity()
                }
            }
        }
    }

    /**
     * Fetches forecast weather for the given city.
     */
    private fun getForecast(city: String, forceRefresh: Boolean = false) {
        if(!isForecastLoaded||forceRefresh){
            viewModelScope.launch {
                _forecastLiveData.value = Resource.Loading()
                try {
                    val result = getWeatherUseCase.getForecastWeather(city, forceRefresh)
                    _forecastLiveData.value = result
                    if (result is Resource.Success) {
                        isForecastLoaded = true
                        currentCity = city
                    }
                } catch (e: Exception) {
                    _forecastLiveData.value = Resource.Error(null, "Forecast error: ${e.message}")
                }
            }
        }
    }

    /**
     * Public method to refresh forecast manually.
     */
    fun refreshWeather(city: String = currentCity) {
        isForecastLoaded = false
        getForecast(city, forceRefresh = true)
    }

    /**
     * Saves the selected city to recent history and triggers forecast fetch.
     * Call this from the Select City dialog's onCitySelected callback.
     */
    fun selectCity(city: String) {
        Log.d("ViewModel", "selectCity called: $city")
        viewModelScope.launch {
            Log.d("ViewModel", "selectCity coroutine started")
            // IMPORTANT: updateCityName MUST be called before addRecentCity.
            // addRecentCity writes RECENT_CITIES_KEY, which triggers a DataStore
            // emission; if the city hasn't been saved to LOCATED_CITY_NAME_KEY yet,
            // the cityName flow re-emits the old (blank) value. The ordering here is
            // preserved through the SettingsRepository (review item 5).
            settingsRepository.updateCityName(city)
            Log.d("ViewModel", "updateCityName completed")
            settingsRepository.addRecentCity(city)
            Log.d("ViewModel", "addRecentCity completed")
        }
    }
}
