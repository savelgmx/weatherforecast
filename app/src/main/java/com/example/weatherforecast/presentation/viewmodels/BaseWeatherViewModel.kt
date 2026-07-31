package com.example.weatherforecast.presentation.viewmodels

import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.components.DataStoreManager
import com.example.weatherforecast.domain.usecases.GetDeviceCityUseCase
import com.example.weatherforecast.domain.usecases.GetWeatherUseCase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

abstract class BaseWeatherViewModel(
    application: Application,
    protected val savedStateHandle: SavedStateHandle,
    protected val getDeviceCityUseCase: GetDeviceCityUseCase,
    protected val getWeatherUseCase: GetWeatherUseCase
) : AndroidViewModel(application) {

    var currentCity: String = savedStateHandle.get<String>(KEY_CURRENT_CITY) ?: ""
        protected set(value) {
            field = value
            savedStateHandle[KEY_CURRENT_CITY] = value
        }
    protected abstract val stateLoaded: Boolean

    init {
        observeCityFromDataStore()
    }

    protected fun hasLocationPermission(): Boolean {
        val ctx = getApplication<Application>()
        return ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    protected fun observeCityFromDataStore() {
        viewModelScope.launch {
            DataStoreManager.cityNamePrefFlow(getApplication())
                .collectLatest { city ->
                    onCityChanged(city)
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
    protected suspend fun fetchAndSaveDeviceCity() {
        try {
            val autoCity = getDeviceCityUseCase.execute()
            if (autoCity.isNotBlank()) {
                DataStoreManager.updateCityName(getApplication(), autoCity)
            } else {
                onCityDetectionFailed()
            }
        } catch (e: Exception) {
            onCityDetectionFailed()
        }
    }

    /**
     * Public retry button for auto-detect (called from the UI when the
     * user taps "Retry" after GPS failed or was denied).
     */
    fun retryDeviceLocation() {
        if (currentCity.isBlank()) {
            viewModelScope.launch {
                fetchAndSaveDeviceCity()
            }
        }
    }

    /**
     * Called when DataStore emits a city name (or null).
     * Each subclass decides how to react.
     */
    protected abstract fun onCityChanged(city: String?)

    /**
     * Called when auto city detection fails.
     * Subclasses can show dialogs or use defaults.
     */
    protected open fun onCityDetectionFailed() {
        // no-op by default
    }

    companion object {
        const val KEY_CURRENT_CITY = "currentCity"
    }
}
