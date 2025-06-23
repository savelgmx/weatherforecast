package com.example.weatherforecast

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.room.Room
import com.example.weatherforecast.data.db.DailyWeatherEntity
import com.example.weatherforecast.data.db.OpenWeatherMapDatabase
import com.example.weatherforecast.data.db.WeatherDao
import com.example.weatherforecast.data.remote.ApiDay
import com.example.weatherforecast.data.remote.ApiHour
import com.example.weatherforecast.data.remote.WeatherApiResponse
import com.example.weatherforecast.data.remote.WeatherApiService
import com.example.weatherforecast.data.repositories.VisualCrossingRepositoryImpl
import com.example.weatherforecast.di.ContextProvider
import com.example.weatherforecast.utils.AppConstants
import com.example.weatherforecast.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@Config(manifest = Config.NONE)
class VisualCrossingRepositoryImplTest {

    private lateinit var repository: VisualCrossingRepositoryImpl
    private lateinit var apiService: WeatherApiService
    private lateinit var weatherDao: WeatherDao
    private lateinit var contextProvider: ContextProvider
    private lateinit var context: Context
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var db: OpenWeatherMapDatabase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        // Настройка тестового окружения
        Dispatchers.setMain(testDispatcher)
        val mockContext = Mockito.mock(Context::class.java)
        contextProvider = Mockito.mock(ContextProvider::class.java)
        Mockito.`when`(contextProvider.provideContext()).thenReturn(mockContext)
        connectivityManager = Mockito.mock(ConnectivityManager::class.java)
        Mockito.`when`(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager)
        db = Room.inMemoryDatabaseBuilder(mockContext, OpenWeatherMapDatabase::class.java)
            .allowMainThreadQueries()
            .setTransactionExecutor { it.run() } // Ensure single-threaded execution
            .build()
        weatherDao = db.weatherDao()

        // Инициализация apiService
        apiService = Mockito.mock(WeatherApiService::class.java)

        // Инициализация репозитория с мок-объектами и фиктивными данными
        repository = VisualCrossingRepositoryImpl(apiService, contextProvider, weatherDao)
        // Исправленная установка значений через рефлексию
        repository.javaClass.getDeclaredField("cityName").apply {
            isAccessible = true
            set(repository, "Krasnoyarsk") // Первый аргумент - экземпляр, второй - значение
        }
        repository.javaClass.getDeclaredField("devLocaleLanguage").apply {
            isAccessible = true
            set(repository, "en")
        }
        repository.javaClass.getDeclaredField("latitude").apply {
            isAccessible = true
            set(repository, "56.01")
        }
        repository.javaClass.getDeclaredField("longitude").apply {
            isAccessible = true
            set(repository, "92.87")
        }
    }

    @After
    fun tearDown() {
        db.close()
        Dispatchers.resetMain()
    }

    private fun mockNetworkAvailability(isAvailable: Boolean) {
        val network = Mockito.mock(Network::class.java)
        val capabilities = Mockito.mock(NetworkCapabilities::class.java)
        Mockito.`when`(connectivityManager.activeNetwork).thenReturn(if (isAvailable) network else null)
        Mockito.`when`(connectivityManager.getNetworkCapabilities(network)).thenReturn(if (isAvailable) capabilities else null)
        Mockito.`when`(capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(isAvailable)
    }

    private fun createMockWeatherApiResponse(): WeatherApiResponse {
        val apiHour = ApiHour(
            time = "12:00",
            timeEpoch = System.currentTimeMillis() / 1000,
            temp = 25.0,
            feelsLike = 26.0,
            pressure = 1013.0,
            humidity = 60.0,
            windSpeed = 5.0,
            windDir = 180.0,
            cloudCover = 20.0,
            conditions = "Clear",
            icon = "01d",
            dew = 10.0,
            precipProbability = 0.0,
            precipType = null,
            precipitation = 0.0,
            severeRisk = 0,
            snow = 0.0,
            snowDepth = 0.0,
            solarEnergy = 0.0,
            solarRadiation = 0.0,
            uvIndex = 0,
            visibility = 10.0,
            windGust = 0.0
        )
        val apiDay = ApiDay(
            date = "2025-06-19",
            dateEpoch = System.currentTimeMillis() / 1000,
            temp = 25.0,
            feelsLike = 26.0,
            tempMin = 20.0,
            tempMax = 30.0,
            pressure = 1013.0,
            humidity = 60.0,
            windSpeed = 5.0,
            windDir = 180.0,
            cloudCover = 20.0,
            conditions = "Clear",
            icon = "01d",
            sunrise = "1623999600",
            sunset = "1624053600",
            moonPhase = 0.5,
            hours = listOf(apiHour),
            description = "Clear sky",
            dew = 10.0,
            feelsLikeMax = 27.0,
            feelsLikeMin = 24.0,
            precipCover = 0.0,
            precipProbability = 0.0,
            precipType = null,
            precipitation = 0.0,
            severeRisk = 0,
            snow = 0.0,
            snowDepth = 0.0,
            solarEnergy = 0.0,
            solarRadiation = 0.0,
            uvIndex = 0,
            visibility = 10.0,
            windGust = 0.0,
            sunriseEpoch = 1623999600,
            sunsetEpoch = 1624053600
        )
        return WeatherApiResponse(
            address = "Krasnoyarsk",
            resolvedAddress = "Krasnoyarsk",
            latitude = 0.0,
            longitude = 0.0,
            queryCost = 0,
            timezone = "UTC",
            tzOffset = 0.0,
            days = listOf(apiDay)
        )
    }

    @Test
    fun `getCurrentWeather with fresh data in DB returns data from DB`() = runTest {
        // Given: Свежие данные в базе
        val entity = DailyWeatherEntity(
            date = "2025-06-19",
            dt = System.currentTimeMillis() / 1000,
            temp = 25.0,
            feelsLike = 26.0,
            tempMin = 20.0,
            tempMax = 30.0,
            pressure = 1013.0,
            humidity = 60,
            windSpeed = 5.0,
            windDeg = 180,
            cloudiness = 20,
            description = "Clear",
            icon = "01d",
            sunrise = 1623999600,
            sunset = 1624053600,
            moonPhase = 0.5
        )
        weatherDao.insertDailyWeather(entity)
        mockNetworkAvailability(true)

        // When: Вызываем метод
        val result = repository.getCurrentWeather()

        // Then: Данные возвращаются из базы, API не вызывается
        assertTrue(result is Resource.Success)
        assertEquals("Krasnoyarsk", result.data?.name)
        assertEquals(25.0, result.data?.main?.temp)
        Mockito.verify(apiService, Mockito.never()).getWeather(eq("Krasnoyarsk"),
            eq("metric"), eq("days,hours"),eq("FS67QRY9G8HVLCANZJM6QBJJC"), eq("json"), eq("en"))
    }

    @Test
    fun `getCurrentWeather with empty DB and internet fetches from API`() = runTest {
        // Given: Пустая база, интернет доступен
        mockNetworkAvailability(true)
        val apiResponse = createMockWeatherApiResponse()
        val apiKey = "FS67QRY9G8HVLCANZJM6QBJJC" // Явное значение API ключа
        Mockito.`when`(apiService.getWeather(
            eq("Krasnoyarsk"),
            eq("metric"),
            eq("days,hours"),
            eq(apiKey),
            eq("json"),
            eq("en")
        )).thenReturn(apiResponse) // Используем when...thenReturn

        // When: Вызываем метод
        val result = repository.getCurrentWeather()

        // Then: Данные запрашиваются из API и сохраняются в базу
        assertTrue(result is Resource.Success)
        assertEquals("Krasnoyarsk", result.data?.name)
        assertEquals(25.0, result.data?.main?.temp)
        assertTrue(weatherDao.getDailyWeatherCount() > 0)
        Mockito.verify(apiService).getWeather(eq("Krasnoyarsk"),
            eq("metric"), eq("days,hours"),eq(BuildConfig.API_KEY), eq("json"), eq("en"))
    }

    @Test
    fun `getCurrentWeather with stale data and internet updates from API`() = runTest {
        // Given: Устаревшие данные в базе, интернет доступен
        val entity = DailyWeatherEntity(
            date = "2025-06-19",
            dt = (System.currentTimeMillis() / 1000) - (AppConstants.CURRENT_WEATHER_UPDATE_INTERVAL / 1000) - 3600,
            temp = 20.0,
            feelsLike = 21.0,
            tempMin = 15.0,
            tempMax = 25.0,
            pressure = 1010.0,
            humidity = 70,
            windSpeed = 4.0,
            windDeg = 170,
            cloudiness = 30,
            description = "Cloudy",
            icon = "02d",
            sunrise = 1623999600,
            sunset = 1624053600,
            moonPhase = 0.5
        )
        weatherDao.insertDailyWeather(entity)
        mockNetworkAvailability(true)
        val apiResponse = createMockWeatherApiResponse()
        Mockito.doReturn(apiResponse).`when`(apiService).getWeather(eq("Krasnoyarsk"),
            eq("metric"), eq("days,hours"),eq(BuildConfig.API_KEY), eq("json"), eq("en"))

        // When: Вызываем метод
        val result = repository.getCurrentWeather()

        // Then: Данные обновляются из API
        assertTrue(result is Resource.Success)
        assertEquals("Krasnoyarsk", result.data?.name)
        assertEquals(25.0, result.data?.main?.temp)
        Mockito.verify(apiService).getWeather(eq("Krasnoyarsk"),
            eq("metric"), eq("days,hours"),eq(BuildConfig.API_KEY), eq("json"), eq("en"))
    }

    @Test
    fun `getCurrentWeather with stale data and no internet returns stale data`() = runTest {
        // Given: Устаревшие данные в базе, интернета нет
        val entity = DailyWeatherEntity(
            date = "2025-06-19",
            dt = (System.currentTimeMillis() / 1000) - (AppConstants.CURRENT_WEATHER_UPDATE_INTERVAL / 1000) - 3600,
            temp = 20.0,
            feelsLike = 21.0,
            tempMin = 15.0,
            tempMax = 25.0,
            pressure = 1010.0,
            humidity = 70,
            windSpeed = 4.0,
            windDeg = 170,
            cloudiness = 30,
            description = "Cloudy",
            icon = "02d",
            sunrise = 1623999600,
            sunset = 1624053600,
            moonPhase = 0.5
        )
        weatherDao.insertDailyWeather(entity)
        mockNetworkAvailability(false)

        // When: Вызываем метод
        val result = repository.getCurrentWeather()

        // Then: Возвращаются устаревшие данные
        assertTrue(result is Resource.Success)
        assertEquals("Krasnoyarsk", result.data?.name)
        assertEquals(20.0, result.data?.main?.temp)
        Mockito.verify(apiService, Mockito.never()).getWeather(eq("Krasnoyarsk"),
            eq("metric"), eq("days,hours"),eq(BuildConfig.API_KEY), eq("json"), eq("en"))
    }

    @Test
    fun `getCurrentWeather with empty DB and no internet returns Internet error`() = runTest {
        // Given: Пустая база, интернета нет
        mockNetworkAvailability(false)

        // When: Вызываем метод
        val result = repository.getCurrentWeather()

        // Then: Возвращается Resource.Internet
        assertTrue(result is Resource.Internet)
        Mockito.verify(apiService, Mockito.never()).getWeather(eq("Krasnoyarsk"),
            eq("metric"), eq("days,hours"),eq(BuildConfig.API_KEY), eq("json"), eq("en"))
    }

    @Test
    fun `getForecastWeather with fresh data in DB returns data from DB`() = runTest {
        // Given: Свежие данные в базе
        val entity = DailyWeatherEntity(
            date = "2025-06-19",
            dt = System.currentTimeMillis() / 1000,
            temp = 25.0,
            feelsLike = 26.0,
            tempMin = 20.0,
            tempMax = 30.0,
            pressure = 1013.0,
            humidity = 60,
            windSpeed = 5.0,
            windDeg = 180,
            cloudiness = 20,
            description = "Clear",
            icon = "01d",
            sunrise = 1623999600,
            sunset = 1624053600,
            moonPhase = 0.5
        )
        weatherDao.insertDailyWeather(entity)
        mockNetworkAvailability(true)

        // When: Вызываем метод
        val result = repository.getForecastWeather()

        // Then: Данные возвращаются из базы, API не вызывается
        assertTrue(result is Resource.Success)
        assertEquals(1, result.data?.daily?.size)
        assertEquals(25.0, result.data?.daily?.first()?.temp)
        Mockito.verify(apiService, Mockito.never()).getWeather(eq("Krasnoyarsk"),
            eq("metric"), eq("days,hours"),eq(BuildConfig.API_KEY), eq("json"), eq("en"))
    }

    @Test
    fun `getForecastWeather with empty DB and internet fetches from API`() = runTest {
        // Given: Пустая база, интернет доступен
        mockNetworkAvailability(true)
        val apiResponse = createMockWeatherApiResponse()
        Mockito.doReturn(apiResponse).`when`(apiService).getWeather(eq("Krasnoyarsk"),
            eq("metric"), eq("days,hours"),eq(BuildConfig.API_KEY), eq("json"), eq("en"))

        // When: Вызываем метод
        val result = repository.getForecastWeather()

        // Then: Данные запрашиваются из API и сохраняются в базу
        assertTrue(result is Resource.Success)
        assertEquals(1, result.data?.daily?.size)
        assertEquals(25.0, result.data?.daily?.first()?.temp)
        assertTrue(weatherDao.getDailyWeatherCount() > 0)
        Mockito.verify(apiService).getWeather(eq("Krasnoyarsk"),
            eq("metric"), eq("days,hours"),eq(BuildConfig.API_KEY), eq("json"), eq("en"))
    }

    @Test
    fun `getForecastWeather with stale data and internet updates from API`() = runTest {
        // Given: Устаревшие данные в базе, интернет доступен
        val entity = DailyWeatherEntity(
            date = "2025-06-19",
            dt = (System.currentTimeMillis() / 1000) - (AppConstants.CURRENT_WEATHER_UPDATE_INTERVAL / 1000) - 3600,
            temp = 20.0,
            feelsLike = 21.0,
            tempMin = 15.0,
            tempMax = 25.0,
            pressure = 1010.0,
            humidity = 70,
            windSpeed = 4.0,
            windDeg = 170,
            cloudiness = 30,
            description = "Cloudy",
            icon = "02d",
            sunrise = 1623999600,
            sunset = 1624053600,
            moonPhase = 0.5
        )
        weatherDao.insertDailyWeather(entity)
        mockNetworkAvailability(true)
        val apiResponse = createMockWeatherApiResponse()
        Mockito.doReturn(apiResponse).`when`(apiService).getWeather(eq("Krasnoyarsk"),
            eq("metric"), eq("days,hours"),eq(BuildConfig.API_KEY), eq("json"), eq("en"))

        // When: Вызываем метод
        val result = repository.getForecastWeather()

        // Then: Данные обновляются из API
        assertTrue(result is Resource.Success)
        assertEquals(1, result.data?.daily?.size)
        assertEquals(25.0, result.data?.daily?.first()?.temp)
        Mockito.verify(apiService).getWeather(eq("Krasnoyarsk"),
            eq("metric"), eq("days,hours"),eq(BuildConfig.API_KEY), eq("json"), eq("en"))
    }

    @Test
    fun `getForecastWeather with stale data and no internet returns stale data`() = runTest {
        // Given: Устаревшие данные в базе, интернета нет
        val entity = DailyWeatherEntity(
            date = "2025-06-19",
            dt = (System.currentTimeMillis() / 1000) - (AppConstants.CURRENT_WEATHER_UPDATE_INTERVAL / 1000) - 3600,
            temp = 20.0,
            feelsLike = 21.0,
            tempMin = 15.0,
            tempMax = 25.0,
            pressure = 1010.0,
            humidity = 70,
            windSpeed = 4.0,
            windDeg = 170,
            cloudiness = 30,
            description = "Cloudy",
            icon = "02d",
            sunrise = 1623999600,
            sunset = 1624053600,
            moonPhase = 0.5
        )
        weatherDao.insertDailyWeather(entity)
        mockNetworkAvailability(false)

        // When: Вызываем метод
        val result = repository.getForecastWeather()

        // Then: Возвращаются устаревшие данные
        assertTrue(result is Resource.Success)
        assertEquals(1, result.data?.daily?.size)
        assertEquals(20.0, result.data?.daily?.first()?.temp)
        Mockito.verify(apiService, Mockito.never()).getWeather(eq("Krasnoyarsk"),
            eq("metric"), eq("days,hours"),eq(BuildConfig.API_KEY), eq("json"), eq("en"))
    }

    @Test
    fun `getForecastWeather with empty DB and no internet returns Internet error`() = runTest {
        // Given: Пустая база, интернета нет
        mockNetworkAvailability(false)

        // When: Вызываем метод
        val result = repository.getForecastWeather()

        // Then: Возвращается Resource.Internet
        assertTrue(result is Resource.Internet)
        Mockito.verify(apiService, Mockito.never()).getWeather(eq("Krasnoyarsk"),
            eq("metric"), eq("days,hours"),eq(BuildConfig.API_KEY), eq("json"), eq("en"))
    }
}