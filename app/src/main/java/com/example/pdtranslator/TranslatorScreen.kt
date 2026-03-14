package com.example.pdtranslator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pdtranslator.ui.theme.PDTranslatorTheme

@Composable
fun TranslatorScreen(
    viewModel: TranslatorViewModel,
    onSelectOriginal: () -> Unit,
    onSelectTranslated: () -> Unit,
    modifier: Modifier = Modifier
) {
    val translationProgress by viewModel.translationProgress
    val searchQuery by viewModel.searchQuery
    val filterOption by viewModel.filterOption
    val filteredItems by viewModel.filteredItems

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChange(it) },
            label = { Text("搜索 (Key)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            FilterChip(text = "所有", selected = filterOption == FilterOption.ALL) { viewModel.onFilterChange(FilterOption.ALL) }
            FilterChip(text = "未翻译", selected = filterOption == FilterOption.UNTRANSLATED) { viewModel.onFilterChange(FilterOption.UNTRANSLATED) }
            FilterChip(text = "已改动", selected = filterOption == FilterOption.MODIFIED) { viewModel.onFilterChange(FilterOption.MODIFIED) }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredItems.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "翻译进度")
                Text(text = "${(translationProgress * 100).toInt()}%")
            }
            LinearProgressIndicator(
                progress = translationProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }

        BoxWithConstraints {
            if (maxWidth > 600.dp) { // Large screen: Side-by-side card layout
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(onClick = onSelectOriginal, modifier = Modifier.weight(1f)) { Text("导入原文文件") }
                        Button(onClick = onSelectTranslated, modifier = Modifier.weight(1f)) { Text("导入译文文件") }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        itemsIndexed(filteredItems) { index, item ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Text(
                                        text = "$index - ${item.key}",
                                        modifier = Modifier.weight(1f),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    OutlinedTextField(
                                        value = item.translation,
                                        onValueChange = { viewModel.updateTranslation(item.key, it) },
                                        label = { Text("Value") },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            } else { // Small screen: Vertical card layout
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Button(onClick = onSelectOriginal) { Text("选择原文文件") }
                        Button(onClick = onSelectTranslated) { Text("选择译文文件") }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    if (filteredItems.isNotEmpty()) {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            itemsIndexed(filteredItems) { index, item ->
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(text = "${index + 1}. ${item.key}", style = MaterialTheme.typography.titleMedium)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(text = "原文: ${item.original}", style = MaterialTheme.typography.bodyMedium)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedTextField(
                                            value = item.translation,
                                            onValueChange = { newTranslation ->
                                                viewModel.updateTranslation(item.key, newTranslation)
                                            },
                                            label = { Text("译文") },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Text(text = text, style = MaterialTheme.typography.bodySmall)
    }
}

@Preview(showBackground = true, widthDp = 800)
@Composable
fun TranslatorScreenPreviewLarge() {
    val viewModel = TranslatorViewModel()
    val originalContent = "key1=Hello\nkey2=World\nkey3=Another long key for testing purposes to see how it wraps"
    val translatedContent = "key1=你好\nkey2="
    viewModel.loadTranslations(originalContent, translatedContent)
    PDTranslatorTheme {
        TranslatorScreen(viewModel, onSelectOriginal = {}, onSelectTranslated = {})
    }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
fun TranslatorScreenPreviewSmall() {
    val viewModel = TranslatorViewModel()
    val originalContent = "key1=Hello\nkey2=World"
    val translatedContent = "key1=你好\nkey2="
    viewModel.loadTranslations(originalContent, translatedContent)
    PDTranslatorTheme {
        TranslatorScreen(viewModel, onSelectOriginal = {}, onSelectTranslated = {})
    }
}
