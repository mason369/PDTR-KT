package com.example.pdtranslator

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import java.io.StringReader
import java.util.Properties

data class TranslationItem(
    val key: String,
    val original: String,
    val translation: String,
    val isTranslated: Boolean
)

class TranslatorViewModel : ViewModel() {

    val translationItems = mutableStateOf<List<TranslationItem>>(emptyList())
    val translationProgress = mutableStateOf(0f)

    fun loadTranslations(originalContent: String, translatedContent: String) {
        val originalProps = Properties()
        originalProps.load(StringReader(originalContent))

        val translatedProps = Properties()
        translatedProps.load(StringReader(translatedContent))

        val items = mutableListOf<TranslationItem>()
        var translatedCount = 0

        originalProps.stringPropertyNames().forEach { key ->
            val originalValue = originalProps.getProperty(key) ?: ""
            val translatedValue = translatedProps.getProperty(key) ?: ""
            val isTranslated = translatedValue.isNotEmpty() && translatedValue != originalValue

            if (isTranslated) {
                translatedCount++
            }

            items.add(TranslationItem(key, originalValue, translatedValue, isTranslated))
        }

        translationItems.value = items
        if (originalProps.size > 0) {
            translationProgress.value = translatedCount.toFloat() / originalProps.size
        }
    }
}
