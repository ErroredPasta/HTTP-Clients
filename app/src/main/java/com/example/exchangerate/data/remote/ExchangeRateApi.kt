package com.example.exchangerate.data.remote

import com.example.exchangerate.BuildConfig
import com.example.exchangerate.data.dto.convertresult.ConversionResultDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ExchangeRateApi {
    @GET("/v6/${API_KEY}/pair/{from}/{to}/{amount}")
    suspend fun convertCurrency(
        @Path("from") from: String,
        @Path("to") to: String,
        @Path("amount") amount: String = "1.0"
    ): ConversionResultDto

    companion object {
        /* TODO: 2022-12-15 목 02:25, Api key를 local.properties에 추가 */
        private const val API_KEY = BuildConfig.EXCHANGE_RATE_API_KEY
    }
}