import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.example.weatherforecast.data.db.DailyWeatherEntity
import com.example.weatherforecast.data.db.WeatherDao
import com.example.weatherforecast.data.mappers.EntityMapper
import com.example.weatherforecast.data.mappers.WeatherResponseMapper
import com.example.weatherforecast.data.remote.WeatherApiResponse
import com.example.weatherforecast.data.remote.WeatherApiService
import com.example.weatherforecast.data.repositories.VisualCrossingRepositoryImpl
import com.example.weatherforecast.di.ContextProvider
import com.example.weatherforecast.utils.Resource
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals


class VisualCrossingRepositoryImplTest {

    private lateinit var repository: VisualCrossingRepositoryImpl
    private lateinit var mockApiService: WeatherApiService
    private lateinit var mockContextProvider: ContextProvider
    private lateinit var mockWeatherDao: WeatherDao
    private lateinit var dailyEntity: DailyWeatherEntity

    @BeforeEach
    fun setUp() {
        mockApiService = mockk()
        mockContextProvider = mockk()
        mockWeatherDao = mockk()

        // Set up mock context for network availability
        val mockContext = mockk<Context>()
        val mockConnectivityManager = mockk<ConnectivityManager>()
        val mockNetwork = mockk<Network>()
        val mockNetworkCapabilities = mockk<NetworkCapabilities>()

        every { mockContext.getSystemService(Context.CONNECTIVITY_SERVICE) } returns mockConnectivityManager
        every { mockConnectivityManager.activeNetwork } returns mockNetwork
        every { mockConnectivityManager.getNetworkCapabilities(mockNetwork) } returns mockNetworkCapabilities
        every { mockNetworkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns true

        every { mockContextProvider.provideContext() } returns mockContext

        dailyEntity = DailyWeatherEntity(
            id = 1, dew = 10.0, uvindex = 5, date = "2025-07-14", dt = System.currentTimeMillis(),
            temp = 25.0, feelsLike = 26.0, tempMin = 20.0, tempMax = 30.0, pressure = 1013.0,
            humidity = 60, windSpeed = 5.0, windDeg = 180, cloudiness = 20, description = "clear sky",
            icon = "01d", sunrise = 1626219600000, sunset = 1626258800000, moonPhase = 0.5, visibility = 10.0
        )

        coEvery { mockWeatherDao.getCurrentDailyWeatherEntity() } returns dailyEntity
        coEvery { mockWeatherDao.getLastUpdateTime() } returns dailyEntity.dt
        coEvery { mockWeatherDao.getDailyWeatherCount() } returns 1

        repository = VisualCrossingRepositoryImpl(mockApiService, mockContextProvider, mockWeatherDao)
    }

    @Test
    fun `getCurrentWeather returns DB data when fresh`() = runBlocking {
        val result = repository.getCurrentWeather()
        val expectedWeatherResponse = WeatherResponseMapper.toWeatherResponse(EntityMapper.toDailyWeather(dailyEntity), "TestCity")
        assertEquals(Resource.Success(expectedWeatherResponse), result)
        coVerify(exactly = 0) { mockApiService.getWeather("Hamburg", "metric", "days,hours", "FS67QRY9G8HVLCANZJM6QBJJC", "json", "de") }
    }

    // Test for getCurrentWeather() - Empty DB, Network Available, API Succeeds
    @Test
    fun `getCurrentWeather fetches from API when DB is empty and network available`() = runBlocking {
        coEvery { mockWeatherDao.getDailyWeatherCount() } returns 0
        coEvery { mockWeatherDao.getCurrentDailyWeatherEntity() } returnsMany listOf(null, dailyEntity)
        val mockWeatherApiResponse = mockk<WeatherApiResponse>()
        coEvery { mockApiService.getWeather("Hamburg", "metric", "days,hours", "FS67QRY9G8HVLCANZJM6QBJJC", "json", "de") } returns mockWeatherApiResponse

        val result = repository.getCurrentWeather()
        val expectedWeatherResponse = WeatherResponseMapper.toWeatherResponse(EntityMapper.toDailyWeather(dailyEntity), "TestCity")
        assertEquals(Resource.Success(expectedWeatherResponse), result)
        coVerify(exactly = 1) { mockApiService.getWeather("Hamburg", "metric", "days,hours", "FS67QRY9G8HVLCANZJM6QBJJC", "json", "de") }
    }

    // Test for getForecastWeather() - Fresh Data
    @Test
    fun `getForecastWeather returns DB data when fresh`() = runBlocking {
        val dailyEntities = listOf(
            dailyEntity,
            DailyWeatherEntity(id = 2, dew = 11.0, uvindex = 6, date = "2025-07-15", dt = System.currentTimeMillis(),
                temp = 26.0, feelsLike = 27.0, tempMin = 21.0, tempMax = 31.0, pressure = 1014.0,
                humidity = 61, windSpeed = 5.5, windDeg = 180, cloudiness = 22, description = "scattered clouds",
                icon = "03d", sunrise = 1626306000000, sunset = 1626345200000, moonPhase = 0.6, visibility = 10.5)
        )
        coEvery { mockWeatherDao.getAllDailyWeatherSync() } returns dailyEntities
        coEvery { mockWeatherDao.getLastUpdateTime() } returns System.currentTimeMillis()
        coEvery { mockWeatherDao.getDailyWeatherCount() } returns dailyEntities.size

        val result = repository.getForecastWeather()
        val expectedForecastResponse = WeatherResponseMapper.toForecastResponse(dailyEntities.map { EntityMapper.toDailyWeather(it) })
        assertEquals(Resource.Success(expectedForecastResponse), result)
        coVerify(exactly = 0) {mockApiService.getWeather("Hamburg", "metric", "days,hours", "FS67QRY9G8HVLCANZJM6QBJJC", "json", "de") }
    }
}

// Helper: Define expectedDailyEntity for tests
private val expectedDailyEntity = DailyWeatherEntity(
    id = 1, dew = 10.5, uvindex = 6, date = "2025-07-14", dt = System.currentTimeMillis(),
    temp = 26.0, feelsLike = 27.0, tempMin = 21.0, tempMax = 31.0, pressure = 1014.0,
    humidity = 61, windSpeed = 5.5, windDeg = 180, cloudiness = 22, description = "scattered clouds",
    icon = "03d", sunrise = 1626219600000, sunset = 1626258800000, moonPhase = 0.6, visibility = 10.5
)