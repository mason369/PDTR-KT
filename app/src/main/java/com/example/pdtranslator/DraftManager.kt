package com.example.pdtranslator

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.security.MessageDigest

data class DraftData(
  val groupName: String,
  val sourceLangCode: String,
  val targetLangCode: String,
  val stagedChanges: Map<String, String>,
  val highlightKeywords: Set<String>,
  val entryCount: Int,
  val keysDigest: String,
  val timestamp: Long
)

enum class DraftValidation {
  MATCH,
  MISMATCH,
  NO_DRAFT
}

class DraftManager(private val context: Context) {

  private val gson = Gson()
  private val file: File get() = File(context.filesDir, "draft.json")

  suspend fun save(draft: DraftData) {
    withContext(Dispatchers.IO) {
      val json = gson.toJson(draft)
      val tmpFile = File(context.filesDir, "draft.json.tmp")
      tmpFile.writeText(json, Charsets.UTF_8)
      tmpFile.renameTo(file)
    }
  }

  suspend fun load(): DraftData? {
    return withContext(Dispatchers.IO) {
      if (!file.exists()) return@withContext null
      try {
        val json = file.readText(Charsets.UTF_8)
        gson.fromJson(json, DraftData::class.java)
      } catch (_: Exception) {
        null
      }
    }
  }

  suspend fun delete() {
    withContext(Dispatchers.IO) {
      file.delete()
    }
  }

  fun exists(): Boolean = file.exists()

  companion object {
    fun computeKeysDigest(keys: List<String>): String {
      val md = MessageDigest.getInstance("SHA-256")
      keys.sorted().forEach { key ->
        md.update(key.toByteArray(Charsets.UTF_8))
      }
      return md.digest().joinToString("") { "%02x".format(it) }
    }

    fun validate(draft: DraftData, currentEntryCount: Int, currentKeysDigest: String): DraftValidation {
      return if (draft.entryCount == currentEntryCount && draft.keysDigest == currentKeysDigest) {
        DraftValidation.MATCH
      } else {
        DraftValidation.MISMATCH
      }
    }
  }
}
