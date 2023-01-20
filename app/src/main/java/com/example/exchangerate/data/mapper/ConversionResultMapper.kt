package com.example.exchangerate.data.mapper

import com.example.exchangerate.data.dto.convertresult.ConversionResultDto
import com.example.exchangerate.domain.model.ConversionResult
import com.example.exchangerate.domain.model.Currency

fun ConversionResultDto.toConversionResult() = ConversionResult(
    from = Currency.valueOf(baseCode),
    to = Currency.valueOf(targetCode),
    conversionResult = conversionResult
)