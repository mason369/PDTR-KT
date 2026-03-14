package com.example.pdtranslator

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.pdtranslator.ui.theme.PDTranslatorTheme
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.zip.ZipInputStream

class MainActivity : ComponentActivity() {
    private val viewModel: TranslatorViewModel by viewModels()

    private val openLanguageGroupLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { loadLanguageGroupFromUri(it) }
    }

    private val saveLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) { uri ->
        uri?.let { saveModifiedContent(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        setContent {
            PDTranslatorTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainScreen(
                        viewModel = viewModel,
                        onSelectLanguageGroup = { selectLanguageGroup() },
                        onSave = { onSave() }
                    )
                }
            }
        }
    }

    private fun selectLanguageGroup() {
        openLanguageGroupLauncher.launch(arrayOf("application/zip"))
    }

    private fun loadLanguageGroupFromUri(uri: Uri) {
        val fileContents = unzipAndReadProperties(uri)
        if (fileContents.isNotEmpty()) {
            viewModel.loadLanguageGroup(fileContents)
        }
    }

    private fun unzipAndReadProperties(uri: Uri): Map<String, String> {
        val fileContents = mutableMapOf<String, String>()
        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                ZipInputStream(inputStream).use { zipInputStream ->
                    var entry = zipInputStream.nextEntry
                    while (entry != null) {
                        if (!entry.isDirectory && entry.name.endsWith(".properties")) {
                            val content = zipInputStream.bufferedReader().readText()
                            fileContents[entry.name] = content
                        }
                        entry = zipInputStream.nextEntry
                    }
                }
            }
        } catch (e: Exception) {
            // Handle exceptions, e.g., show a toast to the user
        }
        return fileContents
    }


    private fun onSave() {
        val targetLanguage = viewModel.targetLanguage.value ?: return
        val fileName = targetLanguage.fileName
        saveLauncher.launch(fileName)
    }

    private fun saveModifiedContent(uri: Uri) {
        val content = viewModel.getModifiedContentForTarget() ?: return
        try {
            contentResolver.openOutputStream(uri)?.use {
                OutputStreamWriter(it).use {
                    writer -> writer.write(content)
                }
            }
        } catch (e: Exception) {
            // Handle exceptions
        }
    }
}
