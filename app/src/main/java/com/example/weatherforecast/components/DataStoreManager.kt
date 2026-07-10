package com.example.weatherforecast.components

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore by preferencesDataStore(name = "settings")

object DataStoreManager {
    private val TEMP_SWITCH_PREF_KEY     = booleanPreferencesKey("temp_switch_preference")
    private val WIND_PREF_KEY       = intPreferencesKey("wind_speed_preference")
    private val PRESSURE_PREF_KEY   = intPreferencesKey("pressure_preference")
    private val LOCATED_CITY_NAME_KEY  = stringPreferencesKey("located_city_preference")
    private val USE_DEVICE_LOCATION_KEY = booleanPreferencesKey("use_dev_location_preference")
    private val RECENT_CITIES_KEY = stringPreferencesKey("recent_cities")

    fun tempSwitchPrefFlow(context: Context): Flow<Boolean> {
        return context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[TEMP_SWITCH_PREF_KEY] ?: true
            }
    }

    fun windPrefFlow(context: Context): Flow<Int> {
        return context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[WIND_PREF_KEY] ?: 0
            }
    }
    fun pressurePrefFlow(context: Context): Flow<Int> {
        return context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[PRESSURE_PREF_KEY] ?: 0
            }
    }
    fun cityNamePrefFlow(context: Context): Flow<String?> {
        return context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[LOCATED_CITY_NAME_KEY]
            }
            .distinctUntilChanged()
    }

    fun recentCitiesPrefFlow(context: Context): Flow<List<String>> {
        return context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val raw = preferences[RECENT_CITIES_KEY]
                // BUGFIX: "".split(",") returns [""] not []. Filter blanks to prevent
                // empty-string entries from leaking into the suggestions dropdown.
                if (raw.isNullOrBlank()) emptyList() else raw.split(",").filter { it.isNotBlank() }
            }
    }

    suspend fun updateSwitchPref(context: Context, isOn: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[TEMP_SWITCH_PREF_KEY] = isOn
        }
    }

    suspend fun updateWindPref(context: Context, selectedOption: Int) {
        context.dataStore.edit { preferences ->
            preferences[WIND_PREF_KEY] = selectedOption
        }
    }
    suspend fun updatePressurePref(context: Context, selectedOption: Int) {
        context.dataStore.edit { preferences ->
            preferences[PRESSURE_PREF_KEY] = selectedOption
        }
    }

    suspend fun updateCityName(context: Context, cityName: String) {
        context.dataStore.edit { preferences ->
            preferences[LOCATED_CITY_NAME_KEY] = cityName
        }
    }

    suspend fun addRecentCity(context: Context, city: String) {
        // BUGFIX: trim the city so "London " and "London" don't create duplicates.
        val trimmedCity = city.trim()
        if (trimmedCity.isBlank()) {
            Log.d("DataStore", "addRecentCity rejected: blank city")
            return
        }
        Log.d("DataStore", "addRecentCity called: $trimmedCity")
        // BUGFIX: filter out any blank entries that may have been stored by a
        // previous "".split(",") → [""] bug.
        val recentCities = getRecentCities(context).filter { it.isNotBlank() }
        Log.d("DataStore", "Current recent cities: $recentCities")
        val updatedCities = recentCities.toMutableList()
        updatedCities.remove(trimmedCity)
        updatedCities.add(0, trimmedCity)
        val trimmedCities = if (updatedCities.size > 5) updatedCities.subList(0, 5) else updatedCities
        Log.d("DataStore", "Saving recent cities: $trimmedCities")
        saveRecentCities(context, trimmedCities)
        Log.d("DataStore", "Recent cities saved successfully")
    }
    suspend fun getRecentCities(context: Context): List<String> {
        return context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                // BUGFIX: same filter-blanks guard as recentCitiesPrefFlow
                val raw = preferences[RECENT_CITIES_KEY]
                if (raw.isNullOrBlank()) emptyList() else raw.split(",").filter { it.isNotBlank() }
            }.firstOrNull() ?: emptyList()
    }

    suspend fun saveRecentCities(context: Context, cities: List<String>) {
        context.dataStore.edit { preferences ->
            preferences[RECENT_CITIES_KEY] = cities.joinToString(",")
        }
    }

    suspend fun clearRecentCities(context: Context) {
        context.dataStore.edit { preferences ->
            preferences.remove(RECENT_CITIES_KEY)
        }
    }
}
