package com.example.weatherforecast.data.repositories


import android.util.Log
import androidx.lifecycle.LiveData
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
    contextProvider: ContextProvider,
    private val  weatherDao: WeatherDao
) : VisualCrossingRepository {
    private var latitude:String
    private var longitude:String
    private var cityName:String
    private lateinit var devLocaleLanguage:String //define system locale for substitute into query

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
        devLocaleLanguage = Locale.getDefault().getLanguage();

    }

    private suspend fun getCurrentWeatherFromAPI(): Resource<WeatherResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getWeather(
                    location = cityName,
                    apiKey = BuildConfig.API_KEY,
                    include = "days,hours",
                    lang=devLocaleLanguage
                )
                val dailyWeather = WeatherMapper.toDailyWeather(response.days.first())
                val weatherResponse = WeatherResponseMapper.toWeatherResponse(dailyWeather, cityName)  // Передайте cityName
                Resource.Success(weatherResponse)
            } catch (e: IOException) {
                Resource.Internet()
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
                    lang=devLocaleLanguage
                )
                Resource.Success(response)
            } catch (e: IOException) {
                Resource.Internet()
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
                date = dailyWeather.date,
                dt = dailyWeather.dt,
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
                moonPhase = dailyWeather.moonPhase
            )

            val dailyId = weatherDao.insertDailyWeather(dailyEntity)
            val hourlyEntities = dailyWeather.hours?.map { hour ->
                HourlyWeatherEntity(
                    dailyId = dailyId,
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
            val currentTime = System.currentTimeMillis()
            val lastUpdateTime = weatherDao.getLastUpdateTime()
            val hasData = weatherDao.getDailyWeatherCount() > 0

            if (!hasData) {
                // База данных пуста: получаем из API, заполняем БД, возвращаем из БД
                val apiResult = getWeatherApiResponse()
                if (apiResult is Resource.Success) {
                    apiResult.data?.let { insertWeatherData(it) }
                    val dbWeather = getCurrentWeatherFromDB()
                    if (dbWeather != null) {
                        Resource.Success(dbWeather)
                    } else {
                        Resource.Error(null, "Failed to get data from DB after insertion")
                    }
                } else {
                    apiResult as Resource<WeatherResponse>
                }
            } else if (lastUpdateTime != null && currentTime > lastUpdateTime + AppConstants.DATA_UPDATE_INTERVAL) {
                // БД не пуста, но данные устарели: получаем из API, очищаем БД, заполняем БД, возвращаем из API
                val apiResult = getCurrentWeatherFromAPI()
                if (apiResult is Resource.Success) {
                    clearDatabase()
                    syncWeather()
                    apiResult
                } else {
                    apiResult
                }
            } else {
                // БД не пуста и данные свежие: возвращаем из БД
                val dbWeather = getCurrentWeatherFromDB()
                if (dbWeather != null) {
                    Resource.Success(dbWeather)
                } else {
                    // Если данных в базе нет, пробуем получить из API
                    val apiResult = getCurrentWeatherFromAPI()
                    if (apiResult is Resource.Success) {
                        clearDatabase()
                        syncWeather()
                        apiResult
                    } else {
                        Resource.Error(null, "No data in DB and failed to fetch from API")
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
            try {
                val response = apiService.getWeather(
                    location = cityName,
                    apiKey = BuildConfig.API_KEY,
                    include = "days,hours",
                    lang=devLocaleLanguage
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

    override suspend fun getWeatherForecastFromDB(): LiveData<List<DailyWeatherEntity>> {
        return weatherDao.getAllDailyWeather()
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
            lang=devLocaleLanguage
        )
        insertWeatherData(response)
    }
}