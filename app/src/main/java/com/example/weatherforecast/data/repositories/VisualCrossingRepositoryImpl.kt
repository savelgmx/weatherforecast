package com.example.weatherforecast.data.repositories


import android.util.Log
import androidx.lifecycle.LiveData
import com.example.weatherforecast.BuildConfig
import com.example.weatherforecast.data.db.CurrentWeatherEntity
import com.example.weatherforecast.data.mappers.WeatherMapper
import com.example.weatherforecast.data.mappers.WeatherResponseMapper
import com.example.weatherforecast.data.remote.WeatherApiService
import com.example.weatherforecast.di.ContextProvider
import com.example.weatherforecast.response.ForecastResponse
import com.example.weatherforecast.response.WeatherResponse
import com.example.weatherforecast.utils.AppConstants
import com.example.weatherforecast.utils.DefineDeviceLocation
import com.example.weatherforecast.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class VisualCrossingRepositoryImpl @Inject constructor(
    private val apiService: WeatherApiService,
    contextProvider: ContextProvider
) : VisualCrossingRepository {
    private var latitude:String
    private var longitude:String
    private var cityName:String

    init {
        val defineLocation=DefineDeviceLocation(contextProvider.provideContext())
        val locationArray= defineLocation.getLocation()
        if (locationArray.isNotEmpty() && locationArray.size == 3) {
            latitude = locationArray[0] ?: ""
            longitude = locationArray[1] ?: ""
            cityName = locationArray[2] ?: ""
            Log.d("getlocation response", "$latitude $longitude $cityName")
        } else {
            // Handle case when location retrieval fails
            // You might want to provide default values or throw an exception
            //i prefer use default values
            latitude= AppConstants.CITY_LAT
            longitude= AppConstants.CITY_LON
            cityName= AppConstants.CITY_FORECAST
        }

    }

    override suspend fun getCurrentWeather(): Resource<WeatherResponse> {
        return withContext(Dispatchers.IO) {
            try {
            val response = apiService.getWeather(
                location = cityName,
                apiKey = BuildConfig.API_KEY,
                include = "days,hours"
            )
            val dailyWeather = WeatherMapper.toDailyWeather(response.days.first())
            val weatherResponse = WeatherResponseMapper.toWeatherResponse(dailyWeather, cityName)  // Передайте cityName
            Resource.Success(weatherResponse)
        } catch (e: IOException) {
            Resource.Internet()
        } catch (e: HttpException) {
            Resource.Error(msg = "API error: ${e.message()}")
        } catch (e: Exception) {
            Resource.Error(msg = "Unknown error: ${e.message}")
        }
    }
    }

    override suspend fun getForecastWeather(): Resource<ForecastResponse> {
        return withContext(Dispatchers.IO) {
            try {
            val response = apiService.getWeather(
                location = cityName,
                apiKey = BuildConfig.API_KEY,
                include = "days,hours"
            )
            val dailyWeathers = response.days.map { WeatherMapper.toDailyWeather(it) }
            val forecastResponse = WeatherResponseMapper.toForecastResponse(dailyWeathers)
            Resource.Success(forecastResponse)
        } catch (e: IOException) {
            Resource.Internet()
        } catch (e: HttpException) {
            Resource.Error(msg = "API error: ${e.message()}")
        } catch (e: Exception) {
            Resource.Error(msg = "Unknown error: ${e.message}")
        }
    }
    }

    override suspend fun getWeatherForecastFromDB(): LiveData<List<CurrentWeatherEntity>> {
        throw NotImplementedError("Database implementation not provided")
    }
}