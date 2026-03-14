package com.example.pdtranslator

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class TranslatorViewModel : ViewModel() {
    val mainLanguage = mutableStateOf("English")
    val translationLanguage = mutableStateOf("Spanish")
    val missingTranslations = mutableStateOf<List<String>>(emptyList())
    val showTranslationDialog = mutableStateOf(false)
    val selectedTranslation = mutableStateOf("")

    fun getMissingTranslations() {
        // In a real application, you would read the files and compare them
        // to find the missing translations.
        missingTranslations.value = listOf("hello", "world", "goodbye")
    }

    fun onTranslateClicked(translation: String) {
        selectedTranslation.value = translation
        showTranslationDialog.value = true
    }

    fun onDismissDialog() {
        showTranslationDialog.value = false
    }
}
