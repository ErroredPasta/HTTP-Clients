package com.example.exchangerate.data.repository.response

import io.ktor.http.*

data class Response(val code: HttpStatusCode, val body: String)
