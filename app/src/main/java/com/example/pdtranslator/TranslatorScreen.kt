package com.example.pdtranslator

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslatorScreen(viewModel: TranslatorViewModel) {
    // Dummy state for now, will be replaced with ViewModel state
    var sourceLang by remember { mutableStateOf("ZH") }
    var targetLang by remember { mutableStateOf("EL") }
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    val filterOptions = listOf("所有", "未翻译", "已改动")
    var selectedFilter by remember { mutableStateOf(filterOptions[0]) }
    val translationProgress by remember { mutableStateOf(0.08f) }
    val currentPage by remember { mutableStateOf(7) }
    val totalPages by remember { mutableStateOf(20) }

    val translationItems = remember {
        listOf(
            TranslationItemData(
                key = "ui.changelist.mlpd.vm0_5_x_changes.icescorpiologs",
                originalText = "原文:来自于恶魔深处的扭曲生物,请小心行事。",
                translatedText = ""
            ),
            TranslationItemData(
                key = "ui.changelist.mlpd.vm0_5_x_changes.icewandgod",
                originalText = "原文:新法杖:霜冻神级法杖",
                translatedText = ""
            )
        )
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { /* Handle import */ }) {
                Text("导入语言组")
            }
            Button(onClick = { /* Handle save */ }, enabled = false) {
                Text("保存")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            LanguageSelector(
                label = "源语言",
                selectedLanguage = sourceLang,
                onLanguageSelected = { sourceLang = it },
                modifier = Modifier.weight(1f)
            )
            LanguageSelector(
                label = "目标语言",
                selectedLanguage = targetLang,
                onLanguageSelected = { targetLang = it },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("搜索 (Key或原文)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            filterOptions.forEach { option ->
                Row(
                    Modifier
                        .selectable(
                            selected = (option == selectedFilter),
                            onClick = { selectedFilter = option }
                        )
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (option == selectedFilter),
                        onClick = { selectedFilter = option }
                    )
                    Text(text = option)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column {
            Text("翻译进度")
            LinearProgressIndicator(
                progress = translationProgress,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "${(translationProgress * 100).toInt()}%",
                modifier = Modifier.align(Alignment.End)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f), // This is the fix!
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(translationItems) { item ->
                TranslationCard(item = item)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { /* prev page */ }) {
                Text("上一页")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("第 $currentPage / $totalPages 页")
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = { /* next page */ }) {
                Text("下一页")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelector(
    label: String,
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val languages = listOf("ZH", "EL", "EN", "FR", "DE") // Dummy data

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedLanguage,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            languages.forEach { language ->
                DropdownMenuItem(
                    text = { Text(language) },
                    onClick = {
                        onLanguageSelected(language)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun TranslationCard(item: TranslationItemData) {
    var translatedText by remember { mutableStateOf(TextFieldValue(item.translatedText)) }
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(item.key, style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(item.originalText, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = translatedText,
                onValueChange = { translatedText = it },
                label = { Text("译文") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

data class TranslationItemData(
    val key: String,
    val originalText: String,
    val translatedText: String
)

@Preview(showBackground = true)
@Composable
fun TranslatorScreenPreview() {
    // Previewing this complex screen requires a mock ViewModel and state,
    // which is beyond the scope of this refactoring.
}
