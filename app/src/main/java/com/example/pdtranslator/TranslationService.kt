package com.example.pdtranslator

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class TranslationRequest(val q: String, val from: String, val to: String)

@Serializable
data class TranslationResponse(val translation: List<String>)

class TranslationService {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.BODY
        }
    }

    suspend fun translate(text: String, from: String, to: String): String? {
        // This is a free API endpoint that doesn't require a key.
        val url = "https://api.mymemory.translated.net/get"
        return try {
            val response: TranslationResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(TranslationRequest(q = text, from = from, to = to))
            }.body()
            response.translation.firstOrNull()
        } catch (e: Exception) {
            // Handle exceptions, e.g., no internet connection
            null
        }
    }
}
