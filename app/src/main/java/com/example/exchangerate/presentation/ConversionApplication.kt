package com.example.exchangerate.presentation

import android.app.Application
import com.example.exchangerate.di.app.AppComponent
import com.example.exchangerate.di.app.DaggerAppComponent

class ConversionApplication : Application() {
    val appComponent: AppComponent by lazy {
        DaggerAppComponent.create()
    }
}