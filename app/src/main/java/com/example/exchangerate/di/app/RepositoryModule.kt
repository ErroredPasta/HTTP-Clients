package com.example.exchangerate.di.app

import com.example.exchangerate.data.repository.ConversionRepositoryImpl
import com.example.exchangerate.domain.repository.ConversionRepository
import dagger.Binds
import dagger.Module

@Module
interface RepositoryModule {

    @Binds
    fun bindConversionRepository(repositoryImpl: ConversionRepositoryImpl): ConversionRepository
}