package com.vaddicalculator.app

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vaddicalculator.app.domain.model.AppThemeMode
import com.vaddicalculator.app.ui.calculator.CalculatorViewModel
import com.vaddicalculator.app.ui.history.HistoryViewModel
import com.vaddicalculator.app.ui.language.ChooseLanguageScreen
import com.vaddicalculator.app.ui.navigation.MainScreen
import com.vaddicalculator.app.ui.settings.SettingsViewModel
import com.vaddicalculator.app.ui.theme.VaddiCalculatorTheme
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val calculatorViewModel: CalculatorViewModel by viewModels()
    private val historyViewModel: HistoryViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by settingsViewModel.themeMode.collectAsStateWithLifecycle()
            val language by settingsViewModel.language.collectAsStateWithLifecycle()
            val currency by settingsViewModel.currency.collectAsStateWithLifecycle()
            val hasSelectedLanguage by settingsViewModel.hasSelectedLanguage.collectAsStateWithLifecycle()

            val isDarkTheme = when (themeMode) {
                AppThemeMode.SYSTEM -> isSystemInDarkTheme()
                AppThemeMode.LIGHT -> false
                AppThemeMode.DARK -> true
            }

            // Localized context & configuration for real-time in-app language switching
            val currentLocale = remember(language) { Locale.forLanguageTag(language.code) }
            val currentConfig = LocalConfiguration.current
            val baseContext = LocalContext.current

            val localizedConfiguration = remember(language, currentConfig) {
                Configuration(currentConfig).apply {
                    setLocale(currentLocale)
                    setLayoutDirection(currentLocale)
                }
            }

            val localizedContext = remember(language, baseContext) {
                baseContext.createConfigurationContext(localizedConfiguration)
            }

            CompositionLocalProvider(
                LocalConfiguration provides localizedConfiguration,
                LocalContext provides localizedContext
            ) {
                VaddiCalculatorTheme(darkTheme = isDarkTheme) {
                    Crossfade(
                        targetState = hasSelectedLanguage,
                        animationSpec = tween(300),
                        label = "FirstLaunchCrossfade"
                    ) { isConfigured ->
                        if (!isConfigured) {
                            ChooseLanguageScreen(
                                currentSelectedLanguage = language,
                                onLanguageConfirmed = { selectedLang ->
                                    settingsViewModel.completeLanguageSelection(selectedLang)
                                }
                            )
                        } else {
                            MainScreen(
                                calculatorViewModel = calculatorViewModel,
                                historyViewModel = historyViewModel,
                                settingsViewModel = settingsViewModel,
                                currency = currency
                            )
                        }
                    }
                }
            }
        }
    }
}
