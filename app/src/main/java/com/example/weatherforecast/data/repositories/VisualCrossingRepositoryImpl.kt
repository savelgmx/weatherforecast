package com.example.weatherforecast.data.repositories

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.weatherforecast.BuildConfig
import com.example.weatherforecast.components.DataStoreManager
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
import com.example.weatherforecast.utils.WeatherUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.util.Locale
import javax.inject.Inject

/**
 * Реализация репозитория для получения данных о погоде через API Visual Crossing.
 * Управляет кэшированием в базе данных Room и запросами к API.
 */
class VisualCrossingRepositoryImpl @Inject constructor(
    private val apiService: WeatherApiService, // Сервис для API-запросов
    private val contextProvider: ContextProvider, // Провайдер контекста
    private val weatherDao: WeatherDao // DAO для работы с базой данных
) : VisualCrossingRepository {

    private var devLocaleLanguage: String

    init {
        // Инициализация языка устройства
        devLocaleLanguage = Locale.getDefault().language
        // НЕ вызываем initializeCityName() здесь, так как это suspend функция
    }

    /**
     * Gets the current city name from WeatherUtils
     */
    override suspend fun getDeviceCity(): String {
        return WeatherUtils.getCityName(contextProvider.provideContext())
    }

    /**
     * Sets the city name using WeatherUtils
     */
    override suspend fun setCityName(city: String) {
        WeatherUtils.saveCityName(contextProvider.provideContext(), city)
        if (BuildConfig.DEBUG) Log.d("City set", " $city")
    }

    /**
     * Проверяет наличие интернет-соединения.
     * @return true, если интернет доступен, иначе false.
     */
    private fun isNetworkAvailable(): Boolean {
        val context = contextProvider.provideContext()
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    /**
     * Сохраняет данные о погоде в базу данных Room.
     * Использует транзакцию для атомарной вставки DailyWeatherEntity и HourlyWeatherEntity.
     * @param response Ответ API с данными о погоде.
     */
    private suspend fun insertWeatherData(response: WeatherApiResponse) {
        withContext(Dispatchers.IO) {
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
                    uvindex=dailyWeather.uvindex,             //UV index (УФ индекс)
                    cityName = WeatherUtils.getCityName(contextProvider.provideContext()) ?: "Unknown"
                )

                val hourlyEntities = dailyWeather.hours?.map { hour ->
                    HourlyWeatherEntity(
                        id = 0, // Room auto-generates
                        dailyId = 0, // Placeholder, будет обновлён в транзакции
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

                // Используем транзакцию для атомарной вставки
                weatherDao.insertWeatherTransaction(dailyEntity, hourlyEntities)
                if (BuildConfig.DEBUG) Log.d("Inserted dailyId", weatherDao.insertDailyWeather(dailyEntity).toString())
            }
        }
    }

    /**
     * Получает текущую погоду из базы данных.
     * @return WeatherResponse или null, если данных нет.
     */
    private suspend fun getCurrentWeatherFromDB(): WeatherResponse? {
        val entity = weatherDao.getCurrentDailyWeatherEntity()
        if (entity != null) {
            val dailyWeather = EntityMapper.toDailyWeather(entity)
            return WeatherResponseMapper.toWeatherResponse(dailyWeather, entity.cityName ?: "Unknown")
        }
        return null
    }

    /**
     * Получает прогноз погоды из базы данных.
     * @return ForecastResponse или null, если данных нет.
     */
    private suspend fun getForecastWeatherFromDB(): ForecastResponse? {
        val entities = weatherDao.getAllDailyWeatherSync()
        if (entities.isNotEmpty()) {
            val dailyWeathers = entities.map { EntityMapper.toDailyWeather(it) }
            return WeatherResponseMapper.toForecastResponse(dailyWeathers)
        }
        return null
    }

    /**
     * Очищает базу данных от всех записей о погоде.
     */
    private suspend fun clearDatabase() {
        weatherDao.deleteAllDailyWeather()
        weatherDao.deleteAllHourlyWeather()
    }

    /**
     * Универсальный метод для получения данных о погоде из базы данных или API.
     * Реализует стратегию кэширования: сначала проверяет базу данных, затем API, если данные отсутствуют или устарели.
     * @param city Название города для запроса.
     * @param forceRefresh Если true, игнорирует кэш и запрашивает данные из API.
     * @param fromDb Лямбда для получения данных из базы данных.
     * @param fromApi Лямбда для получения данных из API.
     * @param mapApiToResult Лямбда для преобразования ответа API в нужный тип результата.
     * @return Resource<T> с результатом (Success, Error, Internet).
     */
    private suspend fun <T> fetchWeather(
        city: String,
        forceRefresh: Boolean,
        fromDb: suspend () -> T?,
        fromApi: suspend (String) -> Resource<WeatherApiResponse>,
        mapApiToResult: suspend (WeatherApiResponse) -> T
    ): Resource<T> where T : Any {
        return withContext(Dispatchers.IO) {
            // Проверяем наличие данных в базе
            val hasData = weatherDao.getDailyWeatherCount() > 0
            // Получаем данные из базы
            val dbData = fromDb()

            // Если данных нет, они null или требуется принудительное обновление
            if (!hasData || dbData == null || forceRefresh) {
                // Проверяем наличие интернета
                if (!isNetworkAvailable()) {
                    // Если интернета нет, возвращаем кэшированные данные (если есть) с флагом isStale
                    return@withContext if (dbData != null) {
                        Resource.Success(dbData, isStale = true)
                    } else {
                        Resource.Internet()
                    }
                }
                // Делаем запрос к API
                val apiResult = fromApi(city)
                if (apiResult is Resource.Success) {
                    // Если запрос успешен, сохраняем данные в базу и преобразуем в нужный тип
                    apiResult.data?.let { apiData ->
                        insertWeatherData(apiData)
                        val mappedResult = mapApiToResult(apiData)
                        Resource.Success(mappedResult)
                    } ?: Resource.Error("No data from API", "")
                } else {
                    // Если запрос не удался, возвращаем ошибку
                    apiResult
                }
            } else {
                // Проверяем свежесть данных
                val currentTime = System.currentTimeMillis()
                val lastUpdateTime = weatherDao.getLastUpdateTime()
                val isDataFresh = lastUpdateTime != null && currentTime <= lastUpdateTime + AppConstants.FORECAST_UPDATE_INTERVAL

                if (isDataFresh) {
                    // Если данные свежие, возвращаем их
                    Resource.Success(dbData)
                } else if (!isNetworkAvailable()) {
                    // Если данные устарели и интернета нет, возвращаем их с флагом isStale
                    Resource.Success(dbData, isStale = true)
                } else {
                    // Если данные устарели и интернет есть, делаем запрос к API
                    val apiResult = fromApi(city)
                    if (apiResult is Resource.Success) {
                        // Очищаем базу перед сохранением новых данных
                        clearDatabase()
                        apiResult.data?.let { apiData ->
                            insertWeatherData(apiData)
                            val mappedResult = mapApiToResult(apiData)
                            Resource.Success(mappedResult)
                        } ?: Resource.Error("No data from API", "")
                    } else {
                        // Если запрос не удался, возвращаем устаревшие данные
                        Resource.Success(dbData, isStale = true)
                    }
                }
            }
        } as Resource<T>
    }

    /**
     * Получает текущую погоду для указанного города.
     * @param city Название города.
     * @param forceRefresh Если true, игнорирует кэш.
     * @return Resource<WeatherResponse> с текущей погодой.
     */
    override suspend fun getCurrentWeather(city: String, forceRefresh: Boolean): Resource<WeatherResponse> {
        // If a specific city is passed, use it directly
        val targetCity = if (city.isNotBlank()) {
            city
        } else {
            // If no city is passed, get from WeatherUtils
            val cityFromUtils = WeatherUtils.getCityName(contextProvider.provideContext())
            if (cityFromUtils.isBlank()) {
            return Resource.Error(null, "City name is not set")
        }
            cityFromUtils
        }

        return fetchWeather(
            city = targetCity,
            forceRefresh = forceRefresh,
            fromDb = { getCurrentWeatherFromDB() },
            fromApi = { cityName ->
                withContext(Dispatchers.IO) {
                    try {
                        val response = apiService.getWeather(
                            location = cityName,
                            apiKey = BuildConfig.API_KEY,
                            include = "days,hours",
                            lang = devLocaleLanguage
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
            },
            mapApiToResult = { apiResponse ->
                val dailyWeather = WeatherMapper.toDailyWeather(apiResponse.days.first())
                WeatherResponseMapper.toWeatherResponse(dailyWeather, targetCity)
            }
        )
    }

    /**
     * Получает прогноз погоды для указанного города.
     * @param city Название города.
     * @param forceRefresh Если true, игнорирует кэш.
     * @return Resource<ForecastResponse> с прогнозом.
     */
    override suspend fun getForecastWeather(city: String, forceRefresh: Boolean): Resource<ForecastResponse> {
        // Если передан конкретный город, используем его напрямую
        val targetCity = if (city.isNotBlank()) {
            city
        } else {
            // Если город не передан, проверяем установленный город
            val cityFromUtils = WeatherUtils.getCityName(contextProvider.provideContext())
            if (cityFromUtils.isBlank()) {
            return Resource.Error(null, "City name is not set")
        }
            cityFromUtils
        }

        return fetchWeather(
            city = targetCity,
            forceRefresh = forceRefresh,
            fromDb = { getForecastWeatherFromDB() },
            fromApi = { cityName ->
                withContext(Dispatchers.IO) {
                    try {
                        val response = apiService.getWeather(
                            location = cityName,
                            apiKey = BuildConfig.API_KEY,
                            include = "days,hours",
                            lang = devLocaleLanguage
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
            },
            mapApiToResult = { apiResponse ->
                val dailyWeathers = apiResponse.days.map { WeatherMapper.toDailyWeather(it) }
                WeatherResponseMapper.toForecastResponse(dailyWeathers)
            }
        )
    }

    /**
     * Синхронизирует данные о погоде, очищая базу и загружая новые данные из API.
     * @param city Название города.
     */
    override suspend fun syncWeather(city: String) {
        clearDatabase()
        val response = apiService.getWeather(
            location = city,
            apiKey = BuildConfig.API_KEY,
            include = "days,hours",
            lang = devLocaleLanguage
        )
        insertWeatherData(response)
    }
}