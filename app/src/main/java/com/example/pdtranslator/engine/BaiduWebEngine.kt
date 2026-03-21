package com.example.pdtranslator.engine

import com.example.pdtranslator.R
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class BaiduWebEngine(
  private val client: HttpClient
) : TranslationEngine {

  companion object {
    val CONFIG = EngineConfig(
      id = "baidu_web",
      nameResId = R.string.engine_baidu_web,
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

      // Step 1: Get token from Baidu Fanyi page
      val pageHtml: String = client.get("https://fanyi.baidu.com/") {
        header("User-Agent", UA)
      }.body()

      val tokenMatch = Regex("""token:\s*'([a-f0-9]+)'""").find(pageHtml)
      val token = tokenMatch?.groupValues?.get(1) ?: ""

      val gtkMatch = Regex("""gtk\s*=\s*'([^']+)'""").find(pageHtml)
      val gtk = gtkMatch?.groupValues?.get(1) ?: ""

      // Step 2: Calculate sign
      val sign = calcSign(text, gtk)

      // Step 3: Call v2transapi
      val formData = buildString {
        append("from=$from")
        append("&to=$to")
        append("&query=${java.net.URLEncoder.encode(text, "UTF-8")}")
        append("&simple_means_flag=3")
        append("&sign=$sign")
        append("&token=${java.net.URLEncoder.encode(token, "UTF-8")}")
        append("&domain=common")
      }

      val response: String = client.post("https://fanyi.baidu.com/v2transapi?from=$from&to=$to") {
        header("User-Agent", UA)
        header("Referer", "https://fanyi.baidu.com/")
        header("Origin", "https://fanyi.baidu.com")
        header("Cookie", "BAIDUID=0:FG=1")
        contentType(ContentType.Application.FormUrlEncoded)
        setBody(formData)
      }.body()

      val trimmed = response.trimStart()
      if (!trimmed.startsWith("{")) {
        return Result.failure(Exception("Server returned non-JSON response"))
      }

      val json = Json { ignoreUnknownKeys = true }.parseToJsonElement(response)
      val transResult = json.jsonObject["trans_result"]?.jsonObject
      val data = transResult?.get("data")?.jsonArray

      if (data == null || data.isEmpty()) {
        // Fallback: try simple endpoint
        return translateFallback(text, from, to)
      }

      val translated = buildString {
        for (item in data) {
          append(item.jsonObject["dst"]?.jsonPrimitive?.content ?: "")
        }
      }

      if (translated.isBlank()) {
        return translateFallback(text, from, to)
      }

      Result.success(TranslationResult(translated, "Baidu Web"))
    } catch (e: Exception) {
      // Fallback on any error
      try {
        val from = mapLang(sourceLang)
        val to = mapLang(targetLang)
        translateFallback(text, from, to)
      } catch (e2: Exception) {
        Result.failure(e)
      }
    }
  }

  /** Fallback: use the simpler sug/langdetect-based endpoint */
  private suspend fun translateFallback(text: String, from: String, to: String): Result<TranslationResult> {
    return try {
      val q = java.net.URLEncoder.encode(text, "UTF-8")
      val response: String = client.get(
        "https://fanyi.baidu.com/transapi?from=$from&to=$to&query=$q"
      ) {
        header("User-Agent", UA)
        header("Referer", "https://fanyi.baidu.com/")
      }.body()

      val trimmed = response.trimStart()
      if (!trimmed.startsWith("{") && !trimmed.startsWith("[")) {
        return Result.failure(Exception("Server returned non-JSON response"))
      }

      val json = Json { ignoreUnknownKeys = true }.parseToJsonElement(response)

      // transapi returns: { "data": [{ "dst": "...", "src": "..." }] }
      val data = json.jsonObject["data"]?.jsonArray
      if (data != null && data.isNotEmpty()) {
        val translated = buildString {
          for (item in data) {
            append(item.jsonObject["dst"]?.jsonPrimitive?.content ?: "")
          }
        }
        if (translated.isNotBlank()) {
          return Result.success(TranslationResult(translated, "Baidu Web"))
        }
      }

      Result.failure(Exception("Empty translation result"))
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  override suspend fun testConnection(): Result<String> {
    return translate("hello", "en", "zh").map { "OK: ${it.translatedText}" }
  }

  private fun mapLang(lang: String): String {
    return when {
      lang == "base" -> "en"
      lang.startsWith("zh") -> if (lang.contains("TW") || lang.contains("Hant")) "cht" else "zh"
      lang == "ja" -> "jp"
      lang == "ko" -> "kor"
      else -> lang.substringBefore("-")
    }
  }

  /**
   * Baidu sign algorithm — JavaScript port of baidu fanyi's token generation.
   * Generates a hash based on the query text and the gtk seed from the page.
   */
  private fun calcSign(text: String, gtk: String): String {
    val gtkParts = gtk.split(".")
    val s1 = gtkParts.getOrNull(0)?.toLongOrNull() ?: 320305L
    val s2 = gtkParts.getOrNull(1)?.toLongOrNull() ?: 131321201L

    val codePoints = mutableListOf<Long>()
    var i = 0
    while (i < text.length) {
      val cp = text.codePointAt(i)
      if (cp <= 0x7F) {
        codePoints.add(cp.toLong())
      } else if (cp <= 0x7FF) {
        codePoints.add((cp shr 6 or 0xC0).toLong())
        codePoints.add((cp and 0x3F or 0x80).toLong())
      } else if (cp in 0xD800..0xDBFF && i + 1 < text.length) {
        val next = text.codePointAt(i + 1)
        if (next in 0xDC00..0xDFFF) {
          val full = ((cp and 0x3FF) shl 10) + (next and 0x3FF) + 0x10000
          codePoints.add((full shr 18 or 0xF0).toLong())
          codePoints.add((full shr 12 and 0x3F or 0x80).toLong())
          codePoints.add((full shr 6 and 0x3F or 0x80).toLong())
          codePoints.add((full and 0x3F or 0x80).toLong())
          i++
        }
      } else {
        codePoints.add((cp shr 12 or 0xE0).toLong())
        codePoints.add((cp shr 6 and 0x3F or 0x80).toLong())
        codePoints.add((cp and 0x3F or 0x80).toLong())
      }
      i++
    }

    var seed = s1
    for (b in codePoints) {
      seed += b
      seed = hashOp(seed, "+-a^+6")
    }
    seed = hashOp(seed, "+-3^+b+-f")
    seed = seed xor s2
    if (seed < 0) {
      seed = (seed and 0x7FFFFFFF) + 0x80000000L
    }
    seed %= 1000000
    return "$seed.${seed xor s1}"
  }

  private fun hashOp(value: Long, ops: String): Long {
    var result = value
    var i = 0
    while (i < ops.length - 2) {
      val c = ops[i + 2]
      val d = if (c >= 'a') (c.code - 87).toLong() else c.toString().toLong()
      val shifted = if (ops[i + 1] == '+') result.ushr(d.toInt()) else result shl d.toInt()
      result = if (ops[i] == '+') (result + shifted) and 0xFFFFFFFFL else result xor shifted
      i += 3
    }
    return result
  }

  private fun Long.ushr(bits: Int): Long = (this and 0xFFFFFFFFL).shr(bits)
}
