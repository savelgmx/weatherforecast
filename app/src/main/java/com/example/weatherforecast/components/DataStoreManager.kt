package com.example.weatherforecast.components

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
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
                preferences[RECENT_CITIES_KEY]?.split(",")?.toList() ?: emptyList()
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
        val recentCities = getRecentCities(context)

        // Remove if already exists
        val updatedCities = recentCities.toMutableList()
        updatedCities.remove(city)

        // Add to front
        updatedCities.add(0, city)

        // Keep only last 5
        if (updatedCities.size > 5) {
            updatedCities.subList(0, 5)
        }

        saveRecentCities(context, updatedCities)
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
            preferences[RECENT_CITIES_KEY]?.split(",")?.toList() ?: emptyList()
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
