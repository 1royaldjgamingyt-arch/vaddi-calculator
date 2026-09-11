package com.vaddicalculatortool.app.domain.model

import java.time.LocalDate

enum class InterestType {
    MONTHLY,
    YEARLY,
    DAILY
}

enum class InterestScheme {
    SIMPLE,
    COMPOUND
}

enum class CalculatorMode {
    QUICK,
    DATE_BASED
}

data class DurationBreakdown(
    val years: Int = 0,
    val months: Int = 0,
    val days: Int = 0,
    val totalDays: Long = 0L,
    val completedMonths: Int = (years * 12) + months,
    val remainingDays: Int = days,
    val totalMonthsDecimal: Double = 0.0,
    val totalYearsDecimal: Double = 0.0
) {
    fun toReadableString(isTelugu: Boolean = false): String {
        val parts = mutableListOf<String>()
        if (years > 0) {
            parts.add(if (isTelugu) "$years సం." else "$years ${if (years == 1) "Year" else "Years"}")
        }
        if (months > 0) {
            parts.add(if (isTelugu) "$months నెలలు" else "$months ${if (months == 1) "Month" else "Months"}")
        }
        if (days > 0) {
            parts.add(if (isTelugu) "$days రోజులు" else "$days ${if (days == 1) "Day" else "Days"}")
        }
        return if (parts.isEmpty()) {
            if (isTelugu) "0 రోజులు" else "0 Days"
        } else {
            parts.joinToString(", ")
        }
    }
}

data class QuickCalculationInput(
    val principal: Double,
    val rate: Double,
    val interestType: InterestType,
    val duration: Double,
    val scheme: InterestScheme = InterestScheme.SIMPLE
)

data class DateCalculationInput(
    val principal: Double,
    val rate: Double,
    val interestType: InterestType,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val scheme: InterestScheme = InterestScheme.SIMPLE
)

data class CalculationResult(
    val principal: Double,
    val rate: Double,
    val interestType: InterestType,
    val scheme: InterestScheme = InterestScheme.SIMPLE,
    val mode: CalculatorMode,
    val interestEarned: Double,
    val totalAmount: Double,
    val durationDescription: String,
    val durationValue: Double = 0.0,
    val monthlyInterest: Double = 0.0,
    val yearlyInterest: Double = 0.0,
    val dailyInterest: Double = 0.0,
    val completedMonths: Int = 0,
    val remainingDays: Int = 0,
    val totalDays: Long = 0L,
    val durationBreakdown: DurationBreakdown? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val formulaUsed: String = "",
    val calculationExplanation: String = ""
)

fun CalculationResult.getLocalizedDuration(context: android.content.Context): String {
    return when (mode) {
        CalculatorMode.QUICK -> {
            val d = if (durationValue % 1.0 == 0.0) durationValue.toInt().toString() else durationValue.toString()
            when (interestType) {
                InterestType.MONTHLY -> "$d ${context.getString(com.vaddicalculatortool.app.R.string.unit_months)}"
                InterestType.YEARLY -> "$d ${context.getString(com.vaddicalculatortool.app.R.string.unit_years)}"
                InterestType.DAILY -> "$d ${context.getString(com.vaddicalculatortool.app.R.string.unit_days)}"
            }
        }
        CalculatorMode.DATE_BASED -> {
            if (durationBreakdown != null) {
                "${durationBreakdown.toLocalizedReadableString(context)} ($totalDays ${context.getString(com.vaddicalculatortool.app.R.string.unit_days)})"
            } else {
                durationDescription
            }
        }
    }
}

fun DurationBreakdown.toLocalizedReadableString(context: android.content.Context): String {
    val parts = mutableListOf<String>()
    if (years > 0) {
        parts.add("$years ${context.getString(com.vaddicalculatortool.app.R.string.unit_years)}")
    }
    if (months > 0) {
        parts.add("$months ${context.getString(com.vaddicalculatortool.app.R.string.unit_months)}")
    }
    if (days > 0) {
        parts.add("$days ${context.getString(com.vaddicalculatortool.app.R.string.unit_days)}")
    }
    return if (parts.isEmpty()) {
        "0 ${context.getString(com.vaddicalculatortool.app.R.string.unit_days)}"
    } else {
        parts.joinToString(", ")
    }
}
