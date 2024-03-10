package com.example.weatherforecast.di

import android.app.Application
import android.content.Context
import javax.inject.Inject

interface ContextProvider {
    fun provideContext(): Context
}

class ApplicationContextProvider @Inject constructor(private val application: Application) : ContextProvider {
    override fun provideContext(): Context {
        return application.applicationContext
    }
}
