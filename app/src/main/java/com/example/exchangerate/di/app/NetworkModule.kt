package com.example.exchangerate.di.app

import com.example.exchangerate.data.remote.ExchangeRateApi
import com.example.exchangerate.data.remote.ExchangeRateApiImpl
import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
interface NetworkModule {

    @Binds
    fun bindExchangeRateApi(impl: ExchangeRateApiImpl): ExchangeRateApi

    companion object {

        @Provides
        @Singleton
        fun provideGson(): Gson = Gson()
    }
}