package com.example.weatherforecast.data.repositories

import kotlinx.coroutines.flow.Flow

/**
 * Abstraction over the user's settings persisted in DataStore (review item 5).
 *
 * Exposes each preference as a reactive [Flow] (read side) plus suspend setters
 * (write side). The UI layer — composables and [com.example.weatherforecast.presentation.viewmodels.SettingsViewModel] —
 * depends on this interface instead of touching the static
 * [com.example.weatherforecast.components.DataStoreManager] object directly, keeping the
 * low-level store behind a repository boundary so it is testable and swappable.
 */
interface SettingsRepository {

    /** Celsius/Fahrenheit toggle; `true` == Celsius. Default: `true`. */
    val tempSwitch: Flow<Boolean>

    /** Selected wind-speed unit index (see `R.array.wind_speed_units`). Default: `0`. */
    val windPref: Flow<Int>

    /** Selected pressure unit index (see `R.array.pressure_units`). Default: `0`. */
    val pressurePref: Flow<Int>

    /** Currently-selected/located city name, or `null` when none is set yet. */
    val cityName: Flow<String?>

    /** Recently-selected cities (max 5 entries), newest first. */
    val recentCities: Flow<List<String>>

    suspend fun updateTempSwitch(isOn: Boolean)

    suspend fun updateWindPref(option: Int)

    suspend fun updatePressurePref(option: Int)

    suspend fun updateCityName(city: String)

    suspend fun addRecentCity(city: String)
}
