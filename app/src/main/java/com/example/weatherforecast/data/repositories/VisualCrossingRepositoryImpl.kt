package com.example.weatherforecast.data.repositories

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.weatherforecast.BuildConfig
import com.example.weatherforecast.data.db.DailyWeatherEntity
import com.example.weatherforecast.data.db.HourlyWeatherEntity
import com.example.weatherforecast.data.db.WeatherDao
import com.example.weatherforecast.data.mappers.EntityMapper
import com.example.weatherforecast.data.mappers.WeatherMapper
import com.example.weatherforecast.data.mappers.WeatherResponseMapper
import com.example.weatherforecast.data.remote.WeatherApiResponse
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
import java.util.Locale
import javax.inject.Inject

class VisualCrossingRepositoryImpl @Inject constructor(
    private val apiService: WeatherApiService,
    private val contextProvider: ContextProvider,
    private val weatherDao: WeatherDao
) : VisualCrossingRepository {
    private var latitude: String = AppConstants.CITY_LAT
    private var longitude: String = AppConstants.CITY_LON
    private var cityName: String = AppConstants.CITY_FORECAST
    private lateinit var devLocaleLanguage: String

    init {
        val defineLocation = DefineDeviceLocation(contextProvider.provideContext())
        val locationArray = defineLocation.getLocation()
        if (locationArray.isNotEmpty() && locationArray.size == 3) {
            latitude = locationArray[0] ?: AppConstants.CITY_LAT
            longitude = locationArray[1] ?: AppConstants.CITY_LON
            cityName = locationArray[2] ?: AppConstants.CITY_FORECAST
            Log.d("getlocation response", "$latitude $longitude $cityName")
        }
        devLocaleLanguage = Locale.getDefault().language
    }

    private fun isNetworkAvailable(): Boolean {
        val context = contextProvider.provideContext()
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private suspend fun getCurrentWeatherFromAPI(): Resource<WeatherResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getWeather(
                    location = cityName,
                    apiKey = BuildConfig.API_KEY,
                    include = "days,hours",
                    lang = devLocaleLanguage
                )
                val dailyWeather = WeatherMapper.toDailyWeather(response.days.first())
                val weatherResponse = WeatherResponseMapper.toWeatherResponse(dailyWeather, cityName)
                Resource.Success(weatherResponse)
            } catch (e: IOException) {
                Resource.Error(null, "Network error: ${e.message}")
            } catch (e: HttpException) {
                Resource.Error(null, "API error: ${e.message()}")
            } catch (e: Exception) {
                Resource.Error(null, "Unknown error: ${e.message}")
            }
        }
    }

    private suspend fun getWeatherApiResponse(): Resource<WeatherApiResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getWeather(
                    location = cityName,
                    apiKey = BuildConfig.API_KEY,
                    include = "days,hours",
                    lang = devLocaleLanguage
                )
                Resource.Success(response)
            } catch (e: IOException) {
                Resource.Error(null, "Network error: ${e.message}")
            } catch (e: HttpException) {
                Resource.Error(null, "API error: ${e.message()}")
            } catch (e: Exception) {
                Resource.Error(null, "Unknown error: ${e.message}")
            }
        }
    }

    private suspend fun insertWeatherData(response: WeatherApiResponse) {
        response.days.forEach { day ->
            val dailyWeather = WeatherMapper.toDailyWeather(day)
            val dailyEntity = DailyWeatherEntity(
                id = 0, // Room auto-generates
                date = dailyWeather.date,
                dt = dailyWeather.dt,
                visibility = dailyWeather.visibility,
                temp = dailyWeather.temp,
                feelsLike = dailyWeather.feelsLike,
                tempMin = dailyWeather.tempMin,
                tempMax = dailyWeather.tempMax,
                pressure = dailyWeather.pressure,
                humidity = dailyWeather.humidity,
                windSpeed = dailyWeather.windSpeed,
                windDeg = dailyWeather.windDeg,
                cloudiness = dailyWeather.cloudiness,
                description = dailyWeather.description,
                icon = dailyWeather.icon,
                sunrise = dailyWeather.sunrise,
                sunset = dailyWeather.sunset,
                moonPhase = dailyWeather.moonPhase,
                dew=dailyWeather.dew,              //point of dew (точка росы)
                uvindex=dailyWeather.uvindex             //UV index (УФ индекс)
            )

            val dailyId = weatherDao.insertDailyWeather(dailyEntity)
            val hourlyEntities = dailyWeather.hours?.map { hour ->
                HourlyWeatherEntity(
                    id = 0, // Room auto-generates
                    dailyId = dailyId.toInt(),
                    time = hour.time,
                    dt = hour.dt,
                    temp = hour.temp,
                    feelsLike = hour.feelsLike,
                    pressure = hour.pressure,
                    humidity = hour.humidity,
                    windSpeed = hour.windSpeed,
                    windDeg = hour.windDeg,
                    cloudiness = hour.cloudiness,
                    description = hour.description,
                    icon = hour.icon
                )
            } ?: emptyList()
            weatherDao.insertHourlyWeather(hourlyEntities)
        }
    }

    override suspend fun getCurrentWeather(): Resource<WeatherResponse> {
        return withContext(Dispatchers.IO) {
            // Шаг 1: Проверяем наличие данных в базе
            val hasData = weatherDao.getDailyWeatherCount() > 0
            val dbWeather = getCurrentWeatherFromDB()

            if (!hasData || dbWeather == null) {
                // База пуста или данные недоступны: пробуем API
                if (!isNetworkAvailable()) {
                    return@withContext Resource.Error(null, "Please connect to the internet to fetch weather data.")
                }
                val apiResult = getWeatherApiResponse()
                if (apiResult is Resource.Success) {
                    apiResult.data?.let { insertWeatherData(it) }
                    getCurrentWeatherFromDB()?.let { Resource.Success(it) }
                        ?: Resource.Error(null, "Failed to get data from DB after insertion")
                } else {
                    Resource.Error(null, "Unknown error")
                }
            } else {
                // Данные есть: проверяем актуальность
                val currentTime = System.currentTimeMillis()
                val lastUpdateTime = weatherDao.getLastUpdateTime()
                val isDataFresh = lastUpdateTime != null && currentTime <= lastUpdateTime + AppConstants.CURRENT_WEATHER_UPDATE_INTERVAL

                if (isDataFresh) {
                    Resource.Success(dbWeather)
                } else if (!isNetworkAvailable()) {
                    // Данные устарели, но интернета нет: возвращаем устаревшие
                    Resource.Success(dbWeather)
                } else {
                    // Данные устарели, интернет есть: обновляем через API
                    val apiResult = getCurrentWeatherFromAPI()
                    if (apiResult is Resource.Success) {
                        clearDatabase()
                        syncWeather()
                        apiResult
                    } else {
                        // При ошибке API возвращаем устаревшие данные
                        Resource.Success(dbWeather)
                    }
                }
            }
        }
    }

    private suspend fun getCurrentWeatherFromDB(): WeatherResponse? {
        val entity = weatherDao.getCurrentDailyWeatherEntity()
        if (entity != null) {
            val dailyWeather = EntityMapper.toDailyWeather(entity)
            return WeatherResponseMapper.toWeatherResponse(dailyWeather, cityName)
        }
        return null
    }

    override suspend fun getForecastWeather(): Resource<ForecastResponse> {
        return withContext(Dispatchers.IO) {
            // Шаг 1: Проверяем наличие данных в базе
            val hasData = weatherDao.getDailyWeatherCount() > 0
            val dbForecast = getForecastWeatherFromDB()

            if (!hasData || dbForecast == null) {
                // База пуста или данные недоступны: пробуем API
                if (!isNetworkAvailable()) {
                    return@withContext Resource.Error(null, "Please connect to the internet to fetch weather data.")
                }
                val apiResult = getWeatherApiResponse()
                if (apiResult is Resource.Success) {
                    apiResult.data?.let { insertWeatherData(it) }
                    getForecastWeatherFromDB()?.let { Resource.Success(it) }
                        ?: Resource.Error(null, "Failed to get forecast data from DB after insertion")
                } else {
                    Resource.Error(null, "Unknown error")
                }
            } else {
                // Данные есть: проверяем актуальность
                val currentTime = System.currentTimeMillis()
                val lastUpdateTime = weatherDao.getLastUpdateTime()
                val isDataFresh = lastUpdateTime != null && currentTime <= lastUpdateTime + AppConstants.FORECAST_UPDATE_INTERVAL

                if (isDataFresh) {
                    Resource.Success(dbForecast)
                } else if (!isNetworkAvailable()) {
                    // Данные устарели, но интернета нет: возвращаем устаревшие
                    Resource.Success(dbForecast)
                } else {
                    // Данные устарели, интернет есть: обновляем через API
                    val apiResult = getForecastWeatherFromAPI()
                    if (apiResult is Resource.Success) {
                        clearDatabase()
                        syncWeather()
                        apiResult
                    } else {
                        // При ошибке API возвращаем устаревшие данные
                        Resource.Success(dbForecast)
                    }
                }
            }
        }
    }

    private suspend fun getForecastWeatherFromAPI(): Resource<ForecastResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getWeather(
                    location = cityName,
                    apiKey = BuildConfig.API_KEY,
                    include = "days,hours",
                    lang = devLocaleLanguage
                )
                val dailyWeathers = response.days.map { WeatherMapper.toDailyWeather(it) }
                val forecastResponse = WeatherResponseMapper.toForecastResponse(dailyWeathers)
                Resource.Success(forecastResponse)
            } catch (e: IOException) {
                Resource.Error(null, "Network error: ${e.message}")
            } catch (e: HttpException) {
                Resource.Error(null, "API error: ${e.message()}")
            } catch (e: Exception) {
                Resource.Error(null, "Unknown error: ${e.message}")
            }
        }
    }

    private suspend fun getForecastWeatherFromDB(): ForecastResponse? {
        val entities = weatherDao.getAllDailyWeatherSync()
        if (entities.isNotEmpty()) {
            val dailyWeathers = entities.map { EntityMapper.toDailyWeather(it) }
            return WeatherResponseMapper.toForecastResponse(dailyWeathers)
        }
        return null
    }

    private suspend fun clearDatabase() {
        weatherDao.deleteAllDailyWeather()
        weatherDao.deleteAllHourlyWeather()
    }

    suspend fun syncWeather() {
        clearDatabase()
        val response = apiService.getWeather(
            location = cityName,
            apiKey = BuildConfig.API_KEY,
            include = "days,hours",
            lang = devLocaleLanguage
        )
        insertWeatherData(response)
    }
}