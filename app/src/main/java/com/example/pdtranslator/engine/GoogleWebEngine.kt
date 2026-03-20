package com.example.pdtranslator.engine

import com.example.pdtranslator.R
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

class GoogleWebEngine(
  private val client: HttpClient
) : TranslationEngine {

  companion object {
    val CONFIG = EngineConfig(
      id = "google_web",
      nameResId = R.string.engine_google_web,
      isExperimental = true,
      requiresApiKey = false
    )
  }

  override val config = CONFIG

  override suspend fun translate(text: String, sourceLang: String, targetLang: String): Result<TranslationResult> {
    return try {
      val sl = mapLang(sourceLang)
      val tl = mapLang(targetLang)
      val q = java.net.URLEncoder.encode(text, "UTF-8")

      val response: String = client.get(
        "https://translate.googleapis.com/translate_a/single?client=gtx&sl=$sl&tl=$tl&dt=t&q=$q"
      ).body()

      val json = Json.parseToJsonElement(response)
      val sentences = json.jsonArray[0].jsonArray
      val translated = buildString {
        for (i in 0 until sentences.size) {
          val sentence = sentences[i]
          if (sentence is kotlinx.serialization.json.JsonArray && sentence.size > 0) {
            append(sentence[0].jsonPrimitive.content)
          }
        }
      }

      if (translated.isBlank()) {
        return Result.failure(Exception("Empty translation"))
      }

      Result.success(TranslationResult(translated, "Google Web"))
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  override suspend fun testConnection(): Result<String> {
    return translate("hello", "en", "zh-CN").map { "OK: ${it.translatedText}" }
  }

  private fun mapLang(lang: String): String {
    return when {
      lang == "base" -> "en"
      lang.startsWith("zh") -> if (lang.contains("TW") || lang.contains("Hant")) "zh-TW" else "zh-CN"
      else -> lang.substringBefore("-")
    }
  }
}
