package com.example.exchangerate.domain.exception

sealed class ConversionException(val errorBodyMessage: String? = null) : RuntimeException() {
    object UnsupportedCode : ConversionException(errorBodyMessage = "unsupported-code")
    object MalformedRequest : ConversionException(errorBodyMessage = "malformed-request")
    object InvalidKey : ConversionException(errorBodyMessage = "invalid-key")
    object InactiveAccount : ConversionException(errorBodyMessage = "inactive-account")
    object QuotaReached : ConversionException(errorBodyMessage = "quota-reached")
    object GeneralHttpException : ConversionException()

    companion object {
        private val exceptionsContainingErrorBodyMessage = listOf(
            UnsupportedCode, MalformedRequest, InvalidKey, InactiveAccount, QuotaReached
        )

        fun findExceptionInErrorBodyMessage(message: String): ConversionException? =
            exceptionsContainingErrorBodyMessage.find { it.errorBodyMessage!! in message }
    }
}