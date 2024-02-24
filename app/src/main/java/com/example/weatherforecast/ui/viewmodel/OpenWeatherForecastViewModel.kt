package com.example.weatherforecast.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.api.OpenWeatherMapRepository
import com.example.weatherforecast.response.ForecastResponse
import com.example.weatherforecast.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OpenWeatherForecastViewModel @Inject constructor(
    private val repository: OpenWeatherMapRepository
):ViewModel(){
    private val _forecastLiveData = MutableLiveData<Resource<ForecastResponse>>()
    val forecastLiveData: LiveData<Resource<ForecastResponse>> get()=_forecastLiveData

    private var isForecastLoaded = false //flag to track is forecast loaded or jet not

    init {
        getForecastWeather()
        isForecastLoaded = true
    }
    fun getForecastWeather(){
        if(!isForecastLoaded){
            viewModelScope.launch {
                _forecastLiveData.value = Resource.Loading()
                try {
                    val result = repository.getForecastWeather()
                    _forecastLiveData.value=result

                }catch (e:Exception){
                    _forecastLiveData.value = Resource.Error(null,"An error occured: ${e.message}")
                }

            }


        }
    }
}