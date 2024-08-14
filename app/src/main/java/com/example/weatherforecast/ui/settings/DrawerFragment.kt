package com.example.weatherforecast.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.weatherforecast.components.DrawerContent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DrawerFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                val navController = findNavController()
                DrawerContent(navController =navController)
            }
        }
    }
}