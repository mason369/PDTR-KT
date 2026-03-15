
package com.example.pdtranslator

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

object AppDestinations {
    const val MAIN_SCREEN = "main"
    const val DEPENDENCY_SCREEN = "dependencies"
    const val CHANGELOG_SCREEN = "changelog"
}

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    val viewModel: TranslatorViewModel = viewModel()
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = AppDestinations.MAIN_SCREEN) {
        composable(AppDestinations.MAIN_SCREEN) {
             MainScreen(
                viewModel = viewModel,
                onNavigateToDependencies = { navController.navigate(AppDestinations.DEPENDENCY_SCREEN) },
                onNavigateToChangelog = { navController.navigate(AppDestinations.CHANGELOG_SCREEN) },
                onLanguageSelected = { lang ->
                    (context as? MainActivity)?.setLocale(lang)
                }
            )
        }
        composable(AppDestinations.DEPENDENCY_SCREEN) {
            DependencyScreen(onNavigateUp = { navController.popBackStack() })
        }
        composable(AppDestinations.CHANGELOG_SCREEN) {
            ChangelogScreen()
        }
    }
}
