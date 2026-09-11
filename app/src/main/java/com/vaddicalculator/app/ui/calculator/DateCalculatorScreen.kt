package com.vaddicalculator.app.ui.calculator

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vaddicalculator.app.R
import com.vaddicalculator.app.domain.model.CalculatorMode
import com.vaddicalculator.app.domain.model.CurrencyOption
import com.vaddicalculator.app.domain.model.InterestType
import com.vaddicalculator.app.domain.util.CurrencyFormatter
import com.vaddicalculator.app.domain.util.DateUtils
import com.vaddicalculator.app.ui.components.AdBannerPlaceholder
import com.vaddicalculator.app.ui.components.CurrencyInputField
import com.vaddicalculator.app.ui.components.DateSelectorField
import com.vaddicalculator.app.ui.components.IndianNumberVisualTransformation
import com.vaddicalculator.app.ui.components.InterestTypeSelector
import com.vaddicalculator.app.ui.components.ResultSection
import com.vaddicalculator.app.ui.components.SchemeSelector
import kotlinx.coroutines.launch

@Composable
fun DateCalculatorScreen(
    viewModel: CalculatorViewModel,
    currency: CurrencyOption,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val indianNumberVisualTransformation = remember { IndianNumberVisualTransformation() }

    val savedToastText = stringResource(R.string.history_saved_toast)

    // Back handling: dismiss calculation result card if displayed
    BackHandler(enabled = uiState.calculationResult != null) {
        viewModel.dismissResult()
    }

    // Ensure mode is DATE_BASED for Date Calculator
    LaunchedEffect(Unit) {
        if (uiState.mode != CalculatorMode.DATE_BASED) {
            viewModel.onModeChanged(CalculatorMode.DATE_BASED)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 680.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Banner
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = stringResource(R.string.date_calc_title),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = stringResource(R.string.date_calc_subtitle),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Scheme Selector (Simple / Compound)
                SchemeSelector(
                    selectedScheme = uiState.scheme,
                    onSchemeSelected = viewModel::onSchemeChanged
                )

                // Date Calculator Input Card
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 1. Principal Amount with Indian grouping and in-words helper
                        val cleanPrincipal = uiState.principalInput.replace(",", "").trim()
                        val principalDouble = cleanPrincipal.toDoubleOrNull()
                        val principalWords = principalDouble?.let { CurrencyFormatter.formatIndianInWords(it) }
                        val principalHelper = if (principalDouble != null && principalWords != null) {
                            "${CurrencyFormatter.formatCurrency(principalDouble, currency)} • $principalWords"
                        } else null

                        CurrencyInputField(
                            value = uiState.principalInput,
                            onValueChange = viewModel::onPrincipalChanged,
                            label = stringResource(R.string.label_principal),
                            placeholder = stringResource(R.string.hint_principal),
                            prefix = currency.symbol,
                            helperText = principalHelper,
                            visualTransformation = indianNumberVisualTransformation,
                            errorMessage = uiState.principalErrorRes?.let { stringResource(it) } ?: uiState.principalError,
                            testTag = "date_principal_input"
                        )

                        // 2. Vaddi Rate with dynamic label based on selected period
                        val rateLabel = when (uiState.interestType) {
                            InterestType.MONTHLY -> stringResource(R.string.label_interest_rate_monthly)
                            InterestType.YEARLY -> stringResource(R.string.label_interest_rate_yearly)
                            InterestType.DAILY -> stringResource(R.string.label_interest_rate_daily)
                        }

                        CurrencyInputField(
                            value = uiState.rateInput,
                            onValueChange = viewModel::onRateChanged,
                            label = rateLabel,
                            placeholder = stringResource(R.string.hint_interest_rate),
                            suffix = "%",
                            errorMessage = uiState.rateErrorRes?.let { stringResource(it) } ?: uiState.rateError,
                            testTag = "date_rate_input"
                        )

                        // 3. Vaddi Type Selector
                        InterestTypeSelector(
                            selectedType = uiState.interestType,
                            currentRate = uiState.rateInput,
                            onTypeSelected = viewModel::onInterestTypeChanged
                        )

                        // 4. Start Date & End Date
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            DateSelectorField(
                                label = stringResource(R.string.label_start_date),
                                selectedDate = uiState.startDate,
                                onDateSelected = viewModel::onStartDateChanged,
                                modifier = Modifier.weight(1f),
                                testTag = "start_date_picker"
                            )

                            DateSelectorField(
                                label = stringResource(R.string.label_end_date),
                                selectedDate = uiState.endDate,
                                onDateSelected = viewModel::onEndDateChanged,
                                minDate = uiState.startDate,
                                modifier = Modifier.weight(1f),
                                testTag = "end_date_picker"
                            )
                        }

                        val dateErrorText = uiState.dateErrorRes?.let { stringResource(it) } ?: uiState.dateError
                        if (dateErrorText != null) {
                            Text(
                                text = dateErrorText,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }

                        // Calculate & Clear Buttons
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = viewModel::calculate,
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .testTag("date_calculate_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.btn_calculate),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                onClick = viewModel::clear,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .height(52.dp)
                                    .testTag("date_clear_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RestartAlt,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Date Duration & Calculation Method Breakdown Card (as explicitly requested!)
                val result = uiState.calculationResult
                if (result != null && result.mode == CalculatorMode.DATE_BASED) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.date_calc_method_title),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = stringResource(R.string.label_start_date),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = result.startDate?.let { DateUtils.formatDateForDisplay(it) } ?: "-",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = stringResource(R.string.label_end_date),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = result.endDate?.let { DateUtils.formatDateForDisplay(it) } ?: "-",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = stringResource(R.string.date_completed_months),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${result.completedMonths} ${stringResource(R.string.unit_months)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = stringResource(R.string.date_remaining_days),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${result.remainingDays} ${stringResource(R.string.unit_days)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = stringResource(R.string.date_total_days),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${result.totalDays} ${stringResource(R.string.unit_days)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = result.calculationExplanation,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 18.sp),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }

                // Result Section
                ResultSection(
                    result = uiState.calculationResult,
                    currency = currency,
                    isSaved = uiState.isSaved,
                    onSaveToHistory = {
                        viewModel.saveToHistory()
                        scope.launch {
                            snackbarHostState.showSnackbar(savedToastText)
                        }
                    },
                    onRecalculate = viewModel::calculate,
                    modifier = Modifier.fillMaxWidth()
                )

                // Ad Banner Space Placeholder
                AdBannerPlaceholder(modifier = Modifier.padding(vertical = 4.dp))

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}
