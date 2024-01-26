package com.example.weatherforecast.db

import androidx.lifecycle.LiveData

interface OpenWeatherMapDao {
//TODO queryes to room db
    fun getWeather(): LiveData<List<CurrentWeatherEntity>>

}
