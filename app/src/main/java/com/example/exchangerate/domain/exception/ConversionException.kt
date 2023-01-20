package com.example.exchangerate.domain.exception

sealed class ConversionException(val errorBodyMessage: String) : RuntimeException() {
    object UnsupportedCode : ConversionException(errorBodyMessage = "unsupported-code")
    object MalformedRequest : ConversionException(errorBodyMessage = "malformed-request")
    object InvalidKey : ConversionException(errorBodyMessage = "invalid-key")
    object InactiveAccount : ConversionException(errorBodyMessage = "inactive-account")
    object QuotaReached : ConversionException(errorBodyMessage = "quota-reached")

    companion object {
        private val exceptionList = listOf(
            UnsupportedCode, MalformedRequest, InvalidKey, InactiveAccount, QuotaReached
        )

        fun findExceptionInErrorBodyMessage(message: String): ConversionException? =
            exceptionList.find { it.errorBodyMessage in message }
    }
}