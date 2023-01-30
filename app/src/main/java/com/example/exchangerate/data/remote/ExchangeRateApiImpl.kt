package com.example.exchangerate.data.remote

import com.example.exchangerate.BuildConfig
import com.example.exchangerate.data.dto.convertresult.ConversionResultDto
import io.ktor.client.*
import io.ktor.client.request.*
import javax.inject.Inject

class ExchangeRateApiImpl @Inject constructor(
    private val httpClient: HttpClient
) : ExchangeRateApi {

    override suspend fun convertCurrency(
        from: String,
        to: String,
        amount: String
    ): ConversionResultDto = httpClient.get("${BASE_URL}/v6/${API_KEY}/pair/${from}/${to}/${amount}")

    companion object {
        /* TODO: 2022-12-15 목 02:25, Api key를 local.properties에 추가 */
        private const val API_KEY = BuildConfig.EXCHANGE_RATE_API_KEY
        private const val BASE_URL = "https://v6.exchangerate-api.com/"
    }
}