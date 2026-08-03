package com.example.weatherforecast.data.repositories

import android.content.Context
import com.example.weatherforecast.components.DataStoreManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * Default [SettingsRepository] backed by [DataStoreManager].
 *
 * This is deliberately a thin delegation layer: all persistence logic stays in
 * [DataStoreManager] (DataStore is the low-level store). The repository simply
 * routes the same flows/setters through a single injectable boundary so the UI
 * no longer depends on the static object or a raw Context.
 */
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    override val tempSwitch: Flow<Boolean> = DataStoreManager.tempSwitchPrefFlow(context)

    override val windPref: Flow<Int> = DataStoreManager.windPrefFlow(context)

    override val pressurePref: Flow<Int> = DataStoreManager.pressurePrefFlow(context)

    override val cityName: Flow<String?> = DataStoreManager.cityNamePrefFlow(context)

    override val recentCities: Flow<List<String>> = DataStoreManager.recentCitiesPrefFlow(context)

    override suspend fun updateTempSwitch(isOn: Boolean) =
        DataStoreManager.updateSwitchPref(context, isOn)

    override suspend fun updateWindPref(option: Int) =
        DataStoreManager.updateWindPref(context, option)

    override suspend fun updatePressurePref(option: Int) =
        DataStoreManager.updatePressurePref(context, option)

    override suspend fun updateCityName(city: String) =
        DataStoreManager.updateCityName(context, city)

    override suspend fun addRecentCity(city: String) =
        DataStoreManager.addRecentCity(context, city)
}
