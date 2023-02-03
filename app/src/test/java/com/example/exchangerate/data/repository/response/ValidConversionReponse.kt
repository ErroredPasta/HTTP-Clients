package com.example.exchangerate.data.repository.response

import io.ktor.http.*

val validConversionResponse = Response(
    code = HttpStatusCode.OK,
    body = """
    {
      "result": "success",
      "documentation": "https://www.exchangerate-api.com/docs",
      "terms_of_use": "https://www.exchangerate-api.com/terms",
      "time_last_update_unix": 1670976001,
      "time_last_update_utc": "Wed, 14 Dec 2022 00:00:01 +0000",
      "time_next_update_unix": 1671062401,
      "time_next_update_utc": "Thu, 15 Dec 2022 00:00:01 +0000",
      "base_code": "EUR",
      "target_code": "GBP",
      "conversion_rate": 0.8589,
      "conversion_result": 1.7178
    }
    """.trimIndent()
)