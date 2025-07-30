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
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore by preferencesDataStore(name = "settings")

object DataStoreManager {
    private val TEMP_SWITCH_PREF_KEY     = booleanPreferencesKey("temp_switch_preference")
    private val WIND_PREF_KEY       = intPreferencesKey("wind_speed_preference")
    private val PRESSURE_PREF_KEY   = intPreferencesKey("pressure_preference")
    private val LOCATED_CITY_NAME_KEY  = stringPreferencesKey("located_city_preference")
    private val USE_DEVICE_LOCATION_KEY = booleanPreferencesKey("use_dev_location_preference")

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
                preferences[TEMP_SWITCH_PREF_KEY] ?: false
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

}
