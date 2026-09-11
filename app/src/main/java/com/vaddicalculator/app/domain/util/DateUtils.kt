package com.vaddicalculator.app.domain.util

import com.vaddicalculator.app.domain.model.DurationBreakdown
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

object DateUtils {

    private val displayFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
    private val isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun formatDateForDisplay(date: LocalDate): String {
        return try {
            date.format(displayFormatter)
        } catch (e: Exception) {
            date.toString()
        }
    }

    fun parseDate(dateStr: String): LocalDate? {
        return try {
            LocalDate.parse(dateStr, isoFormatter)
        } catch (e: Exception) {
            null
        }
    }

    fun calculateDaysBetween(startDate: LocalDate, endDate: LocalDate): Long {
        if (endDate.isBefore(startDate)) return 0L
        return ChronoUnit.DAYS.between(startDate, endDate)
    }

    /**
     * Calculates the breakdown into Years, Months, and Days as per Indian tradition.
     * For example, 15 Jan 2024 to 20 Mar 2025:
     * Period: 1 Year, 2 Months, 5 Days.
     */
    fun calculateBreakdown(startDate: LocalDate, endDate: LocalDate): DurationBreakdown {
        if (endDate.isBefore(startDate)) {
            return DurationBreakdown()
        }

        val totalDays = ChronoUnit.DAYS.between(startDate, endDate)
        val period = Period.between(startDate, endDate)

        val years = period.years
        val months = period.months
        val days = period.days

        // Total months decimal: years * 12 + months + (days / 30.0)
        val totalMonthsDecimal = (years * 12.0) + months + (days / 30.0)
        val totalYearsDecimal = totalDays / 365.0

        return DurationBreakdown(
            years = years,
            months = months,
            days = days,
            totalDays = totalDays,
            totalMonthsDecimal = totalMonthsDecimal,
            totalYearsDecimal = totalYearsDecimal
        )
    }

    fun isValidDateRange(startDate: LocalDate, endDate: LocalDate): Boolean {
        return !endDate.isBefore(startDate)
    }
}
