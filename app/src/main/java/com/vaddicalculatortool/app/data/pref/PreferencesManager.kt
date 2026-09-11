package com.vaddicalculatortool.app.data.pref

import android.content.Context
import android.content.SharedPreferences
import com.vaddicalculatortool.app.domain.model.AppLanguage
import com.vaddicalculatortool.app.domain.model.AppThemeMode
import com.vaddicalculatortool.app.domain.model.CurrencyOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("vaddi_calculator_preferences", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(loadThemeMode())
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _language = MutableStateFlow(loadLanguage())
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _currency = MutableStateFlow(loadCurrency())
    val currency: StateFlow<CurrencyOption> = _currency.asStateFlow()

    private val _hasSelectedLanguage = MutableStateFlow(loadHasSelectedLanguage())
    val hasSelectedLanguage: StateFlow<Boolean> = _hasSelectedLanguage.asStateFlow()

    private fun loadThemeMode(): AppThemeMode {
        val name = prefs.getString(KEY_THEME_MODE, AppThemeMode.SYSTEM.name)
        return try {
            AppThemeMode.valueOf(name ?: AppThemeMode.SYSTEM.name)
        } catch (e: Exception) {
            AppThemeMode.SYSTEM
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
        _themeMode.value = mode
    }

    private fun loadLanguage(): AppLanguage {
        val code = prefs.getString(KEY_LANGUAGE, AppLanguage.ENGLISH.code)
        return AppLanguage.fromCode(code)
    }

    fun setLanguage(lang: AppLanguage) {
        prefs.edit().putString(KEY_LANGUAGE, lang.code).apply()
        _language.value = lang
    }

    private fun loadHasSelectedLanguage(): Boolean {
        return prefs.getBoolean(KEY_LANGUAGE_SELECTED, false)
    }

    fun setHasSelectedLanguage(selected: Boolean) {
        prefs.edit().putBoolean(KEY_LANGUAGE_SELECTED, selected).apply()
        _hasSelectedLanguage.value = selected
    }

    private fun loadCurrency(): CurrencyOption {
        val code = prefs.getString(KEY_CURRENCY, CurrencyOption.INR.code)
        return try {
            CurrencyOption.valueOf(code ?: CurrencyOption.INR.name)
        } catch (e: Exception) {
            CurrencyOption.INR
        }
    }

    fun setCurrency(currency: CurrencyOption) {
        prefs.edit().putString(KEY_CURRENCY, currency.name).apply()
        _currency.value = currency
    }

    companion object {
        private const val KEY_THEME_MODE = "pref_theme_mode"
        private const val KEY_LANGUAGE = "pref_language"
        private const val KEY_LANGUAGE_SELECTED = "pref_has_selected_language"
        private const val KEY_CURRENCY = "pref_currency"

        @Volatile
        private var INSTANCE: PreferencesManager? = null

        fun getInstance(context: Context): PreferencesManager {
            return INSTANCE ?: synchronized(this) {
                val instance = PreferencesManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
