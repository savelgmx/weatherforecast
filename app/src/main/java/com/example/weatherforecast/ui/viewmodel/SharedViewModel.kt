package com.example.weatherforecast.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {
    val temperatureUnitsLiveData = MutableLiveData<Boolean>()
    val distanceUnitsLiveData = MutableLiveData<String>()
}

