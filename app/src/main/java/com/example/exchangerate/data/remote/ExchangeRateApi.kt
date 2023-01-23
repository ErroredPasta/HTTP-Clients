package com.example.exchangerate.data.remote

import com.example.exchangerate.data.dto.convertresult.ConversionResultDto

interface ExchangeRateApi {

    suspend fun convertCurrency(
        from: String,
        to: String,
        amount: String = "1.0"
    ): ConversionResultDto
}