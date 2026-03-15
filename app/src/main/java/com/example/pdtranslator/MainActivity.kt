
package com.example.pdtranslator

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.pdtranslator.ui.theme.PDTranslatorTheme
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        setContent {
            PDTranslatorTheme {
                AppNavigator()
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val sharedPreferences = newBase.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val lang = sharedPreferences.getString("language", "en") ?: "en"
        val locale = Locale(lang)
        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    fun setLocale(lang: String) {
        val sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("language", lang)
            apply()
        }
        recreate()
    }
}
