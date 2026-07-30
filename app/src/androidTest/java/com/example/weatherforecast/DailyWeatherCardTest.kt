package com.example.weatherforecast

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import org.junit.Rule
import org.junit.Test
import com.example.weatherforecast.components.DailyWeatherCard
import com.example.weatherforecast.domain.models.DailyWeather

class DailyWeatherCardTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dailyWeatherCard_displaysCorrectData() {
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
            DailyWeatherCard(daily = daily)
        }
        composeTestRule.onNodeWithText("Clear").assertIsDisplayed()
    }
}
