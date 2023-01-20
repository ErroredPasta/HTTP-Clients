package com.example.exchangerate.di.app

import androidx.lifecycle.ViewModelProvider
import com.example.exchangerate.di.activity.MainActivityComponent
import com.example.exchangerate.presentation.MainViewModel
import dagger.Binds
import dagger.Module

@Module(subcomponents = [MainActivityComponent::class])
interface ViewModelFactoryModule {

    @Binds
    fun bindMainViewModelFactory(factory: MainViewModel.Factory): ViewModelProvider.Factory
}