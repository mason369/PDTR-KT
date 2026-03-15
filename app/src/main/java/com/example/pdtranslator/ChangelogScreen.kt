package com.example.pdtranslator

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

data class ChangelogItem(val version: String, val changes: List<String>)

@Composable
fun getChangelog(): List<ChangelogItem> {
    return listOf(
        ChangelogItem(stringResource(R.string.changelog_version_0_1_0), listOf(stringResource(R.string.changelog_content_0_1_0))),
        ChangelogItem(stringResource(R.string.changelog_version_1_0_0), listOf(stringResource(R.string.changelog_content_1_0_0))),
        ChangelogItem(stringResource(R.string.changelog_version_1_0_1), listOf(stringResource(R.string.changelog_content_1_0_1)))
    )
}

@Composable
fun ChangelogScreen() {
    val changelog = getChangelog()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = stringResource(id = R.string.changelog_title), style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(changelog) { item ->
                Text(text = item.version, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                item.changes.forEach { change ->
                    Text(text = "- $change", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 8.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
