package com.example.exchangerate.data.remote

import com.example.exchangerate.BuildConfig
import com.example.exchangerate.data.dto.convertresult.ConversionResultDto
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

class ExchangeRateApiImpl @Inject constructor(
    private val client: OkHttpClient,
    private val gson: Gson
) : ExchangeRateApi {

    override suspend fun convertCurrency(
        from: String,
        to: String,
        amount: String
    ): ConversionResultDto {
        val request = Request.Builder()
            .get()
            .url("${BASE_URL}/v6/${API_KEY}/pair/${from}/${to}/${amount}")
            .build()

        val response = client.newCall(request).execute()

        return gson.fromJson(
            response.body?.string(),
            ConversionResultDto::class.java
        )
    }

    companion object {
        /* TODO: 2022-12-15 목 02:25, Api key를 local.properties에 추가 */
        private const val API_KEY = BuildConfig.EXCHANGE_RATE_API_KEY
        private const val BASE_URL = "https://v6.exchangerate-api.com/"
    }
}