package com.vaddicalculatortool.app.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vaddicalculatortool.app.data.local.AppDatabase
import com.vaddicalculatortool.app.data.local.CalculationHistoryRepository
import com.vaddicalculatortool.app.data.pref.PreferencesManager
import com.vaddicalculatortool.app.domain.model.AppLanguage
import com.vaddicalculatortool.app.domain.model.AppThemeMode
import com.vaddicalculatortool.app.domain.model.CurrencyOption
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val preferencesManager = PreferencesManager.getInstance(application)
    private val historyRepository = CalculationHistoryRepository(
        AppDatabase.getDatabase(application).calculationHistoryDao()
    )

    val themeMode: StateFlow<AppThemeMode> = preferencesManager.themeMode
    val language: StateFlow<AppLanguage> = preferencesManager.language
    val currency: StateFlow<CurrencyOption> = preferencesManager.currency
    val hasSelectedLanguage: StateFlow<Boolean> = preferencesManager.hasSelectedLanguage

    fun setThemeMode(mode: AppThemeMode) {
        preferencesManager.setThemeMode(mode)
    }

    fun setLanguage(language: AppLanguage) {
        preferencesManager.setLanguage(language)
    }

    fun completeLanguageSelection(language: AppLanguage) {
        preferencesManager.setLanguage(language)
        preferencesManager.setHasSelectedLanguage(true)
    }

    fun setCurrency(currency: CurrencyOption) {
        preferencesManager.setCurrency(currency)
    }

    fun clearHistory(onCleared: () -> Unit) {
        viewModelScope.launch {
            historyRepository.clearAll()
            onCleared()
        }
    }
}
