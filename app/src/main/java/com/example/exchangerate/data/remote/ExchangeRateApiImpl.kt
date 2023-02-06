package com.example.exchangerate.data.remote

import com.example.exchangerate.BuildConfig
import com.example.exchangerate.data.dto.convertresult.ConversionResultDto
import com.example.exchangerate.domain.exception.ConversionException
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.net.URL
import javax.inject.Inject
import javax.net.ssl.HttpsURLConnection


class ExchangeRateApiImpl @Inject constructor(
    private val gson: Gson,
    private val ioDispatcher: CoroutineDispatcher
) : ExchangeRateApi {

    override suspend fun convertCurrency(
        from: String,
        to: String,
        amount: String
    ): ConversionResultDto {
        @Suppress("BlockingMethodInNonBlockingContext")
        val getRequestConnection = withContext(ioDispatcher) {
            URL("${BASE_URL}/v6/${API_KEY}/pair/${from}/${to}/${amount}").openConnection() as HttpsURLConnection
        }.apply {
            requestMethod = "GET"
        }

        val responseCode = getRequestConnection.responseCode
        val responseBody = getRequestConnection.inputStream.bufferedReader().readText()

        return if (responseCode == HttpsURLConnection.HTTP_OK) {
            gson.fromJson(responseBody, ConversionResultDto::class.java)
        } else {
            throw ConversionException.findExceptionInErrorBodyMessage(responseBody)
                ?: ConversionException.GeneralHttpException
        }
    }

    companion object {
        /* TODO: 2022-12-15 목 02:25, Api key를 local.properties에 추가 */
        private const val API_KEY = BuildConfig.EXCHANGE_RATE_API_KEY
        private const val BASE_URL = "https://v6.exchangerate-api.com/"
    }
}