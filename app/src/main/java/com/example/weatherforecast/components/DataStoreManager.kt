package com.example.weatherforecast.components

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore by preferencesDataStore(name = "settings")

object DataStoreManager {
    private val TEMP_SWITCH_PREF_KEY     = booleanPreferencesKey("temp_switch_preference")
    private val RADIO_PREF_KEY      = intPreferencesKey("radio_preference")
    private val WIND_PREF_KEY       = intPreferencesKey("wind_speed_preference")
    private val PRESSURE_PREF_KEY   = intPreferencesKey("pressure_preference")
    private val PRECIPATION_PREF_KEY= intPreferencesKey("precipitation_preference")

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

    fun radioPrefFlow(context: Context): Flow<Int> {
        return context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[RADIO_PREF_KEY] ?: 0
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

    fun precipatePrefFlow(context: Context): Flow<Int> {
        return context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[PRECIPATION_PREF_KEY] ?: 0
            }
    }

    suspend fun updateSwitchPref(context: Context, isOn: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[TEMP_SWITCH_PREF_KEY] = isOn
        }
    }

    suspend fun updateRadioPref(context: Context, selectedOption: Int) {
        context.dataStore.edit { preferences ->
            preferences[RADIO_PREF_KEY] = selectedOption
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

    suspend fun updatePrecipatePref(context: Context, selectedOption: Int) {
        context.dataStore.edit { preferences ->
            preferences[PRECIPATION_PREF_KEY] = selectedOption
        }
    }

}
