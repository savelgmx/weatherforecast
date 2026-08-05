package com.example.weatherforecast.domain.models

// =============================
// Domain Layer
// =============================

// Domain model for one HOURLY weather sample at the selected city.
// The repository builds one WeatherPoint per hour of each forecast day, and the
// map UI renders numeric temp/precip/wind from one of these points. Because a
// point must remember WHICH hour it describes (see `timeEpoch`), otherwise the
// UI cannot tell a "current hour" sample from an old one.
data class WeatherPoint(
    val lat: Double,
    val lon: Double,
    val temperature: Double?,
    val cloudCover: Double?,
    val precipitation: Double?,
    val windSpeed: Double? = null,
    // BUG FIX: epoch-seconds timestamp (seconds, not millis) of the hour this
    // sample belongs to — maps 1:1 to the API field "datetimeEpoch".
    //
    // WHY: the info card used to pick `points.firstOrNull()`, i.e. the very first
    // hourly sample of the first forecast day (00:00), so it always showed the same
    // static values that did NOT match the current weather. This field lets the UI
    // select the sample whose hour is CLOSEST to the current time instead.
    //
    // Nullable + default null keeps every existing WeatherPoint(...) call site valid
    // (pure additive domain change). A null value means "timestamp unknown" and is
    // treated as far-in-the-future by the selection logic, so such a point never wins
    // the tie against a real hour (see WeatherMapScreen.kt CityWeatherInfoCard).
    val timeEpoch: Long? = null
)