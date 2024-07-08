package com.example.weatherforecast.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.ui.platform.ComposeView
import androidx.preference.ListPreference
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

        // Wrap the PreferenceFragmentCompat view with a ComposeView
        val composeView = ComposeView(requireContext()).apply {
            setContent {
                SettingsScreen()
            }
        }

        // Create a FrameLayout to hold the original PreferenceFragmentCompat view and the ComposeView
        val frameLayout = FrameLayout(requireContext()).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // Add the original view and the ComposeView to the FrameLayout
        frameLayout.addView(view)
        frameLayout.addView(composeView)

        return frameLayout
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == "temperature_units" || key == "distance_units") {
            val preference = findPreference<ListPreference>(key)
            preference?.let {
                // Trigger a function call or handle the change
                handlePreferenceChange(key, it.value)
            }
        }

    }



    private fun handlePreferenceChange(key: String, value: String) {
        // Handle the preference change
        // For example, update a global setting or notify other components
        when (key) {
            "temperature_units" -> {
                // Handle temperature units change
            }

            "distance_units" -> {
                // Handle distance units change
            }
        }
    }

}
