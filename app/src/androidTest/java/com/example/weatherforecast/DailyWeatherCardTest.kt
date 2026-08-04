package com.example.weatherforecast

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import org.junit.Rule
import org.junit.Test
import com.example.weatherforecast.components.DailyWeatherCard
import com.example.weatherforecast.data.repositories.SettingsRepositoryImpl
import com.example.weatherforecast.domain.models.DailyWeather
import com.example.weatherforecast.presentation.viewmodels.SettingsViewModel

class DailyWeatherCardTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dailyWeatherCard_displaysCorrectData() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        val daily = DailyWeather(
            dew = 1.37,
            uvindex = 1,
            date = "2025-04-03",
            dt = 1708754400L,
            temp = 20.0,
            feelsLike = 15.0,
            tempMin = 16.0,
            tempMax = 25.0,
            pressure = 1036.0,
            visibility = 10000.0,
            humidity = 73,
            windSpeed = 2.2,
            windDeg = 224,
            cloudiness = 100,
            description = "Clear",
            icon = "04n",
            sunrise = 1708736117L,
            sunset = 1708772966L,
            moonPhase = 0.5,
            hours = null,
            timezone = "Asia/Krasnoyarsk",
            latitude = 56.0097,
            longitude = 92.79
        )

        composeTestRule.setContent {
            // Pass a real repository-backed SettingsViewModel explicitly instead of
            // relying on the hiltViewModel() default. An instrumented Compose test
            // hosts the UI in a plain ComponentActivity with no Hilt graph, so the
            // default would throw IllegalStateException from dagger.hilt.EntryPoints.
            // DataStore is reachable through the test ApplicationContext.
            DailyWeatherCard(
                daily = daily,
                settingsViewModel = SettingsViewModel(SettingsRepositoryImpl(context))
            )
        }
        composeTestRule.onNodeWithText("Clear").assertIsDisplayed()
    }
}
