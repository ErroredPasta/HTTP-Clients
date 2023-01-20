package com.example.exchangerate.data.dto.convertresult


import com.google.gson.annotations.SerializedName

data class ConversionResultDto(
    @SerializedName("base_code")
    val baseCode: String, // EUR
    @SerializedName("conversion_rate")
    val conversionRate: Double, // 0.8592
    @SerializedName("conversion_result")
    val conversionResult: Double, // 0.8592
    @SerializedName("documentation")
    val documentation: String, // https://www.exchangerate-api.com/docs
    @SerializedName("result")
    val result: String, // success
    @SerializedName("target_code")
    val targetCode: String, // GBP
    @SerializedName("terms_of_use")
    val termsOfUse: String, // https://www.exchangerate-api.com/terms
    @SerializedName("time_last_update_unix")
    val timeLastUpdateUnix: Int, // 1670889601
    @SerializedName("time_last_update_utc")
    val timeLastUpdateUtc: String, // Tue, 13 Dec 2022 00:00:01 +0000
    @SerializedName("time_next_update_unix")
    val timeNextUpdateUnix: Int, // 1670976001
    @SerializedName("time_next_update_utc")
    val timeNextUpdateUtc: String // Wed, 14 Dec 2022 00:00:01 +0000
)