package com.example.weatherforecast.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceFragmentCompat
import com.example.weatherforecast.R
import com.example.weatherforecast.components.SettingsScreen
import com.example.weatherforecast.ui.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val sharedViewModel: SharedViewModel by viewModels()

    private lateinit var temperatureUnitsLiveData: MutableLiveData<String>
    private lateinit var distanceUnitsLiveData: MutableLiveData<String>

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onResume() {
        super.onResume()
        val sharedPreferences = preferenceScreen.sharedPreferences
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        temperatureUnitsLiveData = MutableLiveData(sharedPreferences.getString("temperature_units", "Celsius") ?: "Celsius")
        distanceUnitsLiveData = MutableLiveData(sharedPreferences.getString("distance_units", "Metric") ?: "Metric")
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
                    temperatureUnits = sharedViewModel.temperatureUnitsLiveData.value ?: "Celsius",
                    distanceUnits = sharedViewModel.distanceUnitsLiveData.value ?: "Metric",
                    onTemperatureUnitsChange = { newValue ->
                        preferenceScreen.sharedPreferences.edit().putString("temperature_units", newValue).apply()
                        sharedViewModel.temperatureUnitsLiveData.value = newValue
                    },
                    onDistanceUnitsChange = { newValue ->
                        preferenceScreen.sharedPreferences.edit().putString("distance_units", newValue).apply()
                        sharedViewModel.distanceUnitsLiveData.value = newValue
                    },
                    onDismiss = { requireActivity().onBackPressed() } // Handle dismiss
                )
            }
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (sharedPreferences != null) {
            when (key) {
                "temperature_units" -> {
                    sharedViewModel.temperatureUnitsLiveData.value = sharedPreferences.getString("temperature_units", "Celsius") ?: "Celsius"
                }
                "distance_units" -> {
                    sharedViewModel.distanceUnitsLiveData.value = sharedPreferences.getString("distance_units", "Metric") ?: "Metric"
                }
            }
        }
    }
}
