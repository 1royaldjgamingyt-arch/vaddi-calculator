package com.vaddicalculator.app.ui.history

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vaddicalculator.app.R
import com.vaddicalculator.app.data.local.CalculationHistoryEntity
import com.vaddicalculator.app.domain.model.CurrencyOption
import com.vaddicalculator.app.domain.util.CurrencyFormatter
import com.vaddicalculator.app.ui.theme.CardGradientEnd
import com.vaddicalculator.app.ui.theme.CardGradientStart
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryDetailDialog(
    item: CalculationHistoryEntity,
    currency: CurrencyOption,
    onDismiss: () -> Unit,
    onLoadIntoCalculator: (CalculationHistoryEntity) -> Unit,
    onDelete: (Long) -> Unit
) {
    val context = LocalContext.current
    val dateTimeStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(item.timestamp))

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("history_detail_dialog"),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.dialog_history_detail_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { onDelete(item.id); onDismiss() }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.btn_delete),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = dateTimeStr,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Prominent Total Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(CardGradientStart, CardGradientEnd)
                                )
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = stringResource(R.string.result_total_payable),
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = CurrencyFormatter.formatCurrency(item.totalAmount, currency),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                DetailLine(
                    label = stringResource(R.string.result_principal),
                    value = CurrencyFormatter.formatCurrency(item.principal, currency)
                )
                DetailLine(
                    label = stringResource(R.string.result_rate),
                    value = "${item.rate}% ${item.interestType}"
                )
                DetailLine(
                    label = stringResource(R.string.result_interest),
                    value = CurrencyFormatter.formatCurrency(item.interestEarned, currency)
                )
                DetailLine(
                    label = stringResource(R.string.result_duration),
                    value = item.durationText
                )
                if (!item.startDate.isNullOrEmpty() && !item.endDate.isNullOrEmpty()) {
                    DetailLine(
                        label = stringResource(R.string.result_period),
                        value = "${item.startDate} → ${item.endDate}"
                    )
                }
                if (!item.formulaUsed.isNullOrEmpty()) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Text(
                        text = item.formulaUsed,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = {
                        shareHistoryItem(context, item, currency)
                    }
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.btn_share))
                }

                Button(
                    onClick = {
                        onLoadIntoCalculator(item)
                        onDismiss()
                    }
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.btn_open_in_calc))
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.btn_close))
            }
        }
    )
}

@Composable
private fun DetailLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}

private fun shareHistoryItem(context: Context, item: CalculationHistoryEntity, currency: CurrencyOption) {
    val text = buildString {
        appendLine(context.getString(R.string.share_header))
        appendLine(context.getString(R.string.share_principal, CurrencyFormatter.formatCurrency(item.principal, currency)))
        appendLine(context.getString(R.string.share_rate, "${item.rate}% ${item.interestType}"))
        appendLine(context.getString(R.string.share_duration, item.durationText))
        appendLine(context.getString(R.string.share_total_interest, CurrencyFormatter.formatCurrency(item.interestEarned, currency)))
        appendLine(context.getString(R.string.share_total_amount, CurrencyFormatter.formatCurrency(item.totalAmount, currency)))
        appendLine()
        appendLine(context.getString(R.string.share_footer))
    }
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, context.getString(R.string.btn_share)))
}
