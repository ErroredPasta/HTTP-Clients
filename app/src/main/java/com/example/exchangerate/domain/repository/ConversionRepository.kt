package com.example.exchangerate.domain.repository

import com.example.exchangerate.domain.model.ConversionResult
import com.example.exchangerate.domain.model.Currency

interface ConversionRepository {
    suspend fun convertCurrency(from: Currency, to: Currency, amount: Double): ConversionResult
}