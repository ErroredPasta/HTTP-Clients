package com.example.exchangerate.di.app

import com.example.exchangerate.di.activity.MainActivityComponent
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        NetworkModule::class,
        DispatcherModule::class,
        ViewModelFactoryModule::class,
        RepositoryModule::class
    ]
)
interface AppComponent {
    fun getMainActivityComponentFactory(): MainActivityComponent.Factory
}