package com.example.weatherforecast

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer

@HiltAndroidApp
class BaseApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // ⚠ MapLibre.getInstance() ДОЛЖЕН быть вызван до любого создания MapView.
        // Без этого вызова MapLibre выбрасывает MapLibreConfigurationException
        // при первой попытке отобразить карту (Bug #3 — crash fix).
        // Используем MapTiler в качестве tile-сервера с API-ключом из BuildConfig.
        MapLibre.getInstance(this, BuildConfig.MAPTILER_API_KEY, WellKnownTileServer.MapTiler)
    }
}