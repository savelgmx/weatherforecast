package com.example.weatherforecast.api


import androidx.lifecycle.LiveData
import com.example.weatherforecast.db.CurrentWeatherEntity
import com.example.weatherforecast.db.OpenWeatherMapDao
import com.example.weatherforecast.di.NetworkObject


import com.example.weatherforecast.response.WeatherResponse
import com.example.weatherforecast.utils.Resource

import javax.inject.Inject



class OpenWeatherMapRepositoryImpl @Inject constructor(
    private val openWeatherMapAPI: OpenWeatherMapAPI,
    private val openWeatherMapDao: OpenWeatherMapDao
) : OpenWeatherMapRepository {

    override suspend fun getWeatherForecast(city: String): Resource<WeatherResponse> {
        val response = openWeatherMapAPI.getCurrentWeather(city, NetworkObject.API_KEY)
        return if (response.isSuccessful) {
            val data = response.body()
            if (data != null) {
                Resource.Success(data)
             //  addForecastDataToDB(data)
            } else {
                Resource.Error(null, "Empty response body")
            }
        } else {
            Resource.Error(null, "No data found")
        }
    }

    override suspend fun getWeatherForecastFromDB(): LiveData<List<CurrentWeatherEntity>> {

        return openWeatherMapDao.getWeather()
    }
    private fun addForecastDataToDB(data:Resource<WeatherResponse>){
        TODO("Not yet implemented")
    }
}

