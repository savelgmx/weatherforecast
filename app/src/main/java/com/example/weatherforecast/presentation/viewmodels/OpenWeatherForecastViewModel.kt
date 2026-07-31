package com.example.weatherforecast.presentation.viewmodels

import android.app.Application
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.components.DataStoreManager
import com.example.weatherforecast.domain.usecases.GetDeviceCityUseCase
import com.example.weatherforecast.domain.usecases.GetWeatherUseCase
import com.example.weatherforecast.domain.models.DailyWeather
import com.example.weatherforecast.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
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
    getWeatherUseCase: GetWeatherUseCase
) : BaseWeatherViewModel(application, savedStateHandle, getDeviceCityUseCase, getWeatherUseCase) {

    val forecastLiveData: MutableState<Resource<List<DailyWeather>>> = mutableStateOf(Resource.Loading())
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
                forecastLiveData.value = Resource.Loading()
                try {
                    val result = getWeatherUseCase.getForecastWeather(city, forceRefresh)
                    forecastLiveData.value = result
                    if (result is Resource.Success) {
                        isForecastLoaded = true
                        currentCity = city
                    }
                } catch (e: Exception) {
                    forecastLiveData.value = Resource.Error(null, "Forecast error: ${e.message}")
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
            DataStoreManager.updateCityName(getApplication(), city)
            Log.d("ViewModel", "updateCityName completed")
            DataStoreManager.addRecentCity(getApplication(), city)
            Log.d("ViewModel", "addRecentCity completed")
        }
    }
}
