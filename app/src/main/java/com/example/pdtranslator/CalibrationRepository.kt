package com.example.pdtranslator

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class CalibrationRepository(private val baseDir: File) {
    private val gson = Gson()
    private val file: File get() = File(baseDir, "calibrations.json")

    fun load(): CalibrationStore {
        if (!file.exists()) return CalibrationStore()
        return try {
            val json = file.readText(Charsets.UTF_8)
            val type = object : TypeToken<Map<String, CalibrationEntry>>() {}.type
            val map: Map<String, CalibrationEntry> = gson.fromJson(json, type) ?: emptyMap()
            CalibrationStore(LinkedHashMap(map))
        } catch (_: Exception) {
            CalibrationStore()
        }
    }

    fun save(store: CalibrationStore) {
        baseDir.mkdirs()
        file.writeText(gson.toJson(store.entries), Charsets.UTF_8)
    }

    fun exportJson(store: CalibrationStore): ByteArray {
        return gson.toJson(store.entries).toByteArray(Charsets.UTF_8)
    }

    fun importJson(json: String): Map<String, CalibrationEntry> {
        val type = object : TypeToken<Map<String, CalibrationEntry>>() {}.type
        return gson.fromJson(json, type) ?: emptyMap()
    }
}
