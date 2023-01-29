package com.example.exchangerate.di.app

import android.util.Log
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.utils.io.core.*
import javax.inject.Singleton

@Module
object NetworkModule {

    @Provides
    @Singleton
    fun provideKtorClient(
        jsonSerializer: JsonSerializer
    ): HttpClient = HttpClient(Android) {
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d("Ktor", "log: $message")
                }
            }

            level = LogLevel.ALL
        }

        install(JsonFeature) {
            serializer = jsonSerializer
        }

        install(DefaultRequest) {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }
    }

    @Provides
    @Singleton
    fun provideJsonSerializer(): JsonSerializer = object : JsonSerializer {

        private val gson = Gson()

        override fun read(type: TypeInfo, body: Input): Any {
            return gson.fromJson(body.readText(), type.type.java)
        }

        override fun write(data: Any, contentType: ContentType): OutgoingContent {
            return TextContent(gson.toJson(data), contentType)
        }
    }
}