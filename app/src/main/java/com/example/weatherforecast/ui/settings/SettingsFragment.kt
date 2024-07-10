package com.example.weatherforecast.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.preference.PreferenceFragmentCompat
import com.example.weatherforecast.R
import com.example.weatherforecast.components.SettingsScreen

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        // Only use ComposeView
        return ComposeView(requireContext()).apply {
            setContent {
                SettingsScreen(
                    temperatureUnits = preferenceScreen.sharedPreferences.getString("temperature_units", "C") ?: "C",
                    distanceUnits = preferenceScreen.sharedPreferences.getString("distance_units", "metric") ?: "metric",
                    onTemperatureUnitsChange = { newValue ->
                        preferenceScreen.sharedPreferences.edit().putString("temperature_units", newValue).apply()
                    },
                    onDistanceUnitsChange = { newValue ->
                        preferenceScreen.sharedPreferences.edit().putString("distance_units", newValue).apply()
                    },
                    onDismiss = { requireActivity().onBackPressed() } // Handle dismiss
                )
            }
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        // Handle preference changes if necessary
    }
}
