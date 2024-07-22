package com.example.weatherforecast.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
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

                val temperatureUnits by sharedViewModel.temperatureUnitsLiveData.observeAsState(true)
                val distanceUnits by sharedViewModel.distanceUnitsLiveData.observeAsState("metric")

                SettingsScreen(
                    temperatureUnits = temperatureUnits,
                    distanceUnits = distanceUnits,
                    onTemperatureUnitsChange = { newValue ->
                        sharedViewModel.setTemperatureUnits(newValue)
                    },
                    onDistanceUnitsChange = { newValue ->
                        sharedViewModel.setDistanceUnits(newValue)
                    },
                    onDismiss = { requireActivity().onBackPressed() }
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
