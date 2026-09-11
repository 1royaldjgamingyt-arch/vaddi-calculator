package com.vaddicalculator.app.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vaddicalculator.app.R
import com.vaddicalculator.app.domain.model.CalculationResult
import com.vaddicalculator.app.domain.model.CalculatorMode
import com.vaddicalculator.app.domain.model.CurrencyOption
import com.vaddicalculator.app.domain.model.InterestScheme
import com.vaddicalculator.app.domain.model.InterestType
import com.vaddicalculator.app.domain.model.getLocalizedDuration
import com.vaddicalculator.app.domain.util.CurrencyFormatter
import com.vaddicalculator.app.ui.theme.CardGradientEnd
import com.vaddicalculator.app.ui.theme.CardGradientStart

@Composable
fun ResultSection(
    result: CalculationResult?,
    currency: CurrencyOption,
    isSaved: Boolean,
    onSaveToHistory: () -> Unit,
    onRecalculate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AnimatedVisibility(
        visible = result != null,
        enter = fadeIn() + expandVertically() + slideInVertically(initialOffsetY = { it / 4 }),
        modifier = modifier
    ) {
        if (result == null) return@AnimatedVisibility

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("result_section"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.title_result),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    val badgeLabel = when (result.scheme) {
                        InterestScheme.SIMPLE -> stringResource(R.string.scheme_simple)
                        InterestScheme.COMPOUND -> stringResource(R.string.scheme_compound)
                    }
                    Text(
                        text = badgeLabel,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Hero Highlight Total Amount Card
            Card(
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("result_card_total")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(CardGradientStart, CardGradientEnd)
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.result_total_payable),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.9f),
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = CurrencyFormatter.formatCurrency(result.totalAmount, currency),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontSize = 36.sp,
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.testTag("total_amount_text")
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Visual Breakdown Equation: Principal + Interest = Total
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White.copy(alpha = 0.14f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.Start) {
                                    Text(
                                        text = stringResource(R.string.result_principal),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White.copy(alpha = 0.75f)
                                    )
                                    Text(
                                        text = CurrencyFormatter.formatCurrency(result.principal, currency),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                Text(
                                    text = "+",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.8f)
                                )

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = stringResource(R.string.result_interest),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White.copy(alpha = 0.75f)
                                    )
                                    Text(
                                        text = CurrencyFormatter.formatCurrency(result.interestEarned, currency),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    )
                                }

                                Text(
                                    text = "=",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.8f)
                                )

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = stringResource(R.string.result_total),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White.copy(alpha = 0.75f)
                                    )
                                    Text(
                                        text = CurrencyFormatter.formatCurrency(result.totalAmount, currency),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Structured Key Results Card (As explicitly requested by user)
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.result_breakdown),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // 1. అసలు మొత్తం / Principal Amount
                    ResultDetailRow(
                        label = stringResource(R.string.result_principal),
                        value = CurrencyFormatter.formatCurrency(result.principal, currency)
                    )

                    // 2. వడ్డీ రేటు / Vaddi Rate
                    val rateSuffix = when (result.interestType) {
                        InterestType.MONTHLY -> stringResource(R.string.rate_badge_monthly, result.rate.toCleanString())
                        InterestType.YEARLY -> stringResource(R.string.rate_badge_yearly, result.rate.toCleanString())
                        InterestType.DAILY -> stringResource(R.string.rate_badge_daily, result.rate.toCleanString())
                    }
                    ResultDetailRow(
                        label = stringResource(R.string.result_rate),
                        value = rateSuffix
                    )

                    // 3. Duration
                    ResultDetailRow(
                        label = stringResource(R.string.result_duration),
                        value = result.getLocalizedDuration(context)
                    )

                    // 4. Unit Interest (Monthly / Yearly / Daily)
                    val unitInterestLabel = when (result.interestType) {
                        InterestType.MONTHLY -> stringResource(R.string.result_monthly_vaddi)
                        InterestType.YEARLY -> stringResource(R.string.result_yearly_vaddi)
                        InterestType.DAILY -> stringResource(R.string.result_daily_vaddi)
                    }
                    val unitInterestValue = when (result.interestType) {
                        InterestType.MONTHLY -> result.monthlyInterest
                        InterestType.YEARLY -> result.yearlyInterest
                        InterestType.DAILY -> result.dailyInterest
                    }
                    ResultDetailRow(
                        label = unitInterestLabel,
                        value = CurrencyFormatter.formatCurrency(unitInterestValue, currency),
                        highlight = true
                    )

                    // 5. మొత్తం వడ్డీ / Total Interest
                    ResultDetailRow(
                        label = stringResource(R.string.result_interest),
                        value = CurrencyFormatter.formatCurrency(result.interestEarned, currency),
                        highlight = true
                    )

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        thickness = 1.dp
                    )

                    // 6. మొత్తం చెల్లించాల్సిన మొత్తం / Total Payable Amount
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.result_total_payable),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = CurrencyFormatter.formatCurrency(result.totalAmount, currency),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Calculation Explanation if available (for date calculator & quick)
                    if (result.calculationExplanation.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = stringResource(R.string.result_formula),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = result.calculationExplanation,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Action Buttons (Share, Save, Recalculate)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Share Button
                Button(
                    onClick = {
                        shareResult(context, result, currency)
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("share_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.btn_share),
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Save to History Button
                OutlinedButton(
                    onClick = onSaveToHistory,
                    enabled = !isSaved,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        1.5.dp,
                        if (isSaved) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("save_history_button")
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.BookmarkAdded else Icons.Default.BookmarkBorder,
                        contentDescription = if (isSaved) "Saved" else "Save",
                        tint = if (isSaved) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isSaved) stringResource(R.string.btn_saved) else stringResource(R.string.btn_save),
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSaved) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Recalculate / Calculate Again link
            OutlinedButton(
                onClick = onRecalculate,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("recalculate_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.btn_recalculate),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ResultDetailRow(
    label: String,
    value: String,
    highlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.SemiBold,
            color = if (highlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun Double.toCleanString(): String {
    return if (this % 1.0 == 0.0) this.toInt().toString() else this.toString()
}

private fun shareResult(context: Context, result: CalculationResult, currency: CurrencyOption) {
    val rateText = when (result.interestType) {
        InterestType.MONTHLY -> context.getString(R.string.rate_badge_monthly, result.rate.toCleanString())
        InterestType.YEARLY -> context.getString(R.string.rate_badge_yearly, result.rate.toCleanString())
        InterestType.DAILY -> context.getString(R.string.rate_badge_daily, result.rate.toCleanString())
    }
    val vaddiTypeName = when (result.interestType) {
        InterestType.MONTHLY -> context.getString(R.string.type_monthly)
        InterestType.YEARLY -> context.getString(R.string.type_yearly)
        InterestType.DAILY -> context.getString(R.string.type_daily)
    }
    val principalFormatted = CurrencyFormatter.formatCurrency(result.principal, currency)
    val interestFormatted = CurrencyFormatter.formatCurrency(result.interestEarned, currency)
    val totalFormatted = CurrencyFormatter.formatCurrency(result.totalAmount, currency)

    val shareText = buildString {
        appendLine(context.getString(R.string.share_header))
        appendLine()
        appendLine(context.getString(R.string.share_principal, principalFormatted))
        appendLine(context.getString(R.string.share_rate, rateText))
        appendLine(context.getString(R.string.share_vaddi_type, vaddiTypeName))
        appendLine(context.getString(R.string.share_duration, result.getLocalizedDuration(context)))
        when (result.interestType) {
            InterestType.MONTHLY -> appendLine(context.getString(R.string.share_monthly_interest, CurrencyFormatter.formatCurrency(result.monthlyInterest, currency)))
            InterestType.YEARLY -> appendLine(context.getString(R.string.share_yearly_interest, CurrencyFormatter.formatCurrency(result.yearlyInterest, currency)))
            InterestType.DAILY -> appendLine(context.getString(R.string.share_daily_interest, CurrencyFormatter.formatCurrency(result.dailyInterest, currency)))
        }
        appendLine(context.getString(R.string.share_total_interest, interestFormatted))
        appendLine(context.getString(R.string.share_total_amount, totalFormatted))
        appendLine()
        appendLine(context.getString(R.string.share_footer))
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    context.startActivity(Intent.createChooser(intent, context.getString(R.string.btn_share)))
}
