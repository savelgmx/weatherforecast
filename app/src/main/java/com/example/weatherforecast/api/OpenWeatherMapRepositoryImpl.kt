package com.example.weatherforecast.api


import android.util.Log
import androidx.lifecycle.LiveData
import com.example.weatherforecast.db.CurrentWeatherEntity
import com.example.weatherforecast.db.OpenWeatherMapDao
import com.example.weatherforecast.di.ContextProvider
import com.example.weatherforecast.response.Clouds
import com.example.weatherforecast.response.Coord
import com.example.weatherforecast.response.ForecastResponse
import com.example.weatherforecast.response.Main
import com.example.weatherforecast.response.Sys
import com.example.weatherforecast.response.WeatherResponse
import com.example.weatherforecast.response.Wind
import com.example.weatherforecast.utils.AppConstants
import com.example.weatherforecast.utils.DefineDeviceLocation
import com.example.weatherforecast.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
class OpenWeatherMapRepositoryImpl @Inject constructor(
    private val openWeatherMapAPI: OpenWeatherMapAPI,
    private val openWeatherMapDao: OpenWeatherMapDao,
    private val contextProvider: ContextProvider
) : OpenWeatherMapRepository {
    private val repositoryScope = CoroutineScope(Dispatchers.IO)
    private var latitude:String
    private var longitude:String
    private var cityName:String
    init {
        val locationProvider = DefineDeviceLocation(contextProvider.provideContext())
        val locationArray = locationProvider.getLocation()

        if (locationArray.isNotEmpty() && locationArray.size == 3) {
            latitude = locationArray[0] ?: ""
            longitude = locationArray[1] ?: ""
            cityName = locationArray[2] ?: ""

            Log.d("getlocation response",latitude+" "+longitude+cityName)
        } else {
            // Handle case when location retrieval fails
            // You might want to provide default values or throw an exception
            //i prefer use default values
            latitude=AppConstants.CITY_LAT
            longitude=AppConstants.CITY_LON
            cityName=AppConstants.CITY_FORECAST
        }
    }

    override suspend fun getCurrentWeather(): Resource<WeatherResponse> {
        val response = openWeatherMapAPI.getCurrentWeather(
            cityName,
            // as soon as they will be implemented
            "metric",AppConstants.API_KEY,
            Locale.getDefault().language)
        Log.d("Repository response", response.body().toString())

        return if (response.isSuccessful) {
            val data = response.body()
            if (data != null) {

                addCurrentWeatherDataToDB(data,repositoryScope) //first we

                Resource.Success(data)

            } else {
                Resource.Error(null, "Empty response body")
            }
        } else {
            Resource.Error(null, "No data found")
        }
    }

    override suspend fun getForecastWeather(): Resource<ForecastResponse> {
        try {
            Log.d("Log Lan response", longitude +"  "+ latitude)

            val response = openWeatherMapAPI.getForecastWeather(
                AppConstants.API_KEY,
                longitude,
                latitude,
                "metric",
                Locale.getDefault().language
            )
            Log.d("Forecast response",response.body().toString())
            return if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {

                    addForecastDataToDB(data,repositoryScope)
                    Resource.Success(data)

                } else {
                    Resource.Error(null, "Empty  forecast response body")
                }
            } else {
                Resource.Error(null, "No data found")
            }
        } catch (e: Exception) {
            Log.e("ForecastAPIError", "Error fetching forecast: ${e.message}")
            return Resource.Error(null, "An error occurred: ${e.message}")
        }
    }


    override suspend fun getWeatherForecastFromDB(): LiveData<List<CurrentWeatherEntity>> {

        return openWeatherMapDao.getWeather()
    }

    private suspend fun addCurrentWeatherDataToDB(data: WeatherResponse?, coroutineScope: CoroutineScope) {
        if (data != null) {
            coroutineScope.launch(Dispatchers.IO) {

                openWeatherMapDao.deleteAllFromWeather()
                openWeatherMapDao.insertOrUpdate(
                    CurrentWeatherEntity(
                        coord = data.coord,
                        weather = data.weather,
                        base = data.base,
                        main = data.main,
                        visibility = data.visibility,
                        wind = data.wind,
                        clouds = data.clouds,
                        dt = data.dt,
                        sys = data.sys,
                        timezone = data.timezone,
                        name = data.name,
                        cod = data.cod
                    )
                )
            }
        } else {
            Log.e("AddCurrentData", "WeatherResponse data is null")
        }
    }
    private suspend fun addForecastDataToDB(data:ForecastResponse?,coroutineScope: CoroutineScope){
        if (data!=null){
            coroutineScope.launch(Dispatchers.IO) {
                openWeatherMapDao.deleteAllFromForecastResponse()
                openWeatherMapDao.upsertSevenDaysForecast(data)
            }
        }
    }

}

