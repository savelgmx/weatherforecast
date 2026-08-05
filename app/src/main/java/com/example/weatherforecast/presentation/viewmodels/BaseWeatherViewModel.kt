package com.example.weatherforecast.presentation.viewmodels

import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.data.repositories.SettingsRepository
import com.example.weatherforecast.domain.usecases.GetDeviceCityUseCase
import com.example.weatherforecast.domain.usecases.GetWeatherUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

abstract class BaseWeatherViewModel(
    application: Application,
    protected val savedStateHandle: SavedStateHandle,
    protected val getDeviceCityUseCase: GetDeviceCityUseCase,
    protected val getWeatherUseCase: GetWeatherUseCase,
    protected val settingsRepository: SettingsRepository
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
        // CRASH FIX (fatal-exeption-fix): This method is invoked from the base-class
        // `init` block (line 31), which in Kotlin runs BEFORE the subclass's own
        // property initializers. The previous code used `viewModelScope.launch { ... }`,
        // which dispatches on Dispatchers.Main.immediate — that starts the collector
        // coroutine INLINE, right here inside the constructor. If the DataStore city
        // flow then emits its current value synchronously, kotlinx.coroutines delivers
        // it immediately and calls onCityChanged(city) before OpenWeatherForecastViewModel
        // has initialized its `_forecastLiveData` MutableStateFlow field.
        //
        // The result was:
        //   java.lang.NullPointerException: Attempt to invoke interface method
        //   'void kotlinx.coroutines.flow.MutableStateFlow.setValue(Object)' on a null
        //   object reference  (OpenWeatherForecastViewModel.getForecast, line 58)
        //
        // FIX: Launch the collector on Dispatchers.Main (the NON-immediate variant)
        // instead of the viewModelScope default. Dispatchers.Main posts the coroutine
        // onto the main Looper queue rather than executing it inline, so it can only
        // start running AFTER the constructor has fully returned — i.e. once every
        // subclass field (including `_forecastLiveData`) is guaranteed to be initialized.
        // The first city emission then triggers getForecast() safely on non-null state.
        viewModelScope.launch(Dispatchers.Main) {
            // City selection changes flow through the SettingsRepository rather than
            // the static DataStoreManager (review item 5).
            settingsRepository.cityName
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
                settingsRepository.updateCityName(autoCity)
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
