package com.example.weatherforecast.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import com.example.weatherforecast.R
import com.example.weatherforecast.components.DistanceUnitsScreen
import com.example.weatherforecast.ui.viewmodel.SharedViewModel
import com.example.weatherforecast.ui.viewmodel.SharedViewModelHolder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DistanceUnitsFragment : PreferenceFragmentCompat() {

    private val sharedViewModel: SharedViewModel
        get() = SharedViewModelHolder.sharedViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState).apply {
            val preferenceScreen = this@DistanceUnitsFragment.preferenceScreen

            this?.findViewById<FrameLayout>(android.R.id.list_container)?.let {
                it.addView(ComposeView(requireContext()).apply {
                    setContent {
                        DistanceUnitsScreen(
                            onDistanceUnitsChange = { newValue ->
                                preferenceScreen.sharedPreferences.edit().putString("distance_units", newValue).apply()
                                sharedViewModel.distanceUnitsLiveData.value = newValue
                            },
                            onWindSpeedChange = { newValue ->
                                preferenceScreen.sharedPreferences.edit().putString("wind_speed", newValue).apply()
                                sharedViewModel.windSpeedLiveData.value = newValue
                            },
                            onDone = {
                                findNavController().navigateUp()
                            }
                        )
                    }
                })
            }
        }
    }
}
