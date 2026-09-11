package com.vaddicalculatortool.app.ui.calculator

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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.vaddicalculatortool.app.R
import com.vaddicalculatortool.app.domain.model.CalculatorMode
import com.vaddicalculatortool.app.domain.model.CurrencyOption
import com.vaddicalculatortool.app.domain.model.InterestScheme
import com.vaddicalculatortool.app.domain.model.InterestType
import com.vaddicalculatortool.app.domain.util.CurrencyFormatter
import com.vaddicalculatortool.app.ui.components.AdBannerPlaceholder
import com.vaddicalculatortool.app.ui.components.CurrencyInputField
import com.vaddicalculatortool.app.ui.components.IndianNumberVisualTransformation
import com.vaddicalculatortool.app.ui.components.InterestTypeSelector
import com.vaddicalculatortool.app.ui.components.ResultSection
import com.vaddicalculatortool.app.ui.components.SchemeSelector
import kotlinx.coroutines.launch

@Composable
fun CalculatorScreen(
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

    // Ensure mode is QUICK for the Quick Calculator screen
    if (uiState.mode != CalculatorMode.QUICK) {
        viewModel.onModeChanged(CalculatorMode.QUICK)
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
                // Header Banner: "Vaddi Calculator" & "Simple & Accurate Interest Calculator"
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
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Savings,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = stringResource(R.string.app_tagline),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Scheme Selector (Simple Vaddi vs Compound Vaddi)
                SchemeSelector(
                    selectedScheme = uiState.scheme,
                    onSchemeSelected = viewModel::onSchemeChanged
                )

                // Main Input Card: Enter Principal -> Enter Vaddi -> Select Period -> Calculate
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
                            testTag = "principal_input_field"
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
                            testTag = "rate_input_field"
                        )

                        // 3. Interest Type / Period Selector (Monthly / Yearly / Daily)
                        InterestTypeSelector(
                            selectedType = uiState.interestType,
                            currentRate = uiState.rateInput,
                            onTypeSelected = viewModel::onInterestTypeChanged
                        )

                        // 4. Dynamic Duration Input
                        val durationLabel = when (uiState.interestType) {
                            InterestType.MONTHLY -> stringResource(R.string.duration_unit_monthly)
                            InterestType.YEARLY -> stringResource(R.string.duration_unit_yearly)
                            InterestType.DAILY -> stringResource(R.string.duration_unit_daily)
                        }

                        val durationSuffix = when (uiState.interestType) {
                            InterestType.MONTHLY -> stringResource(R.string.unit_months)
                            InterestType.YEARLY -> stringResource(R.string.unit_years)
                            InterestType.DAILY -> stringResource(R.string.unit_days)
                        }

                        CurrencyInputField(
                            value = uiState.durationInput,
                            onValueChange = viewModel::onDurationChanged,
                            label = durationLabel,
                            placeholder = stringResource(R.string.hint_duration),
                            suffix = durationSuffix,
                            errorMessage = uiState.durationErrorRes?.let { stringResource(it) } ?: uiState.durationError,
                            testTag = "duration_input_field",
                            imeAction = androidx.compose.ui.text.input.ImeAction.Done,
                            onImeAction = viewModel::calculate
                        )

                        // 5. Contextual Helper Example (adapts dynamically to inputs)
                        val exampleP = principalDouble ?: 100000.0
                        val exampleR = uiState.rateInput.replace(",", "").toDoubleOrNull() ?: 2.0
                        val exampleInterest = exampleP * exampleR / 100.0
                        val exampleTypeString = when (uiState.interestType) {
                            InterestType.MONTHLY -> stringResource(R.string.type_monthly)
                            InterestType.YEARLY -> stringResource(R.string.type_yearly)
                            InterestType.DAILY -> stringResource(R.string.type_daily)
                        }
                        val exampleUnitString = when (uiState.interestType) {
                            InterestType.MONTHLY -> stringResource(R.string.duration_unit_monthly)
                            InterestType.YEARLY -> stringResource(R.string.duration_unit_yearly)
                            InterestType.DAILY -> stringResource(R.string.duration_unit_daily)
                        }
                        val dynamicExampleText = stringResource(
                            R.string.vaddi_helper_text,
                            CurrencyFormatter.formatCurrency(exampleP, currency),
                            if (exampleR % 1.0 == 0.0) exampleR.toInt().toString() else exampleR.toString(),
                            exampleTypeString,
                            CurrencyFormatter.formatCurrency(exampleInterest, currency),
                            exampleUnitString
                        )

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = dynamicExampleText,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        // Primary CTA: Prominent "Calculate Vaddi" & Reset buttons
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
                                    .testTag("calculate_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Calculate,
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
                                    .testTag("clear_button")
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

                // Result Section (Displays the exact requested parameters)
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
