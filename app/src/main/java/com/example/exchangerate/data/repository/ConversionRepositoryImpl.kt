package com.example.exchangerate.data.repository

import com.example.exchangerate.data.mapper.toConversionResult
import com.example.exchangerate.data.remote.ExchangeRateApi
import com.example.exchangerate.domain.model.ConversionResult
import com.example.exchangerate.domain.model.Currency
import com.example.exchangerate.domain.repository.ConversionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ConversionRepositoryImpl @Inject constructor(
    private val api: ExchangeRateApi,
    private val ioDispatcher: CoroutineDispatcher
) : ConversionRepository {

    override suspend fun convertCurrency(
        from: Currency,
        to: Currency,
        amount: Double
    ): ConversionResult = withContext(ioDispatcher) {
        return@withContext api.convertCurrency(
            from = from.toString(),
            to = to.toString(),
            amount = String.format("%f", amount)
        ).toConversionResult()
    }
}