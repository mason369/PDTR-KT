package com.example.pdtranslator

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.zip.ZipInputStream

// Data classes for better organization
data class LanguageFile(val fileName: String, val languageCode: String, val content: String)
data class LanguageGroup(val baseName: String, val files: List<LanguageFile>)

class TranslatorViewModel : ViewModel() {
    val languageGroups = mutableStateListOf<LanguageGroup>()
    val selectedGroup = mutableStateOf<LanguageGroup?>(null)
    val sourceLanguage = mutableStateOf<LanguageFile?>(null)
    val targetLanguage = mutableStateOf<LanguageFile?>(null)

    val translationResult = mutableStateOf("")

    // This property derives the list of group names from the languageGroups list.
    // It is a State object, so Compose can react to its changes.
    val languageGroupNames: State<List<String>> = derivedStateOf {
        languageGroups.map { it.baseName }
    }

    fun loadLanguageFilesFromZip(contentResolver: ContentResolver, zipFileUri: Uri) {
        val rawFiles = mutableMapOf<String, String>()
        try {
            contentResolver.openInputStream(zipFileUri)?.use { inputStream ->
                ZipInputStream(inputStream).use { zipInputStream ->
                    var entry = zipInputStream.nextEntry
                    while (entry != null) {
                        if (!entry.isDirectory) {
                            // Store the full path to handle nested directories
                            val fileName = entry.name
                            val fileContent = BufferedReader(InputStreamReader(zipInputStream)).readText()
                            rawFiles[fileName] = fileContent
                        }
                        entry = zipInputStream.nextEntry
                    }
                }
            }
            processLanguageFiles(rawFiles)
        } catch (e: Exception) {
            // Handle exceptions - maybe update a state to show an error
        }
    }

    private fun processLanguageFiles(rawFiles: Map<String, String>) {
        val groupedFiles = mutableMapOf<String, MutableList<LanguageFile>>()

        for ((fullPath, content) in rawFiles) {
            val fileName = fullPath.substringAfterLast('/')
            val baseName = fileName.substringBeforeLast('_')
            val languageCode = fileName.substringAfterLast('_').substringBefore('.')

            if (baseName.isNotBlank() && languageCode.isNotBlank()) {
                val languageFile = LanguageFile(fileName, languageCode, content)
                groupedFiles.getOrPut(baseName) { mutableListOf() }.add(languageFile)
            }
        }

        val newLanguageGroups = groupedFiles.map { (baseName, files) ->
            LanguageGroup(baseName, files)
        }

        languageGroups.clear()
        languageGroups.addAll(newLanguageGroups)

        // Reset selections
        selectedGroup.value = null
        sourceLanguage.value = null
        targetLanguage.value = null
    }

    fun selectGroup(group: LanguageGroup) {
        selectedGroup.value = group
        // Reset language selections when a new group is selected
        sourceLanguage.value = null
        targetLanguage.value = null
    }

    // This function allows selecting a group by its name, as required by LanguageGroupScreen.
    fun selectLanguageGroup(groupName: String) {
        val group = languageGroups.find { it.baseName == groupName }
        group?.let { selectGroup(it) }
    }


    fun selectSourceLanguage(file: LanguageFile) {
        sourceLanguage.value = file
    }

    fun selectTargetLanguage(file: LanguageFile) {
        targetLanguage.value = file
    }

    fun translate() {
        // Placeholder for translation logic
        val source = sourceLanguage.value
        val target = targetLanguage.value
        if (source != null && target != null) {
            // Simple placeholder logic
            translationResult.value = "Translated from ${source.fileName} to ${target.fileName}:\n\n${target.content}"
        } else {
            translationResult.value = "Please select source and target languages."
        }
    }
}
