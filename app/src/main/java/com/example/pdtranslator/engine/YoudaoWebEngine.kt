package com.example.pdtranslator.engine

import com.example.pdtranslator.R
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class YoudaoWebEngine(
  private val client: HttpClient
) : TranslationEngine {

  companion object {
    val CONFIG = EngineConfig(
      id = "youdao_web",
      nameResId = R.string.engine_youdao_web,
      isExperimental = true,
      requiresApiKey = false
    )
    private const val UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
  }

  override val config = CONFIG

  override suspend fun translate(text: String, sourceLang: String, targetLang: String): Result<TranslationResult> {
    return try {
      val from = mapLang(sourceLang)
      val to = mapLang(targetLang)
      val formData = "i=${java.net.URLEncoder.encode(text, "UTF-8")}&from=$from&to=$to&doctype=json&version=2.1&keyfrom=fanyi.web&action=FY_BY_REALTlME"

      val response: String = client.post("https://fanyi.youdao.com/translate_o?smartresult=dict&smartresult=rule") {
        header("User-Agent", UA)
        header("Referer", "https://fanyi.youdao.com/")
        header("Origin", "https://fanyi.youdao.com")
        contentType(ContentType.Application.FormUrlEncoded)
        setBody(formData)
      }.body()

      // Validate response is JSON
      val trimmed = response.trimStart()
      if (!trimmed.startsWith("{") && !trimmed.startsWith("[")) {
        // Fallback: try simpler endpoint
        return translateFallback(text, from, to)
      }

      val json = Json { ignoreUnknownKeys = true }.parseToJsonElement(response)
      val errorCode = json.jsonObject["errorCode"]?.jsonPrimitive?.content
      if (errorCode != "0") {
        return translateFallback(text, from, to)
      }

      val results = json.jsonObject["translateResult"]?.jsonArray
      val translated = buildString {
        results?.forEach { paragraph ->
          paragraph.jsonArray.forEach { sentence ->
            append(sentence.jsonObject["tgt"]?.jsonPrimitive?.content ?: "")
          }
        }
      }

      if (translated.isBlank()) {
        return Result.failure(Exception("Empty translation"))
      }

      Result.success(TranslationResult(translated, "Youdao Web"))
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  private suspend fun translateFallback(text: String, from: String, to: String): Result<TranslationResult> {
    val formData = "i=${java.net.URLEncoder.encode(text, "UTF-8")}&from=$from&to=$to&doctype=json"

    val response: String = client.post("https://fanyi.youdao.com/translate") {
      header("User-Agent", UA)
      header("Referer", "https://fanyi.youdao.com/")
      contentType(ContentType.Application.FormUrlEncoded)
      setBody(formData)
    }.body()

    val trimmed = response.trimStart()
    if (!trimmed.startsWith("{") && !trimmed.startsWith("[")) {
      return Result.failure(Exception("Server returned non-JSON response"))
    }

    val json = Json { ignoreUnknownKeys = true }.parseToJsonElement(response)
    val errorCode = json.jsonObject["errorCode"]?.jsonPrimitive?.content
    if (errorCode != "0") {
      return Result.failure(Exception("Youdao error: $errorCode"))
    }

    val results = json.jsonObject["translateResult"]?.jsonArray
    val translated = buildString {
      results?.forEach { paragraph ->
        paragraph.jsonArray.forEach { sentence ->
          append(sentence.jsonObject["tgt"]?.jsonPrimitive?.content ?: "")
        }
      }
    }

    if (translated.isBlank()) {
      return Result.failure(Exception("Empty translation"))
    }

    return Result.success(TranslationResult(translated, "Youdao Web"))
  }

  override suspend fun testConnection(): Result<String> {
    return translate("hello", "en", "zh-CHS").map { "OK: ${it.translatedText}" }
  }

  private fun mapLang(lang: String): String {
    return when {
      lang == "base" -> "en"
      lang.startsWith("zh") -> if (lang.contains("TW") || lang.contains("Hant")) "zh-CHT" else "zh-CHS"
      else -> lang.substringBefore("-")
    }
  }
}
