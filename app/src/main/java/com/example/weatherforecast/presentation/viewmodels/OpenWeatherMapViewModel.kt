package com.example.weatherforecast.presentation.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.domain.usecases.GetDeviceCityUseCase
import com.example.weatherforecast.domain.usecases.GetWeatherUseCase
import com.example.weatherforecast.domain.usecases.SetCityNameUseCase
import com.example.weatherforecast.response.WeatherResponse
import com.example.weatherforecast.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OpenWeatherMapViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getDeviceCityUseCase: GetDeviceCityUseCase,
    private val setCityNameUseCase: SetCityNameUseCase
) : ViewModel() {

    val weatherLiveData: MutableState<Resource<WeatherResponse>> = mutableStateOf(Resource.Loading())
    val showCitySelectionDialog: MutableState<Boolean> = mutableStateOf(false)
    private var isWeatherLoaded = false // Flag to track if weather data is already loaded
    private var currentCity: String = ""

    init {
        viewModelScope.launch {
            initializeWeather()
        }
    }

    private suspend fun initializeWeather() {
        try {
            currentCity = getDeviceCityUseCase.execute()

            if (currentCity.isEmpty()) {
                // No city available, show dialog
                showCitySelectionDialog.value = true
            } else {
                // City available, load weather
                getCurrentWeather(currentCity)
            }
        } catch (e: Exception) {
            // Error getting city, show dialog
            showCitySelectionDialog.value = true
        }
    }

    /**
     * Запрашивает текущую погоду для указанного города.
     * @param city Название города.
     * @param forceRefresh Если true, игнорирует кэш.
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

    fun refreshWeather(city: String = currentCity) {
        isWeatherLoaded = false
        getCurrentWeather(city, forceRefresh = true)
    }

    fun onCitySelected(cityName: String) {
        viewModelScope.launch {
            try {
                setCityNameUseCase.execute(cityName)
                currentCity = cityName
                showCitySelectionDialog.value = false
                getCurrentWeather(cityName, forceRefresh = true)
            } catch (e: Exception) {
                weatherLiveData.value = Resource.Error(null, "Failed to set city: ${e.message}")
            }
        }
    }

    fun dismissCitySelectionDialog() {
        showCitySelectionDialog.value = false
    }
 }