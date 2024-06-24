package com.example.weatherforecast


import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.TypedValue
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint

import android.graphics.Color
import android.graphics.Typeface
import android.view.Menu
import androidx.fragment.app.FragmentTransaction
import androidx.preference.PreferenceManager
import com.example.weatherforecast.ui.settings.SettingsFragment

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Remove default title
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Inflate the menu
        toolbar.inflateMenu(R.menu.menu_toolbar)
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_settings -> {
                    openSettingsFragment()
                    true
                }
                else -> false
            }
        }

        // Set up NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Set up ActionBar with NavController
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Add destination change listener to update toolbar title manually
        navController.addOnDestinationChangedListener { _, _, _ ->
            updateToolbarTitle("")
        }

        // Access the preferences
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val temperatureUnits = sharedPreferences.getString("temperature_units", "C")
        val distanceUnits = sharedPreferences.getString("distance_units", "metric")

        // Apply the preferences
        // For example:
        applySettings(temperatureUnits, distanceUnits)
    }



    // Function to update toolbar title from fragment
    fun updateToolbarTitle(title: String) {
        val toolbarTitle: TextView = findViewById(R.id.toolbar_title)
        toolbarTitle.text = title
        toolbarTitle.setTextColor(Color.WHITE)
        toolbarTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
        toolbarTitle.setTypeface(toolbarTitle.typeface, Typeface.BOLD)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    // Handle permission request result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with location-related operations
                // You might want to call the weather and forecast APIs here
            } else {
                // Permission denied, handle accordingly (e.g., show explanation, disable functionality)
            }
        }
    }

    // Handle navigation when the up button is pressed
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
    private fun applySettings(temperatureUnits: String?, distanceUnits: String?) {
        //TODO  Implement your logic to apply the settings
    }
    private fun openSettingsFragment() {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, SettingsFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }
}







