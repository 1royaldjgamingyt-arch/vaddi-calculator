package com.vaddicalculatortool.app.ui.navigation

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vaddicalculatortool.app.domain.model.CalculatorMode
import com.vaddicalculatortool.app.domain.model.CurrencyOption
import com.vaddicalculatortool.app.ui.calculator.CalculatorScreen
import com.vaddicalculatortool.app.ui.calculator.CalculatorViewModel
import com.vaddicalculatortool.app.ui.calculator.DateCalculatorScreen
import com.vaddicalculatortool.app.ui.history.HistoryScreen
import com.vaddicalculatortool.app.ui.history.HistoryViewModel
import com.vaddicalculatortool.app.ui.settings.SettingsScreen
import com.vaddicalculatortool.app.ui.settings.SettingsViewModel

@Composable
fun MainScreen(
    calculatorViewModel: CalculatorViewModel,
    historyViewModel: HistoryViewModel,
    settingsViewModel: SettingsViewModel,
    currency: CurrencyOption,
    modifier: Modifier = Modifier
) {
    var currentDestination by rememberSaveable { mutableStateOf(AppNavDestination.QUICK_VADDI) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.navigationBars,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("main_bottom_nav")
            ) {
                AppNavDestination.values().forEach { destination ->
                    val isSelected = destination == currentDestination
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentDestination = destination },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = stringResource(destination.titleRes)
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(destination.titleRes),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag(destination.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(
                targetState = currentDestination,
                animationSpec = tween(220),
                label = "MainCrossfade"
            ) { dest ->
                when (dest) {
                    AppNavDestination.QUICK_VADDI -> {
                        CalculatorScreen(
                            viewModel = calculatorViewModel,
                            currency = currency
                        )
                    }
                    AppNavDestination.DATE_VADDI -> {
                        DateCalculatorScreen(
                            viewModel = calculatorViewModel,
                            currency = currency
                        )
                    }
                    AppNavDestination.HISTORY -> {
                        HistoryScreen(
                            viewModel = historyViewModel,
                            currency = currency,
                            onOpenCalculation = { item ->
                                calculatorViewModel.loadFromHistory(item)
                                currentDestination = if (item.calculatorMode == CalculatorMode.DATE_BASED.name) {
                                    AppNavDestination.DATE_VADDI
                                } else {
                                    AppNavDestination.QUICK_VADDI
                                }
                            },
                            onNavigateToCalculator = {
                                currentDestination = AppNavDestination.QUICK_VADDI
                            }
                        )
                    }
                    AppNavDestination.SETTINGS -> {
                        SettingsScreen(
                            viewModel = settingsViewModel
                        )
                    }
                }
            }
        }
    }
}
