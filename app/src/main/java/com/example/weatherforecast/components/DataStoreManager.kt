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
    /**
     * Flow that emits the currently-selected city name whenever it changes.
     *
     * BUGFIX (distinctUntilChanged):
     * Without .distinctUntilChanged(), ANY write to ANY DataStore key — including
     * writes to RECENT_CITIES_KEY by addRecentCity() — causes the underlying
     * dataStore.data to emit a new Preferences instance, which re-evaluates
     * .map { preferences[LOCATED_CITY_NAME_KEY] } and emits the SAME value again.
     *
     * This was the root cause of the auto-detect race:
     *   1. selectCity() called addRecentCity() (writes RECENT_CITIES_KEY)
     *   2. dataStore.data emits → cityNamePrefFlow re-maps LOCATED_CITY_NAME_KEY
     *   3. LOCATED_CITY_NAME_KEY hasn't been written yet (or is still the old value)
     *      → the observer sees null/blank → restarts the auto-detect flow
     *      OR shows the city-selection dialog again
     *   4. Then updateCityName() writes the correct value — too late
     *
     * .distinctUntilChanged() prevents this by comparing the new emission to the
     * previous one and only propagating when the value ACTUALLY changes.
     */
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

    /**
     * Reactive Flow of the recent-cities list (max 5 entries).
     * Used by CitySelectionDialog to show the dropdown suggestions.
     *
     * BUGFIX (blank filter):
     * "".split(",") in Kotlin returns [""] (a list with one empty string), not [].
     * This means if RECENT_CITIES_KEY was ever set to an empty string (or was
     * never set → raw is null, which is handled by isNullOrBlank), the old code
     * would produce ["", ""] (etc.) after repeated add → split cycles, causing
     * blank rows to appear in the city-selection dropdown.
     *
     * The .filter { it.isNotBlank() } guard ensures empty entries are never
     * exposed to the UI layer.
     */
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

    /**
     * Adds a city to the front of the recent-cities history (max 5 entries).
     * If the city already exists in the list, it is moved to the front.
     *
     * BUGFIX 1 — trim:
     * Without trimming, "London " and "London" would be treated as different cities,
     * creating duplicate entries and consuming slots in the 5-city limit.
     *
     * BUGFIX 2 — filter blanks from loaded list:
     * Entries that were stored by the old "".split(",") → [""] bug (before the
     * .filter { it.isNotBlank() } guards were added) are cleaned up on every write.
     *
     * ORDERING (call-site responsibility):
     * This function must be called AFTER updateCityName(). Writing RECENT_CITIES_KEY
     * triggers a dataStore.data emission, which cityNamePrefFlow re-evaluates.
     * If the city hasn't been saved to LOCATED_CITY_NAME_KEY yet, the flow emits
     * the old (null/blank) value — restarting auto-detect or showing the dialog.
     * .distinctUntilChanged() on cityNamePrefFlow guards against this, but keeping
     * the calls ordered is the correct defensive approach.
     */
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
    /**
     * One-shot (non-reactive) read of the recent-cities list from DataStore.
     * Used by addRecentCity() to read the current list before modifying it.
     *
     * BUGFIX: same filter-blanks guard as recentCitiesPrefFlow — ensures that
     * any empty-string entries lingering from the old "".split(",") → [""] bug
     * are filtered out on every read, not just on the reactive Flow path.
     */
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

    /**
     * Persists the recent-cities list to DataStore as a comma-separated string.
     * The reactive Flow (recentCitiesPrefFlow) picks up the change automatically.
     *
     * Encoding: cities.joinToString(",") — simple CSV without escaping because
     * city names are assumed not to contain commas. If that assumption changes,
     * switch to a proper encoding (JSON, URL-encoded, etc.).
     */
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
