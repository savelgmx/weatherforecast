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


    private fun addForecastDataToDB(data: WeatherResponse?, coroutineScope: CoroutineScope) {
        data?.let { weatherResponse ->
            val currentWeatherEntity = CurrentWeatherEntity(
                coord = weatherResponse.coord ?: Coord(0.0, 0.0),
                weather = weatherResponse.weather ?: emptyList(),
                base = weatherResponse.base ?: "",
                main = weatherResponse.main ?: Main(0.0, 0.0, 0.0, 0.0, 0, 0),
                visibility = weatherResponse.visibility ?: 0,
                wind = weatherResponse.wind ?: Wind(0.0, 0),
                clouds = weatherResponse.clouds ?: Clouds(0),
                dt = weatherResponse.dt ?: 0L,
                sys = weatherResponse.sys ?: Sys(0, 0, "", 0L, 0L),
                timezone = weatherResponse.timezone ?: 0,
                name = weatherResponse.name ?: "",
                cod = weatherResponse.cod ?: 0
            )
            // Insert or update the data in the database
            coroutineScope.launch(Dispatchers.IO) {
                openWeatherMapDao.insertOrUpdate(currentWeatherEntity)
            }
        } ?: run {
            Log.e("AddForecastData", "WeatherResponse data is null")
        }
    }


}

