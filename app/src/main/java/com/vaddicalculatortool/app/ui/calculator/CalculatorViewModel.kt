package com.vaddicalculatortool.app.ui.calculator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vaddicalculatortool.app.data.local.AppDatabase
import com.vaddicalculatortool.app.data.local.CalculationHistoryEntity
import com.vaddicalculatortool.app.data.local.CalculationHistoryRepository
import com.vaddicalculatortool.app.data.pref.PreferencesManager
import com.vaddicalculatortool.app.domain.engine.VaddiCalculationEngine
import com.vaddicalculatortool.app.domain.model.CalculationResult
import com.vaddicalculatortool.app.domain.model.CalculatorMode
import com.vaddicalculatortool.app.domain.model.DateCalculationInput
import com.vaddicalculatortool.app.domain.model.InterestScheme
import com.vaddicalculatortool.app.domain.model.InterestType
import com.vaddicalculatortool.app.domain.model.QuickCalculationInput
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class CalculatorUiState(
    val mode: CalculatorMode = CalculatorMode.QUICK,
    val scheme: InterestScheme = InterestScheme.SIMPLE,
    val principalInput: String = "100000",
    val rateInput: String = "2.0",
    val interestType: InterestType = InterestType.MONTHLY,
    val durationInput: String = "6",
    val startDate: LocalDate = LocalDate.now().minusMonths(6),
    val endDate: LocalDate = LocalDate.now(),
    val principalErrorRes: Int? = null,
    val rateErrorRes: Int? = null,
    val durationErrorRes: Int? = null,
    val dateErrorRes: Int? = null,
    val principalError: String? = null,
    val rateError: String? = null,
    val durationError: String? = null,
    val dateError: String? = null,
    val calculationResult: CalculationResult? = null,
    val isSaved: Boolean = false,
    val saveMessage: String? = null
)

class CalculatorViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: CalculationHistoryRepository =
        CalculationHistoryRepository(AppDatabase.getDatabase(application).calculationHistoryDao())
    private val preferencesManager: PreferencesManager =
        PreferencesManager.getInstance(application)

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    init {
        // Initial calculation with standard Indian ₹2/month vaddi values
        calculate()
    }

    fun onModeChanged(mode: CalculatorMode) {
        _uiState.update { it.copy(mode = mode, calculationResult = null, isSaved = false) }
        calculate()
    }

    fun onSchemeChanged(scheme: InterestScheme) {
        _uiState.update { it.copy(scheme = scheme, isSaved = false) }
        calculate()
    }

    fun onPrincipalChanged(value: String) {
        _uiState.update { it.copy(principalInput = value, principalErrorRes = null, principalError = null, isSaved = false) }
    }

    fun onRateChanged(value: String) {
        _uiState.update { it.copy(rateInput = value, rateErrorRes = null, rateError = null, isSaved = false) }
    }

    fun onDurationChanged(value: String) {
        _uiState.update { it.copy(durationInput = value, durationErrorRes = null, durationError = null, isSaved = false) }
    }

    fun onInterestTypeChanged(type: InterestType) {
        _uiState.update { it.copy(interestType = type, isSaved = false) }
    }

    fun onStartDateChanged(date: LocalDate) {
        _uiState.update { current ->
            val errorRes = if (current.endDate.isBefore(date)) com.vaddicalculatortool.app.R.string.err_date_order else null
            current.copy(startDate = date, dateErrorRes = errorRes, isSaved = false)
        }
    }

    fun onEndDateChanged(date: LocalDate) {
        _uiState.update { current ->
            val errorRes = if (date.isBefore(current.startDate)) com.vaddicalculatortool.app.R.string.err_date_order else null
            current.copy(endDate = date, dateErrorRes = errorRes, isSaved = false)
        }
    }

    fun calculate() {
        val state = _uiState.value

        var hasError = false
        var principalErrorRes: Int? = null
        var rateErrorRes: Int? = null
        var durationErrorRes: Int? = null
        var dateErrorRes: Int? = null

        val cleanPrincipal = state.principalInput.replace(",", "").trim()
        val cleanRate = state.rateInput.replace(",", "").trim()
        val cleanDuration = state.durationInput.replace(",", "").trim()

        val principal = cleanPrincipal.toDoubleOrNull()
        if (cleanPrincipal.isEmpty()) {
            principalErrorRes = com.vaddicalculatortool.app.R.string.err_principal_empty
            hasError = true
        } else if (principal == null || principal.isNaN() || principal.isInfinite() || principal <= 0.0) {
            principalErrorRes = com.vaddicalculatortool.app.R.string.err_principal_invalid
            hasError = true
        }

        val rate = cleanRate.toDoubleOrNull()
        if (cleanRate.isEmpty()) {
            rateErrorRes = com.vaddicalculatortool.app.R.string.err_rate_empty
            hasError = true
        } else if (rate == null || rate.isNaN() || rate.isInfinite() || rate <= 0.0) {
            rateErrorRes = com.vaddicalculatortool.app.R.string.err_rate_invalid
            hasError = true
        }

        if (state.mode == CalculatorMode.QUICK) {
            val duration = cleanDuration.toDoubleOrNull()
            if (cleanDuration.isEmpty()) {
                durationErrorRes = com.vaddicalculatortool.app.R.string.err_duration_empty
                hasError = true
            } else if (duration == null || duration.isNaN() || duration.isInfinite() || duration <= 0.0) {
                durationErrorRes = com.vaddicalculatortool.app.R.string.err_duration_invalid
                hasError = true
            }

            if (hasError) {
                _uiState.update {
                    it.copy(
                        principalErrorRes = principalErrorRes,
                        rateErrorRes = rateErrorRes,
                        durationErrorRes = durationErrorRes,
                        calculationResult = null
                    )
                }
                return
            }

            val result = VaddiCalculationEngine.calculateQuick(
                QuickCalculationInput(
                    principal = principal!!,
                    rate = rate!!,
                    interestType = state.interestType,
                    duration = duration!!,
                    scheme = state.scheme
                )
            )

            _uiState.update {
                it.copy(
                    principalErrorRes = null,
                    rateErrorRes = null,
                    durationErrorRes = null,
                    principalError = null,
                    rateError = null,
                    durationError = null,
                    calculationResult = result,
                    isSaved = false
                )
            }
        } else {
            if (state.endDate.isBefore(state.startDate)) {
                dateErrorRes = com.vaddicalculatortool.app.R.string.err_date_order
                hasError = true
            }

            if (hasError) {
                _uiState.update {
                    it.copy(
                        principalErrorRes = principalErrorRes,
                        rateErrorRes = rateErrorRes,
                        dateErrorRes = dateErrorRes,
                        calculationResult = null
                    )
                }
                return
            }

            val result = VaddiCalculationEngine.calculateDateBased(
                DateCalculationInput(
                    principal = principal!!,
                    rate = rate!!,
                    interestType = state.interestType,
                    startDate = state.startDate,
                    endDate = state.endDate,
                    scheme = state.scheme
                )
            )

            _uiState.update {
                it.copy(
                    principalErrorRes = null,
                    rateErrorRes = null,
                    dateErrorRes = null,
                    principalError = null,
                    rateError = null,
                    dateError = null,
                    calculationResult = result,
                    isSaved = false
                )
            }
        }
    }

    fun dismissResult() {
        _uiState.update { it.copy(calculationResult = null) }
    }

    fun clear() {
        _uiState.update {
            it.copy(
                principalInput = "",
                rateInput = "",
                durationInput = "",
                principalErrorRes = null,
                rateErrorRes = null,
                durationErrorRes = null,
                dateErrorRes = null,
                principalError = null,
                rateError = null,
                durationError = null,
                dateError = null,
                calculationResult = null,
                isSaved = false
            )
        }
    }

    fun saveToHistory() {
        val result = _uiState.value.calculationResult ?: return
        if (_uiState.value.isSaved) return

        viewModelScope.launch {
            val entity = CalculationHistoryEntity(
                principal = result.principal,
                rate = result.rate,
                interestType = result.interestType.name,
                calculatorMode = result.mode.name,
                interestScheme = result.scheme.name,
                durationText = result.durationDescription,
                monthlyInterest = result.monthlyInterest,
                startDate = result.startDate?.toString(),
                endDate = result.endDate?.toString(),
                interestEarned = result.interestEarned,
                totalAmount = result.totalAmount,
                currencyCode = preferencesManager.currency.value.code,
                formulaUsed = result.formulaUsed
            )
            repository.insert(entity)
            _uiState.update { it.copy(isSaved = true) }
        }
    }

    fun loadFromHistory(item: CalculationHistoryEntity) {
        val parsedType = try {
            InterestType.valueOf(item.interestType)
        } catch (e: Exception) {
            InterestType.MONTHLY
        }

        val parsedMode = try {
            CalculatorMode.valueOf(item.calculatorMode)
        } catch (e: Exception) {
            CalculatorMode.QUICK
        }

        val parsedScheme = try {
            InterestScheme.valueOf(item.interestScheme)
        } catch (e: Exception) {
            InterestScheme.SIMPLE
        }

        val durationVal = Regex("([0-9]+(?:\\.[0-9]+)?)").find(item.durationText)?.value ?: "1"

        _uiState.update {
            it.copy(
                mode = parsedMode,
                scheme = parsedScheme,
                principalInput = item.principal.toString().removeSuffix(".0"),
                rateInput = item.rate.toString().removeSuffix(".0"),
                interestType = parsedType,
                durationInput = durationVal,
                startDate = item.startDate?.let { d -> LocalDate.parse(d) } ?: it.startDate,
                endDate = item.endDate?.let { d -> LocalDate.parse(d) } ?: it.endDate,
                principalErrorRes = null,
                rateErrorRes = null,
                durationErrorRes = null,
                dateErrorRes = null,
                principalError = null,
                rateError = null,
                durationError = null,
                dateError = null
            )
        }
        calculate()
    }
}
