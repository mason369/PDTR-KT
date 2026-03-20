package com.example.pdtranslator

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

data class DictEntry(
  val sourceText: String? = null,
  val translation: String,
  val timestamp: Long
)

class DictionaryManager(private val context: Context) {

  // Key format (new): "groupName|srcLang|tgtLang|propertyKey" (4 segments)
  // Key format (old): "srcLang|tgtLang|propertyKey" (3 segments)
  private val entries = mutableMapOf<String, DictEntry>()
  private val gson = Gson()
  private val file: File get() = File(context.filesDir, "dictionary.json")

  private fun newKey(groupName: String, srcLang: String, tgtLang: String, propKey: String): String {
    return "$groupName|$srcLang|$tgtLang|$propKey"
  }

  private fun oldKey(srcLang: String, tgtLang: String, propKey: String): String {
    return "$srcLang|$tgtLang|$propKey"
  }

  fun addEntry(groupName: String, srcLang: String, tgtLang: String, propKey: String, sourceText: String, translation: String) {
    if (translation.isBlank()) return
    val nk = newKey(groupName, srcLang, tgtLang, propKey)
    val ok = oldKey(srcLang, tgtLang, propKey)
    entries.remove(ok)  // clean up old format
    entries[nk] = DictEntry(
      sourceText = sourceText,
      translation = translation,
      timestamp = System.currentTimeMillis()
    )
  }

  fun getEntry(groupName: String, srcLang: String, tgtLang: String, propKey: String): DictEntry? {
    // Try new key (4 segments) first
    val nk = newKey(groupName, srcLang, tgtLang, propKey)
    entries[nk]?.let { return it }
    // Fallback to old key (3 segments)
    val ok = oldKey(srcLang, tgtLang, propKey)
    return entries[ok]
  }

  fun getTranslation(groupName: String, srcLang: String, tgtLang: String, propKey: String): String? {
    return getEntry(groupName, srcLang, tgtLang, propKey)?.translation
  }

  fun getTotalCount(): Int = entries.keys.count { it.count { c -> c == '|' } == 3 }

  fun importFromProperties(
    sourceProps: java.util.Properties,
    targetProps: java.util.Properties,
    groupName: String,
    srcLang: String,
    tgtLang: String
  ): Int {
    var count = 0
    sourceProps.forEach { (key, srcValue) ->
      val propKey = key as String
      val sourceText = srcValue as String
      val targetText = targetProps.getProperty(propKey)
      if (!targetText.isNullOrBlank() && sourceText.isNotBlank()) {
        addEntry(groupName, srcLang, tgtLang, propKey, sourceText, targetText)
        count++
      }
    }
    return count
  }

  fun applyToEntries(
    entries: List<TranslationEntry>,
    groupName: String,
    srcLang: String,
    tgtLang: String
  ): Map<String, String> {
    val applied = mutableMapOf<String, String>()
    entries.forEach { entry ->
      if (entry.targetValue.isBlank() || entry.isMissing) {
        val dictEntry = getEntry(groupName, srcLang, tgtLang, entry.key)
        if (dictEntry != null &&
            (dictEntry.sourceText == null || dictEntry.sourceText == entry.sourceValue)) {
          applied[entry.key] = dictEntry.translation
        }
      }
    }
    return applied
  }

  suspend fun save() {
    withContext(Dispatchers.IO) {
      val json = gson.toJson(entries)
      val tmpFile = File(context.filesDir, "dictionary.json.tmp")
      tmpFile.writeText(json, Charsets.UTF_8)
      tmpFile.renameTo(file)
    }
  }

  suspend fun load() {
    withContext(Dispatchers.IO) {
      if (file.exists()) {
        try {
          val json = file.readText(Charsets.UTF_8)
          val type = object : TypeToken<Map<String, DictEntry>>() {}.type
          val loaded: Map<String, DictEntry> = gson.fromJson(json, type)
          entries.clear()
          entries.putAll(loaded)
        } catch (_: Exception) {}
      }
    }
  }

  suspend fun clear() {
    entries.clear()
    withContext(Dispatchers.IO) {
      if (file.exists()) file.delete()
    }
  }
}
