package com.vaddicalculator.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vaddicalculator.app.R
import com.vaddicalculator.app.domain.model.InterestType

@Composable
fun InterestTypeSelector(
    selectedType: InterestType,
    currentRate: String,
    onTypeSelected: (InterestType) -> Unit,
    modifier: Modifier = Modifier
) {
    val rateDisplay = if (currentRate.isNotBlank()) currentRate else "2"

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.label_interest_type),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("interest_type_selector"),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InterestType.values().forEach { type ->
                val isSelected = type == selectedType

                val containerColor by animateColorAsState(
                    targetValue = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    },
                    label = "chipContainer"
                )

                val contentColor by animateColorAsState(
                    targetValue = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    label = "chipContent"
                )

                val label = when (type) {
                    InterestType.MONTHLY -> stringResource(R.string.type_monthly)
                    InterestType.YEARLY -> stringResource(R.string.type_yearly)
                    InterestType.DAILY -> stringResource(R.string.type_daily)
                }

                Surface(
                    onClick = { onTypeSelected(type) },
                    shape = RoundedCornerShape(14.dp),
                    color = containerColor,
                    border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .testTag("interest_type_${type.name.lowercase()}")
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = contentColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
                        maxLines = 2
                    )
                }
            }
        }
    }
}
