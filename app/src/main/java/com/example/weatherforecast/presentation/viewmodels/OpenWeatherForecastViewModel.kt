package com.example.weatherforecast.presentation.viewmodels

import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.components.DataStoreManager
import com.example.weatherforecast.domain.usecases.GetDeviceCityUseCase
import com.example.weatherforecast.domain.usecases.GetWeatherUseCase
import com.example.weatherforecast.response.ForecastResponse
import com.example.weatherforecast.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для управления прогнозом погоды.
 */
@HiltViewModel
class OpenWeatherForecastViewModel @Inject constructor(
    application: Application,
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getDeviceCityUseCase: GetDeviceCityUseCase // ✅ Added fallback use case
) : AndroidViewModel(application) {

    val forecastLiveData: MutableState<Resource<ForecastResponse>> = mutableStateOf(Resource.Loading())
    private var currentCity: String = ""
    private var isForecastLoaded = false

    init {
        observeCityAndFetchForecast()
    }

    private fun hasLocationPermission(): Boolean {
        val ctx = getApplication<Application>()
        return ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun observeCityAndFetchForecast() {
        viewModelScope.launch {
            DataStoreManager.cityNamePrefFlow(getApplication())
                .collectLatest { city ->
                    if (!city.isNullOrBlank()) {
                        currentCity = city
                        getForecast(city, forceRefresh = true)
                    } else {
                        if (hasLocationPermission()) {
                            fetchAndSaveDeviceCity()
                        }
                    }
                }
        }
    }

    /**
     * Auto-detects device location via GPS and saves the city name to DataStore.
     *
     * CRITICAL DESIGN DECISION: This is a suspend function (not fire-and-forget).
     *
     * It is called inside collectLatest {}. collectLatest cancels its lambda's
     * coroutine whenever a new value is emitted before the previous one finishes.
     * Making this a suspend function ensures the cancellation propagates into the
     * GPS resolution:
     *
     *   BEFORE (broken):  viewModelScope.launch { fetchAndSaveDeviceCity() }
     *   → launched a coroutine DISCONNECTED from collectLatest's scope.
     *   → User selects a city → collectLatest cancels the lambda, but the
     *     launched coroutine keeps running and overwrites the city choice.
     *
     *   AFTER (fixed):    suspend fun fetchAndSaveDeviceCity()
     *   → Called directly in the collectLatest lambda.
     *   → User selects a city → collectLatest cancels the lambda → coroutine
     *     is cancelled immediately → GPS resolution stops.
     *
     * See also: retryDeviceLocation() for the explicit manual retry path,
     * which correctly wraps this suspend function in viewModelScope.launch.
     */
    private suspend fun fetchAndSaveDeviceCity() {
        try {
            val autoCity = getDeviceCityUseCase.execute()
            if (autoCity.isNotBlank()) {
                DataStoreManager.updateCityName(getApplication(), autoCity)
                // No need to call getForecast(), as flow will emit again
            }
        } catch (_: Exception) { }
    }

    /**
     * Public retry button for auto-detect (called from the UI when the
     * user taps "Retry" after GPS failed or was denied).
     *
     * Wraps fetchAndSaveDeviceCity() in viewModelScope.launch because this
     * is called from a non-coroutine context (UI event handler), NOT from
     * inside collectLatest.
     */
    fun retryDeviceLocation() {
        if (currentCity.isBlank()) {
            viewModelScope.launch {
                fetchAndSaveDeviceCity()
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
                        currentCity=city
                    }
                } catch (e: Exception) {
                    forecastLiveData.value = Resource.Error(null, "Forecast error: ${e.message}")
                }
            }
        }
    }
    /**
     * Refreshes forecast manually (e.g., swipe-to-refresh).
     */

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
            // IMPORTANT: updateCityName must be called FIRST so cityNamePrefFlow
            // emits the correct city name. addRecentCity writes a different key
            // (RECENT_CITIES_KEY) to DataStore, which would trigger cityNamePrefFlow
            // to re-emit the OLD value (null/blank) before the city is saved,
            // causing the city-selection dialog to reappear or conflicting
            // fetchAndSaveDeviceCity() calls (see distinctUntilChanged guard).
            DataStoreManager.updateCityName(getApplication(), city)
            Log.d("ViewModel", "updateCityName completed")
            DataStoreManager.addRecentCity(getApplication(), city)
            Log.d("ViewModel", "addRecentCity completed")
        }
    }
}

