
package com.example.pdtranslator

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class Library(
    val name: String,
    val description: String,
    val license: String,
    val version: String,
    val url: String
)

@Composable
fun getLibraries(): List<Library> {
    return listOf(
        Library(stringResource(R.string.lib_kotlin_name), stringResource(R.string.lib_kotlin_desc), "Apache License 2.0", "1.9.22", "https://github.com/JetBrains/kotlin"),
        Library(stringResource(R.string.lib_crash_name), stringResource(R.string.lib_crash_desc), "Apache License 2.0", "2.4.0", "https://github.com/Ereza/CustomActivityOnCrash"),
        Library(stringResource(R.string.lib_nav_compose_name), stringResource(R.string.lib_nav_compose_desc), "Apache License 2.0", "2.7.7", "https://developer.android.com/jetpack/androidx/releases/navigation"),
        Library(stringResource(R.string.lib_core_ktx_name), stringResource(R.string.lib_core_ktx_desc), "Apache License 2.0", "1.12.0", "https://developer.android.com/jetpack/androidx/releases/core"),
        Library(stringResource(R.string.lib_splashscreen_name), stringResource(R.string.lib_splashscreen_desc), "Apache License 2.0", "1.0.1", "https://developer.android.com/jetpack/androidx/releases/core"),
        Library(stringResource(R.string.lib_lifecycle_name), stringResource(R.string.lib_lifecycle_desc), "Apache License 2.0", "2.6.2", "https://developer.android.com/jetpack/androidx/releases/lifecycle"),
        Library(stringResource(R.string.lib_activity_compose_name), stringResource(R.string.lib_activity_compose_desc), "Apache License 2.0", "1.8.2", "https://developer.android.com/jetpack/androidx/releases/activity"),
        Library(stringResource(R.string.lib_compose_name), stringResource(R.string.lib_compose_desc), "Apache License 2.0", "2023.08.00", "https://developer.android.com/jetpack/compose"),
        Library(stringResource(R.string.lib_accompanist_name), stringResource(R.string.lib_accompanist_desc), "Apache License 2.0", "0.28.0", "https://github.com/google/accompanist")
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DependencyScreen(onNavigateUp: () -> Unit) {
    val libraries = getLibraries()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.source_code_license_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.back_button_description))
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(libraries) { library ->
                DependencyItem(library = library)
            }
        }
    }
}

@Composable
fun DependencyItem(library: Library) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { 
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(library.url))
                context.startActivity(intent)
             }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(library.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(library.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("${library.license} - v${library.version}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.width(16.dp))
        IconButton(onClick = { 
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(library.url))
            context.startActivity(intent)
        }) {
            Icon(Icons.Default.Link, contentDescription = stringResource(id = R.string.visit_link_description))
        }
    }
}
