package com.example.weatherforecast.utils

sealed class Resource<T>(val data: T? = null, val msg: String? = null, val isStale: Boolean = false) {
    class Success<T>(data: T?, isStale: Boolean = false) : Resource<T>(data, isStale = isStale)
    class Error<T>(data: T? = null, msg: String?) : Resource<T>(data, msg)

    class Loading<T> : Resource<T>()
    class Internet<T> : Resource<T>()
}