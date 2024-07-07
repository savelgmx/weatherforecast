package com.example.weatherforecast.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.preference.PreferenceFragmentCompat
import com.example.weatherforecast.R
import com.example.weatherforecast.components.SettingsScreen

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
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
}

/*
@Composable
fun SettingsScreen() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val screenWidth = LocalConfiguration.current.screenWidthDp

    ModalDrawer(
        drawerContent = {
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .width((screenWidth / 2).dp),
                elevation = 8.dp,
                color = MaterialTheme.colors.surface
            ) {
                // Your settings UI implementation here
                Text("Settings", modifier = Modifier.padding(16.dp))
            }
        },
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Settings") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.close()
                            }
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                // Your main settings content here
            }
        }
    }
}
*/
