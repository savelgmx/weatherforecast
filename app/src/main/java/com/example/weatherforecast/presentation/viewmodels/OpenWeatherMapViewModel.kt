package com.example.weatherforecast.presentation.viewmodels

import android.app.Application
import android.content.pm.PackageManager
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.components.DataStoreManager
import com.example.weatherforecast.data.remote.AirVisualResponse
import com.example.weatherforecast.domain.usecases.GetAirVisualDataUseCase
import com.example.weatherforecast.domain.usecases.GetCoordinatesUseCase
import com.example.weatherforecast.domain.usecases.GetDeviceCityUseCase
import com.example.weatherforecast.domain.usecases.GetWeatherUseCase
import com.example.weatherforecast.response.WeatherResponse
import com.example.weatherforecast.utils.Resource
import com.example.weatherforecast.utils.WeatherUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OpenWeatherMapViewModel @Inject constructor(
    application: Application,
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getDeviceCityUseCase: GetDeviceCityUseCase,
    private val getCoordinatesUseCase: GetCoordinatesUseCase,
    private val getAirVisualDataUseCase: GetAirVisualDataUseCase
) : AndroidViewModel(application) {

    val airVisualLiveData: MutableState<AirVisualResponse?> = mutableStateOf(null)
    val weatherLiveData: MutableState<Resource<WeatherResponse>> = mutableStateOf(Resource.Loading())
    val showCitySelectionDialog: MutableState<Boolean> = mutableStateOf(false)
    private var isWeatherLoaded = false // Flag to track if weather data is already loaded
    private var currentCity: String = ""

    init {
        observeCityFromDataStore()
    }

    private fun hasLocationPermission(): Boolean {
        val ctx = getApplication<Application>()
        return ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun observeCityFromDataStore() {
        viewModelScope.launch {
            DataStoreManager.cityNamePrefFlow(getApplication())
                .collectLatest { city ->
                    if (!city.isNullOrBlank()) {
                        currentCity = city
                        getCurrentWeather(city, forceRefresh = true)
                        showCitySelectionDialog.value = false
                    } else {
                        if (hasLocationPermission()) {
                            fetchAndSaveDeviceCity()
                        } else {
                            showCitySelectionDialog.value = true
                        }
                    }
                }
        }
    }

    private fun fetchAndSaveDeviceCity() {
        viewModelScope.launch {
            try {
                val autoCity = getDeviceCityUseCase.execute()
                if (autoCity.isNotBlank()) {
                    DataStoreManager.updateCityName(getApplication(), autoCity)
                    // No need to call getCurrentWeather directly
                    // Flow will re-emit and trigger the block again
                } else {
                    showCitySelectionDialog.value = true
                }
            } catch (e: Exception) {
                showCitySelectionDialog.value = true
            }
        }
    }

    fun retryDeviceLocation() {
        if (currentCity.isBlank()) {
            fetchAndSaveDeviceCity()
        }
    }

    /**
     * Fetches current weather.
     * @param city The city name.
     * @param forceRefresh If true, ignores cache and forces API refresh.
     */
    fun getCurrentWeather(city: String=currentCity,forceRefresh: Boolean = false) {
        if (!isWeatherLoaded || forceRefresh) {
            viewModelScope.launch {
                weatherLiveData.value = Resource.Loading()
                try {
                    val result = getWeatherUseCase.getCurrentWeather(city, forceRefresh)
                    weatherLiveData.value = result
                    if (result is Resource.Success) {
                        isWeatherLoaded = true
                        currentCity = city
                    }
                } catch (e: Exception) {
                    weatherLiveData.value = Resource.Error(null, "An error occurred: ${e.message}")
                }
            }
        }
    }

    /**
     * Получаем pollution по координатам города
     */
    private fun getAirVisualData(city: String) {
        viewModelScope.launch {
            val coords = WeatherUtils.getCoordinatesFromCity(getApplication(), city, getCoordinatesUseCase)
            coords?.let { (lat, lon) ->
                val result = getAirVisualDataUseCase(lat, lon, "f3324376-d944-44f4-bda6-e936f76bbbeb")
                airVisualLiveData.value = result
            }
        }
    }

    /**
     *  Унифицированный метод:
     * обновляет и погоду, и pollution
     */
    fun refreshWeather(city: String = currentCity) {
        isWeatherLoaded = false
        getCurrentWeather(city, forceRefresh = true)
        getAirVisualData(city)
    }

    /**
     * Called when user selects a city in dialog.
     * Updates city in DataStore and triggers weather refresh automatically.
     */
    fun onCitySelected(cityName: String) {
        viewModelScope.launch {
            try {
                if (cityName.isNotBlank()) {
                    DataStoreManager.updateCityName(getApplication(), cityName)
                    // Weather refresh will be triggered via DataStore observer
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