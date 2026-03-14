package com.example.pdtranslator

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun TranslatorScreen(viewModel: TranslatorViewModel) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Main Language: ${viewModel.mainLanguage.value}")
        Text("Translation Language: ${viewModel.translationLanguage.value}")

        Button(onClick = { viewModel.getMissingTranslations() }) {
            Text("Find Missing Translations")
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(viewModel.missingTranslations.value) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text = it, modifier = Modifier.weight(1f))
                    Button(onClick = { viewModel.onTranslateClicked(it) }) {
                        Text("Translate")
                    }
                }
            }
        }
    }

    if (viewModel.showTranslationDialog.value) {
        TranslationDialog(
            translation = viewModel.selectedTranslation.value,
            onDismiss = viewModel::onDismissDialog
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslationDialog(translation: String, onDismiss: () -> Unit) {
    val translatedText = remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Translate: $translation")
                TextField(
                    value = translatedText.value,
                    onValueChange = { translatedText.value = it },
                    label = { Text("Translated Text") }
                )
                Button(onClick = onDismiss) {
                    Text("Save")
                }
            }
        }
    }
}
