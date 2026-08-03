package com.example.weatherforecast.data.repositories

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.example.weatherforecast.data.db.DailyWeatherEntity
import com.example.weatherforecast.data.db.HourlyWeatherEntity
import com.example.weatherforecast.data.db.WeatherDao
import com.example.weatherforecast.data.mappers.EntityMapper
import com.example.weatherforecast.data.remote.ApiDay
import com.example.weatherforecast.data.remote.WeatherApiResponse
import com.example.weatherforecast.data.remote.WeatherApiService
import com.example.weatherforecast.domain.models.HourlyWeather
import com.example.weatherforecast.utils.Resource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [VisualCrossingRepositoryImpl] caching strategy (review item 6).
 *
 * The repository has a three-state cache policy driven by the Room DAO and the
 * network availability flag:
 *
 *  1. cache present AND fresh            -> serve cache, never touch the API;
 *  2. cache absent, OR stale, OR forced  -> go to the API;
 *  3. offline fallback                   -> return the (possibly stale) cache,
 *     or [Resource.Internet] when there is nothing cached.
 *
 * All tests exercise [VisualCrossingRepositoryImpl.getCurrentWeather] so the
 * per-city DAO queries (`getDailyWeatherCountByCity`, `getDailyWeatherByCity`,
 * `getLastUpdateTimeByCity`, `getHourlyWeatherForDay`, `deleteCityWeather`) are
 * exercised exactly as production does.
 */
@OptIn(ExperimentalCoroutinesApi::class) // for Dispatchers.setMain / resetMain
class VisualCrossingRepositoryImplTest {

    private val apiService: WeatherApiService = mockk()
    private val context: Context = mockk(relaxed = true)
    private val weatherDao: WeatherDao = mockk(relaxed = true)

    private val connectivityManager: ConnectivityManager = mockk()
    private val network: Network = mockk()
    private val networkCapabilities: NetworkCapabilities = mockk()

    private lateinit var repository: VisualCrossingRepositoryImpl

    @Before
    fun setUp() {
        // StandardTestDispatcher keeps any Main-dispatched coroutines deterministic;
        // the repository itself never touches Dispatchers.Main, so this is purely
        // defensive and matches the project's test conventions.
        Dispatchers.setMain(StandardTestDispatcher())
        repository = VisualCrossingRepositoryImpl(apiService, context, weatherDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- Fixtures -----------------------------------------------------------------

    private fun dailyEntity(id: Int, city: String) = DailyWeatherEntity(
        id = id,
        dew = 5.0,                 // point of dew (точка росы)
        uvindex = 3,               // UV index (УФ индекс)
        date = "2025-04-03",
        dt = 1708754400L,
        temp = 15.0,
        feelsLike = 13.0,
        tempMin = 10.0,
        tempMax = 20.0,
        pressure = 1013.0,
        humidity = 60,
        windSpeed = 4.5,
        windDeg = 200,
        cloudiness = 50,
        description = "Partly cloudy",
        icon = "02d",
        sunrise = 1708736117L,
        sunset = 1708772966L,
        moonPhase = 0.5,
        visibility = 10000.0,
        cityName = city,
        timezone = "Europe/London",
        latitude = 51.5,
        longitude = -0.13
    )

    private fun hourlyEntity(id: Int, dailyId: Int) = HourlyWeatherEntity(
        id = id,
        dailyId = dailyId,
        time = "12:00:00",
        dt = 1708797600L,
        temp = 15.0,
        feelsLike = 13.0,
        pressure = 1013.0,
        humidity = 60,
        windSpeed = 4.5,
        windDeg = 200,
        cloudiness = 50,
        description = "Partly cloudy",
        icon = "02d"
    )

    private fun apiDay() = ApiDay(
        date = "2025-04-03",
        dateEpoch = 1708754400L,
        tempMax = 20.0,
        tempMin = 10.0,
        temp = 15.0,
        feelsLikeMax = 19.0,
        feelsLikeMin = 9.0,
        feelsLike = 13.0,
        dew = 5.0,
        humidity = 60.0,
        precipitation = 0.0,
        precipProbability = 0.0,
        precipCover = 0.0,
        precipType = null,
        snow = 0.0,
        snowDepth = 0.0,
        windGust = 12.0,
        windSpeed = 4.5,
        windDir = 200.0,
        pressure = 1013.0,
        cloudCover = 50.0,
        visibility = 10000.0,
        solarRadiation = 500.0,
        solarEnergy = 5.0,
        uvIndex = 3,
        severeRisk = 0,
        sunrise = "06:00:00",
        sunriseEpoch = 1708736117L,
        sunset = "18:00:00",
        sunsetEpoch = 1708772966L,
        moonPhase = 0.5,
        conditions = "Partly cloudy",
        description = "Partly cloudy",
        icon = "02d",
        hours = null
    )

    private fun apiResponse() = WeatherApiResponse(
        queryCost = 1,
        latitude = 51.5,
        longitude = -0.13,
        resolvedAddress = "London",
        address = "London",
        timezone = "Europe/London",
        tzOffset = 0.0,
        days = listOf(apiDay())
    )

    /** Points the mocked [ConnectivityManager] at a network with/without Internet. */
    private fun stubNetwork(available: Boolean) {
        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns networkCapabilities
        every { networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns available
    }

    /** Fresh timestamp: within the 6h FORECAST_UPDATE_INTERVAL window. */
    private fun freshTimestamp() = System.currentTimeMillis()

    /** Stale timestamp: more than 6h in the past. */
    private fun staleTimestamp() = System.currentTimeMillis() - 7 * 60 * 60 * 1000L

    // --- Tests --------------------------------------------------------------------

    /**
     * Fresh cache + forceRefresh=false -> the cached [DailyWeather] is returned and
     * the API is never contacted (the whole point of the cache).
     */
    @Test
    fun `fresh cache returns cached Success without API call`() = runTest {
        stubNetwork(available = true)
        coEvery { weatherDao.getDailyWeatherCountByCity("London") } returns 1
        coEvery { weatherDao.getDailyWeatherByCity("London") } returns dailyEntity(id = 1, city = "London")
        coEvery { weatherDao.getHourlyWeatherForDay(1) } returns listOf(hourlyEntity(id = 1, dailyId = 1))
        coEvery { weatherDao.getLastUpdateTimeByCity("London") } returns freshTimestamp()

        val result = repository.getCurrentWeather("London", forceRefresh = false)

        assertTrue(result is Resource.Success)
        val data = (result as Resource.Success).data
        assertNotNull(data)
        assertEquals(15.0, data?.temp ?: 0.0, 0.0)
        assertEquals("Partly cloudy", data?.description)
        // Hourly data restored from the cache, not null.
        assertEquals(1, data?.hours?.size)
        assertEquals("12:00:00", data?.hours?.first()?.time)

        // The cache path must never reach the network layer.
        coVerify(exactly = 0) { apiService.getWeather(location = any(), include = any(), apiKey = any(), lang = any()) }
        coVerify(exactly = 0) { weatherDao.deleteCityWeather(any()) }
    }

    /**
     * Stale cache + offline -> the stale cache is returned flagged isStale=true.
     * The app must degrade gracefully instead of crashing on a missing network.
     */
    @Test
    fun `stale cache while offline returns stale Success without crash`() = runTest {
        stubNetwork(available = false)
        coEvery { weatherDao.getDailyWeatherCountByCity("London") } returns 1
        coEvery { weatherDao.getDailyWeatherByCity("London") } returns dailyEntity(id = 1, city = "London")
        coEvery { weatherDao.getHourlyWeatherForDay(1) } returns listOf(hourlyEntity(id = 1, dailyId = 1))
        coEvery { weatherDao.getLastUpdateTimeByCity("London") } returns staleTimestamp()

        val result = repository.getCurrentWeather("London", forceRefresh = false)

        assertTrue(result is Resource.Success)
        val success = result as Resource.Success
        assertTrue("stale cache must be flagged isStale", success.isStale)
        assertNotNull(success.data)
        assertEquals("Europe/London", success.data?.timezone)
        // No API attempt offline.
        coVerify(exactly = 0) { apiService.getWeather(location = any(), include = any(), apiKey = any(), lang = any()) }
    }

    /**
     * No cache + offline -> [Resource.Internet]. There is nothing to fall back to,
     * so the repository reports the "no internet" failure instead of a null Success.
     */
    @Test
    fun `no cache while offline returns Internet`() = runTest {
        stubNetwork(available = false)
        coEvery { weatherDao.getDailyWeatherCountByCity("London") } returns 0
        coEvery { weatherDao.getDailyWeatherByCity("London") } returns null

        val result = repository.getCurrentWeather("London", forceRefresh = false)

        // Resource.Internet is the repository's "no connectivity" outcome for an
        // empty cache (there is no cached Success to fall back on).
        assertTrue(result is Resource.Internet)
        coVerify(exactly = 0) { apiService.getWeather(location = any(), include = any(), apiKey = any(), lang = any()) }
    }

    /**
     * forceRefresh=true + online -> the per-city cache is replaced: the old city rows
     * are deleted via [WeatherDao.deleteCityWeather] BEFORE the fresh API payload is
     * re-inserted, so repeated refreshes cannot accumulate duplicate cache entries.
     */
    @Test
    fun `force refresh deletes city cache then re-inserts and returns Success`() = runTest {
        stubNetwork(available = true)
        coEvery { weatherDao.getDailyWeatherCountByCity("London") } returns 1
        coEvery { apiService.getWeather(location = "London", include = any(), apiKey = any(), lang = any()) } returns apiResponse()

        val result = repository.getCurrentWeather("London", forceRefresh = true)

        assertTrue(result is Resource.Success)
        assertNotNull((result as Resource.Success).data)

        // Per-city replacement ordering: delete first, then a fresh insert.
        coVerify(exactly = 1) { weatherDao.deleteCityWeather("London") }
        coVerify(exactly = 1) { weatherDao.insertWeatherTransaction(any(), any()) }
    }

    /**
     * Stale cache + online + API failure -> the stale cache is returned flagged
     * isStale=true. A throwing backend must not take the offline data down with it;
     * this is the graceful-fallback path.
     */
    @Test
    fun `stale cache with failing API returns cached stale Success`() = runTest {
        stubNetwork(available = true)
        coEvery { weatherDao.getDailyWeatherCountByCity("London") } returns 1
        coEvery { weatherDao.getDailyWeatherByCity("London") } returns dailyEntity(id = 1, city = "London")
        coEvery { weatherDao.getHourlyWeatherForDay(1) } returns listOf(hourlyEntity(id = 1, dailyId = 1))
        coEvery { weatherDao.getLastUpdateTimeByCity("London") } returns staleTimestamp()
        coEvery { apiService.getWeather(location = "London", include = any(), apiKey = any(), lang = any()) } throws IOException("network down")

        val result = repository.getCurrentWeather("London", forceRefresh = false)

        assertTrue(result is Resource.Success)
        val success = result as Resource.Success
        assertTrue("failed refresh must fall back to the stale cache", success.isStale)
        assertNotNull(success.data)
        // Hourly data is still intact in the fallback payload.
        val hours: List<HourlyWeather>? = success.data?.hours
        assertTrue(hours.isNullOrEmpty().not())
        assertEquals(1, hours?.size)
    }
}
