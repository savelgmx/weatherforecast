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

    private lateinit var temperatureUnitsLiveData: MutableLiveData<Boolean>
    private lateinit var distanceUnitsLiveData: MutableLiveData<String>

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onResume() {
        super.onResume()
        val sharedPreferences = preferenceScreen.sharedPreferences
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        temperatureUnitsLiveData = MutableLiveData(sharedPreferences.getBoolean("temperature_units", true))
        distanceUnitsLiveData = MutableLiveData(sharedPreferences.getString("distance_units", "metric") ?: "metric")
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
                    temperatureUnits = sharedViewModel.temperatureUnitsLiveData.value ?: true,
                    distanceUnits = sharedViewModel.distanceUnitsLiveData.value ?: "metric",
                    onTemperatureUnitsChange = { newValue ->
                        preferenceScreen.sharedPreferences.edit().putBoolean("temperature_units", newValue).apply()
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
                    sharedViewModel.temperatureUnitsLiveData.value = sharedPreferences.getBoolean("temperature_units", true)
                }
                "distance_units" -> {
                    sharedViewModel.distanceUnitsLiveData.value = sharedPreferences.getString("distance_units", "metric") ?: "metric"
                }
            }
        }
    }
}
