package com.example.weatherforecast.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.data.repositories.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel exposing the user's measurement/city preferences as [StateFlow]s
 * (review item 5).
 *
 * WHY StateFlow + WhileSubscribed(5000):
 * The upstream DataStore flow is only collected while at least one UI collector
 * is subscribed (plus a 5s grace period after the last one leaves). The
 * Activity-scoped SettingsViewModel instance itself survives recompositions and
 * navigation-destination changes, so the last value is retained between screens;
 * when nothing is collecting, the DataStore flow simply stops emitting instead of
 * doing wasted reads. We intentionally do NOT use SharingStarted.Eagerly — no
 * preference value is consumed outside a composable collector in this Activity.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val tempSwitch: StateFlow<Boolean> = settingsRepository.tempSwitch
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    val windPref: StateFlow<Int> = settingsRepository.windPref
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val pressurePref: StateFlow<Int> = settingsRepository.pressurePref
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val cityName: StateFlow<String?> = settingsRepository.cityName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val recentCities: StateFlow<List<String>> = settingsRepository.recentCities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun updateTempSwitch(isOn: Boolean) {
        viewModelScope.launch { settingsRepository.updateTempSwitch(isOn) }
    }

    fun updateWindPref(option: Int) {
        viewModelScope.launch { settingsRepository.updateWindPref(option) }
    }

    fun updatePressurePref(option: Int) {
        viewModelScope.launch { settingsRepository.updatePressurePref(option) }
    }

    fun updateCityName(city: String) {
        viewModelScope.launch { settingsRepository.updateCityName(city) }
    }

    fun addRecentCity(city: String) {
        viewModelScope.launch { settingsRepository.addRecentCity(city) }
    }
}
