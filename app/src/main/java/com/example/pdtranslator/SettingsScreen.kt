package com.example.pdtranslator

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(viewModel: TranslatorViewModel) { // Simplified
    val (isCustomKeywordsEnabled, setCustomKeywordsEnabled) = remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("设置", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("翻译引擎", style = MaterialTheme.typography.titleLarge)
                // Placeholder for translation engine selection
                Text("（功能待实现）", style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("自定义关键词高亮", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                    Switch(
                        checked = isCustomKeywordsEnabled,
                        onCheckedChange = setCustomKeywordsEnabled
                    )
                }
                if (isCustomKeywordsEnabled) {
                     // Placeholder for custom keyword input
                    Text("（功能待实现）", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}