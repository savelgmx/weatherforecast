package com.example.weatherforecast


import android.content.pm.PackageManager
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment


@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    private val PERMISSION_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Optional: Set up ActionBar with NavController
        //setupActionBarWithNavController(navController)
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

    @AndroidEntryPoint
    class MainActivity : FragmentActivity() {

        private val PERMISSION_REQUEST_CODE = 123

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            // Initialize NavController
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val navController = navHostFragment.navController

            // Optional: Set up ActionBar with NavController
  //          setupActionBarWithNavController(navController)
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

/*
        override fun onSupportNavigateUp(): Boolean {
            val navController = findNavController(R.id.nav_host_fragment)
            return navController.navigateUp() || super.onSupportNavigateUp()
        }
*/

    }

}





