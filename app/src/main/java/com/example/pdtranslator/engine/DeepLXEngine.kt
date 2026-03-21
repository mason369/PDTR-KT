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
  private val endpoint: String = ""
) : TranslationEngine {

  companion object {
    val CONFIG = EngineConfig(
      id = "deeplx",
      nameResId = R.string.engine_deeplx,
      isExperimental = true,
      requiresApiKey = false,
      requiresEndpoint = false  // optional — uses public endpoint by default
    )
    private val PUBLIC_ENDPOINTS = listOf(
      "https://api.deeplx.org",
      "https://deeplx.missuo.ru"
    )
  }

  override val config = CONFIG

  private val baseUrl: String
    get() = if (endpoint.isNotBlank()) endpoint.trimEnd('/') else ""

  override suspend fun translate(text: String, sourceLang: String, targetLang: String): Result<TranslationResult> {
    val body = buildJsonObject {
      put("text", text)
      put("source_lang", mapLang(sourceLang))
      put("target_lang", mapLang(targetLang))
    }

    // If user provided a custom endpoint, try that first
    if (baseUrl.isNotBlank()) {
      val result = tryTranslate(baseUrl, body.toString())
      if (result.isSuccess) return result
    }

    // Try public endpoints
    for (pub in PUBLIC_ENDPOINTS) {
      val result = tryTranslate(pub, body.toString())
      if (result.isSuccess) return result
    }

    return Result.failure(Exception("All DeepLX endpoints unavailable"))
  }

  private suspend fun tryTranslate(endpoint: String, bodyJson: String): Result<TranslationResult> {
    return try {
      val response: String = client.post("$endpoint/translate") {
        contentType(ContentType.Application.Json)
        setBody(bodyJson)
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
