package com.example.weatherforecast.utils

object AppConstants {

    const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    const val API_KEY = "33cc710b4ef18155198d89c3b2033f56"
    const val WEATHER_API_IMAGE_ENDPOINT = "http://openweathermap.org/img/wn/"
    const val CITY_FORECAST="Krasnoyarsk"  //we set cityName constant and
    const val CITY_LAT="56.0097"  // latitude
    const val CITY_LON="92.7917" //langitude if we can't get no data about device location
    const val LOCATION_PERMISSION_REQUEST_CODE = 100
    const val MIN_TIME_BETWEEN_UPDATES: Long = 1000 * 60 * 5 // 5 minutes
    const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 10f // 10 meters
}