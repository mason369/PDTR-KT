package com.example.pdtranslator

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun TranslatorScreen(
    viewModel: TranslatorViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            viewModel.loadLanguageFilesFromZip(context.contentResolver, uri)
        }
    }

    val languageGroups by remember { mutableStateOf(viewModel.languageGroups) }
    val selectedGroup by viewModel.selectedGroup
    val sourceLanguage by viewModel.sourceLanguage
    val targetLanguage by viewModel.targetLanguage
    val translationResult by viewModel.translationResult

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(onClick = { launcher.launch("application/zip") }) {
            Text("导入ZIP")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Dropdown for Language Groups
        LanguageGroupSelector(groups = languageGroups, selected = selectedGroup, onSelected = { viewModel.selectGroup(it) })

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedGroup != null) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Dropdown for Source Language
                LanguageFileSelector(
                    label = "源语言",
                    files = selectedGroup!!.files,
                    selected = sourceLanguage,
                    onSelected = { viewModel.selectSourceLanguage(it) },
                    modifier = Modifier.weight(1f)
                )

                // Dropdown for Target Language
                LanguageFileSelector(
                    label = "目标语言",
                    files = selectedGroup!!.files,
                    selected = targetLanguage,
                    onSelected = { viewModel.selectTargetLanguage(it) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { viewModel.translate() }, enabled = sourceLanguage != null && targetLanguage != null) {
                Text("翻译")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = translationResult,
            onValueChange = {},
            readOnly = true,
            label = { Text("翻译结果") },
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        )
    }
}

@Composable
fun LanguageGroupSelector(groups: List<LanguageGroup>, selected: LanguageGroup?, onSelected: (LanguageGroup) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
        OutlinedTextField(
            value = selected?.baseName ?: "请选择语言组",
            onValueChange = {},
            readOnly = true,
            label = { Text("语言组") },
            trailingIcon = { Icon(Icons.Filled.ArrowDropDown, "Dropdown", modifier = Modifier.clickable { expanded = true }) },
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            groups.forEach { group ->
                DropdownMenuItem(text = { Text(group.baseName) }, onClick = {
                    onSelected(group)
                    expanded = false
                })
            }
        }
    }
}

@Composable
fun LanguageFileSelector(label: String, files: List<LanguageFile>, selected: LanguageFile?, onSelected: (LanguageFile) -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.wrapContentSize(Alignment.TopStart)) {
        OutlinedTextField(
            value = selected?.fileName ?: "请选择",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { Icon(Icons.Filled.ArrowDropDown, "Dropdown", modifier = Modifier.clickable { expanded = true }) },
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            files.forEach { file ->
                DropdownMenuItem(text = { Text(file.fileName) }, onClick = {
                    onSelected(file)
                    expanded = false
                })
            }
        }
    }
}
