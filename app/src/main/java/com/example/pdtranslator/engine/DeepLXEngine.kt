package com.example.pdtranslator.engine

import com.example.pdtranslator.R
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

class DeepLXEngine(
  private val client: HttpClient,
  private val endpoint: String
) : TranslationEngine {

  companion object {
    val CONFIG = EngineConfig(
      id = "deeplx",
      nameResId = R.string.engine_deeplx,
      isExperimental = false,
      requiresApiKey = false,
      requiresEndpoint = true
    )
  }

  override val config = CONFIG

  private val baseUrl: String
    get() = endpoint.trimEnd('/')

  override suspend fun translate(text: String, sourceLang: String, targetLang: String): Result<TranslationResult> {
    return try {
      val body = buildJsonObject {
        put("text", text)
        put("source_lang", mapLang(sourceLang))
        put("target_lang", mapLang(targetLang))
      }

      val response: String = client.post("$baseUrl/translate") {
        contentType(ContentType.Application.Json)
        setBody(body.toString())
      }.body()

      val json = Json.parseToJsonElement(response)
      val translated = json.jsonObject["data"]?.jsonPrimitive?.content
        ?: return Result.failure(Exception("Unexpected response format"))

      Result.success(TranslationResult(translated, "DeepLX"))
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  override suspend fun testConnection(): Result<String> {
    return translate("hello", "EN", "ZH").map { "OK: ${it.translatedText}" }
  }

  private fun mapLang(lang: String): String {
    return when {
      lang == "base" -> "EN"
      lang.startsWith("zh") -> "ZH"
      else -> lang.substringBefore("-").uppercase()
    }
  }
}
