package com.example.exchangerate.di.app

import com.example.exchangerate.data.remote.ExchangeRateApi
import com.example.exchangerate.data.remote.ExchangeRateApiImpl
import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
interface NetworkModule {

    @Binds
    fun bindExchangeApi(impl: ExchangeRateApiImpl): ExchangeRateApi

    companion object {
        @Provides
        @Singleton
        fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }).build()

        @Provides
        fun provideGson(): Gson = Gson()
    }
}