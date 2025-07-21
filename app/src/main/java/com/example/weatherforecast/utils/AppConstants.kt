package com.example.weatherforecast.utils

object AppConstants {

    const val API_KEY = "33cc710b4ef18155198d89c3b2033f56"

    const val WEATHER_API_IMAGE_ENDPOINT = "https://raw.githubusercontent.com/visualcrossing/WeatherIcons/refs/heads/main/PNG/1st%20Set%20-%20Color/"
    const val CITY_FORECAST="Krasnoyarsk"  //we set cityName constant and
    const val CITY_LAT="56.0097"  // latitude
    const val CITY_LON="92.7917" //langitude if we can't get no data about device location
    const val CURRENT_WEATHER_UPDATE_INTERVAL = 15 * 60 * 1000L // 15 минут 3600L // 1 час в секундах
    const val FORECAST_UPDATE_INTERVAL = 6 * 60 * 60 * 1000L // 6 часов

    //https://dashboard.iqair.com/personal/api-keys
    //Air quality API key expires Jul 21, 2026
    const val IQAir_API_KEY="f3324376-d944-44f4-bda6-e936f76bbbeb"
    //https://api.airvisual.com/v2/nearest_city?lat=56.0097&lon=92.7917&key=f3324376-d944-44f4-bda6-e936f76bbbeb

}