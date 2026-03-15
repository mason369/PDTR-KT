
package com.example.pdtranslator

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

object AppDestinations {
    const val MAIN_SCREEN = "main"
    const val DEPENDENCY_SCREEN = "dependencies"
}

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    val viewModel: TranslatorViewModel = viewModel()

    NavHost(navController = navController, startDestination = AppDestinations.MAIN_SCREEN) {
        composable(AppDestinations.MAIN_SCREEN) {
            MainScreen(
                viewModel = viewModel,
                onNavigateToDependencies = { navController.navigate(AppDestinations.DEPENDENCY_SCREEN) }
            )
        }
        composable(AppDestinations.DEPENDENCY_SCREEN) {
            DependencyScreen(onNavigateUp = { navController.popBackStack() })
        }
    }
}
