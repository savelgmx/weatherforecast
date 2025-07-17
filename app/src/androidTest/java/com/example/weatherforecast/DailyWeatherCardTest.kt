package com.example.weatherforecast

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import org.junit.Rule
import org.junit.Test
import com.example.weatherforecast.components.DailyWeatherCard
import com.example.weatherforecast.response.Daily
import com.example.weatherforecast.response.Temp
import com.example.weatherforecast.response.FeelsLike
import com.example.weatherforecast.response.Weather

class DailyWeatherCardTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dailyWeatherCard_displaysCorrectData() {
        val daily =
            Daily(
                100, 18.32, 1708754400,
                FeelsLike(20.0, 20.0, 15.0, 15.0),
                73, 0.5, 1708862520, 1708824420, 1036,
                1708736117, 1708772966,
                Temp(25.0, 16.0, 25.0, 19.0, 20.0, 15.0),
                1, listOf(Weather(804, "Clear", "Clear", "04n")),
                224, 1.37, 2.40
            )


        composeTestRule.setContent {
            DailyWeatherCard(daily = daily)
        }
        composeTestRule.onNodeWithText("Day: 25").assertIsDisplayed()
        composeTestRule.onNodeWithText("Night: 16").assertIsDisplayed()
        composeTestRule.onNodeWithText("Feels like: 15").assertIsDisplayed()
        composeTestRule.onNodeWithText("Feels like: 20").assertIsDisplayed()
        composeTestRule.onNodeWithText("Clear").assertIsDisplayed()
    }
}