package com.example.exchangerate.domain.model

data class ConversionResult(
    val from: Currency,
    val to: Currency,
    val conversionResult: Double
)
