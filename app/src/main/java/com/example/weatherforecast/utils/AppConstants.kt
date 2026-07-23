package com.example.weatherforecast.utils

object AppConstants {

    // API_KEY moved to BuildConfig.API_KEY (set in app/build.gradle via buildConfigField)
    const val WEATHER_API_IMAGE_ENDPOINT = "https://raw.githubusercontent.com/visualcrossing/WeatherIcons/refs/heads/main/PNG/1st%20Set%20-%20Color/"
    const val CITY_FORECAST="Krasnoyarsk"  //we set cityName constant and
    const val CITY_LAT="56.0097"  // latitude
    const val CITY_LON="92.7917" //langitude if we can't get no data about device location
    const val CURRENT_WEATHER_UPDATE_INTERVAL = 15 * 60 * 1000L // 15 минут 3600L // 1 час в секундах
    const val FORECAST_UPDATE_INTERVAL = 6 * 60 * 60 * 1000L // 6 часов

    // https://dashboard.iqair.com/personal/api-keys
    // Air quality API key expires Jul 21, 2026

}