package com.example.weatherforecast.utils

object AppConstants {

    const val API_KEY = "33cc710b4ef18155198d89c3b2033f56"

    const val WEATHER_API_IMAGE_ENDPOINT = "https://raw.githubusercontent.com/visualcrossing/WeatherIcons/refs/heads/main/PNG/1st%20Set%20-%20Color/"
    const val CITY_FORECAST="Krasnoyarsk"  //we set cityName constant and
    const val CITY_LAT="56.0097"  // latitude
    const val CITY_LON="92.7917" //langitude if we can't get no data about device location
    const val DATA_UPDATE_INTERVAL = 3600L // 1 час в секундах

    //https://dashboard.iqair.com/personal/api-keys
    //Air quality API key expires Sep 13, 2025
    const val IQAir_API_KEY="16e8af95-47d9-41a4-88ae-8d1a318252d4"
    //https://api.airvisual.com/v2/nearest_city?lat=56.0097&lon=92.7917&key=16e8af95-47d9-41a4-88ae-8d1a318252d4

}