package com.example.exchangerate.data.remote

import com.example.exchangerate.BuildConfig
import com.example.exchangerate.data.dto.convertresult.ConversionResultDto
import com.example.exchangerate.domain.exception.ConversionException
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

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

        return suspendCoroutine { continuation ->
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val conversionResultDto = gson.fromJson(
                            response.body?.string(),
                            ConversionResultDto::class.java
                        )

                        continuation.resume(conversionResultDto)
                    } else {
                        val exception = response.body?.let {
                            ConversionException.findExceptionInErrorBodyMessage(it.string())
                        } ?: ConversionException.GeneralHttpException

                        continuation.resumeWithException(exception)
                    }
                }
            })
        }
    }

    companion object {
        /* TODO: 2022-12-15 목 02:25, Api key를 local.properties에 추가 */
        private const val API_KEY = BuildConfig.EXCHANGE_RATE_API_KEY
        private const val BASE_URL = "https://v6.exchangerate-api.com/"
    }
}