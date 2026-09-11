package com.vaddicalculator.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.vaddicalculator.app.R

enum class AppNavDestination(
    val titleRes: Int,
    val icon: ImageVector,
    val testTag: String
) {
    QUICK_VADDI(R.string.nav_quick_vaddi, Icons.Default.Calculate, "nav_quick_vaddi"),
    DATE_VADDI(R.string.nav_date_vaddi, Icons.Default.DateRange, "nav_date_vaddi"),
    HISTORY(R.string.nav_history, Icons.Default.History, "nav_history"),
    SETTINGS(R.string.nav_settings, Icons.Default.Settings, "nav_settings")
}
