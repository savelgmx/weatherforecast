package com.example.weatherforecast.presentation.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.domain.usecases.GetDeviceCityUseCase
import com.example.weatherforecast.domain.usecases.GetWeatherUseCase
import com.example.weatherforecast.response.WeatherResponse
import com.example.weatherforecast.utils.AppConstants
import com.example.weatherforecast.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OpenWeatherMapViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getDeviceCityUseCase:GetDeviceCityUseCase
) : ViewModel() {

    val weatherLiveData: MutableState<Resource<WeatherResponse>> = mutableStateOf(Resource.Loading())
    private var isWeatherLoaded = false // Flag to track if weather data is already loaded
    private var currentCity:String = AppConstants.CITY_FORECAST

    init {
        viewModelScope.launch {
            currentCity = getDeviceCityUseCase.execute()
            getCurrentWeather(currentCity)
        }
    }

    /**
     * Запрашивает текущую погоду для указанного города.
     * @param city Название города.
     * @param forceRefresh Если true, игнорирует кэш.
     */
    fun getCurrentWeather(city: String = currentCity, forceRefresh: Boolean = false) {
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
     * Принудительно обновляет данные о погоде.
     * @param city Название города.
     */
    fun refreshWeather(city: String = currentCity) {
        isWeatherLoaded = false // Сбрасываем флаг для принудительного обновления
        getCurrentWeather(city, forceRefresh = true)
    }
}