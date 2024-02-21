package com.example.weatherforecast.api


import android.util.Log
import androidx.lifecycle.LiveData
import com.example.weatherforecast.db.CurrentWeatherEntity
import com.example.weatherforecast.db.OpenWeatherMapDao
import com.example.weatherforecast.response.Clouds
import com.example.weatherforecast.response.Coord
import com.example.weatherforecast.response.Main
import com.example.weatherforecast.response.Sys


import com.example.weatherforecast.response.WeatherResponse
import com.example.weatherforecast.response.Wind
import com.example.weatherforecast.utils.AppConstants
import com.example.weatherforecast.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

import javax.inject.Inject



class OpenWeatherMapRepositoryImpl @Inject constructor(
    private val openWeatherMapAPI: OpenWeatherMapAPI,
    private val openWeatherMapDao: OpenWeatherMapDao
) : OpenWeatherMapRepository {
    private val repositoryScope = CoroutineScope(Dispatchers.IO)
    override suspend fun getWeatherForecast(city: String): Resource<WeatherResponse> {
        val response = openWeatherMapAPI.getCurrentWeather(city,
            "metric",AppConstants.API_KEY,
            Locale.getDefault().language)
        Log.d("Repository response", response.body().toString())

        return if (response.isSuccessful) {
            val data = response.body()
            if (data != null) {

                addForecastDataToDB(data,repositoryScope) //first we
                Resource.Success(data)

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


    private suspend fun addForecastDataToDB(data: WeatherResponse?, coroutineScope: CoroutineScope) {
    if (data != null) {
           // coroutineScope.launch(Dispatchers.IO) {
            openWeatherMapDao.insertOrUpdate(
                CurrentWeatherEntity(
                    coord = data.coord ?: Coord(0.0, 0.0),
                    weather = data.weather ?: emptyList(),
                    base = data.base ?: "",
                    main = data.main ?: Main(0.0, 0.0, 0.0, 0.0, 0, 0),
                    visibility = data.visibility ?: 0,
                    wind = data.wind ?: Wind(0.0, 0),
                    clouds = data.clouds ?: Clouds(0),
                    dt = data.dt ?: 0L,
                    sys = data.sys ?: Sys(0, 0, "", 0L, 0L),
                    timezone = data.timezone ?: 0,
                    name = data.name ?: "",
                    cod = data.cod ?: 0
                )
            )
           // }
    } else {
            Log.e("AddForecastData", "WeatherResponse data is null")
        }
    }


}

