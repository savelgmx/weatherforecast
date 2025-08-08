package com.example.weatherforecast.presentation.viewmodels

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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

    /**
     * Observes the city from DataStore and fetches forecast.
     * If city is not set, tries to detect device location and save it.
     */
    private fun observeCityAndFetchForecast() {
        viewModelScope.launch {
            DataStoreManager.cityNamePrefFlow(getApplication())
                .collectLatest { city ->
                    if (!city.isNullOrBlank()) {
                        currentCity = city
                        getForecast(city, forceRefresh = true)
                    } else {
                        // Try to get device location if no city is saved
                        try {
                            val autoCity = getDeviceCityUseCase.execute()
                            if (autoCity.isNotBlank()) {
                                DataStoreManager.updateCityName(getApplication(), autoCity)
                                // No need to call getForecast(), as flow will emit again
                            }
                        } catch (e: Exception) {
                            // Silently ignore or log the error
                        }
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
}

