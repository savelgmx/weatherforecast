package com.example.weatherforecast.presentation.viewmodels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.BuildConfig
import com.example.weatherforecast.domain.usecases.GetWeatherUseCase
import com.example.weatherforecast.response.ForecastResponse
import com.example.weatherforecast.utils.AppConstants
import com.example.weatherforecast.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OpenWeatherForecastViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase
):ViewModel(){

    val forecastLiveData: MutableState<Resource<ForecastResponse>> = mutableStateOf(Resource.Loading())
    private var isForecastLoaded = false // Flag to track if forecast data is already loaded
    private var currentCity:String= AppConstants.CITY_FORECAST

    init {
        getForecastWeather(currentCity)
       // isForecastLoaded = true
    }
    fun getForecastWeather(city: String,forceRefresh:Boolean=false) {
        if(!isForecastLoaded||forceRefresh){
            viewModelScope.launch {
                forecastLiveData.value = Resource.Loading()
                val result = getWeatherUseCase.getForecastWeather(city)
                    forecastLiveData.value = result
                if (result is Resource.Success) {
                    if(BuildConfig.DEBUG) Log.d("Map view model response", result.data.toString())
                    isForecastLoaded = true // Set only on success
                    currentCity=city
                }
            }
        }
    }
    fun refreshWeather(city: String = currentCity) {
        isForecastLoaded = false // Сбрасываем флаг для принудительного обновления
        getForecastWeather(city, forceRefresh = true)
    }

}

