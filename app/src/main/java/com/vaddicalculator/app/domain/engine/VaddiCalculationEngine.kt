package com.vaddicalculator.app.domain.engine

import com.vaddicalculator.app.domain.model.CalculationResult
import com.vaddicalculator.app.domain.model.CalculatorMode
import com.vaddicalculator.app.domain.model.DateCalculationInput
import com.vaddicalculator.app.domain.model.DurationBreakdown
import com.vaddicalculator.app.domain.model.InterestScheme
import com.vaddicalculator.app.domain.model.InterestType
import com.vaddicalculator.app.domain.model.QuickCalculationInput
import com.vaddicalculator.app.domain.util.DateUtils
import java.math.BigDecimal
import java.math.RoundingMode

object VaddiCalculationEngine {

    /**
     * Calculates Simple or Compound Vaddi for Quick mode (Direct duration input).
     *
     * Core Indian Vaddi rule:
     * When Monthly Vaddi is selected, rate is per month.
     * Monthly Interest = Principal × Monthly Vaddi Rate × Number of Months / 100.
     * Example: ₹1,00,000 × 2 × 6 / 100 = ₹12,000.
     */
    fun calculateQuick(input: QuickCalculationInput): CalculationResult {
        if (input.principal <= 0.0 || input.rate <= 0.0 || input.duration <= 0.0) {
            return CalculationResult(
                principal = input.principal.coerceAtLeast(0.0),
                rate = input.rate.coerceAtLeast(0.0),
                interestType = input.interestType,
                scheme = input.scheme,
                mode = CalculatorMode.QUICK,
                interestEarned = 0.0,
                totalAmount = input.principal.coerceAtLeast(0.0),
                durationDescription = "0"
            )
        }

        val principalBd = BigDecimal.valueOf(input.principal)
        val rateBd = BigDecimal.valueOf(input.rate)
        val durationBd = BigDecimal.valueOf(input.duration)
        val hundred = BigDecimal.valueOf(100)

        // 1 Month / 1 Unit interest:
        val perUnitInterestBd = principalBd.multiply(rateBd).divide(hundred, 6, RoundingMode.HALF_UP)
        val monthlyInterest = when (input.interestType) {
            InterestType.MONTHLY -> perUnitInterestBd.toDouble()
            InterestType.YEARLY -> perUnitInterestBd.divide(BigDecimal.valueOf(12), 6, RoundingMode.HALF_UP).toDouble()
            InterestType.DAILY -> perUnitInterestBd.multiply(BigDecimal.valueOf(30)).toDouble()
        }
        val yearlyInterest = when (input.interestType) {
            InterestType.YEARLY -> perUnitInterestBd.toDouble()
            InterestType.MONTHLY -> perUnitInterestBd.multiply(BigDecimal.valueOf(12)).toDouble()
            InterestType.DAILY -> perUnitInterestBd.multiply(BigDecimal.valueOf(365)).toDouble()
        }

        val (interestEarnedBd, totalAmountBd, formula, explanation) = if (input.scheme == InterestScheme.SIMPLE) {
            // Simple Vaddi: Principal * Rate * Duration / 100
            val interestBd = principalBd
                .multiply(rateBd)
                .multiply(durationBd)
                .divide(hundred, 6, RoundingMode.HALF_UP)
            val totalBd = principalBd.add(interestBd)

            val f = when (input.interestType) {
                InterestType.MONTHLY -> "₹${input.principal.toPlainString()} × ${input.rate}% × ${input.duration.toPlainString()} Months / 100"
                InterestType.YEARLY -> "₹${input.principal.toPlainString()} × ${input.rate}% × ${input.duration.toPlainString()} Years / 100"
                InterestType.DAILY -> "₹${input.principal.toPlainString()} × ${input.rate}% × ${input.duration.toPlainString()} Days / 100"
            }
            val exp = when (input.interestType) {
                InterestType.MONTHLY -> "1 Month Vaddi = ₹${monthlyInterest.toRoundedString()} | Total for ${input.duration.toPlainString()} Months = ₹${interestBd.setScale(2, RoundingMode.HALF_UP)}"
                InterestType.YEARLY -> "1 Year Vaddi = ₹${perUnitInterestBd.toDouble().toRoundedString()} | Total for ${input.duration.toPlainString()} Years = ₹${interestBd.setScale(2, RoundingMode.HALF_UP)}"
                InterestType.DAILY -> "1 Day Vaddi = ₹${perUnitInterestBd.toDouble().toRoundedString()} | Total for ${input.duration.toPlainString()} Days = ₹${interestBd.setScale(2, RoundingMode.HALF_UP)}"
            }
            Quadruple(interestBd, totalBd, f, exp)
        } else {
            // Compound Vaddi: A = P * (1 + r/100)^t
            val base = 1.0 + (input.rate / 100.0)
            val totalAmountDouble = input.principal * Math.pow(base, input.duration)
            val interestDouble = totalAmountDouble - input.principal
            val totalBd = BigDecimal.valueOf(totalAmountDouble).setScale(2, RoundingMode.HALF_UP)
            val interestBd = BigDecimal.valueOf(interestDouble).setScale(2, RoundingMode.HALF_UP)
            val f = "Compound: P × (1 + Rate / 100)^Duration"
            val exp = "Chakra Vaddi compounded per duration period"
            Quadruple(interestBd, totalBd, f, exp)
        }

        val interest = interestEarnedBd.setScale(2, RoundingMode.HALF_UP).toDouble()
        val total = totalAmountBd.setScale(2, RoundingMode.HALF_UP).toDouble()

        val durationDesc = when (input.interestType) {
            InterestType.MONTHLY -> {
                val m = input.duration
                if (m % 1.0 == 0.0) "${m.toInt()} Months" else "$m Months"
            }
            InterestType.YEARLY -> {
                val y = input.duration
                if (y % 1.0 == 0.0) "${y.toInt()} Years" else "$y Years"
            }
            InterestType.DAILY -> {
                val d = input.duration
                if (d % 1.0 == 0.0) "${d.toInt()} Days" else "$d Days"
            }
        }

        val dailyEquivalent = when (input.interestType) {
            InterestType.MONTHLY -> monthlyInterest / 30.0
            InterestType.YEARLY -> (monthlyInterest * 12.0) / 365.0
            InterestType.DAILY -> interest / input.duration
        }

        return CalculationResult(
            principal = input.principal,
            rate = input.rate,
            interestType = input.interestType,
            scheme = input.scheme,
            mode = CalculatorMode.QUICK,
            interestEarned = interest,
            totalAmount = total,
            durationDescription = durationDesc,
            durationValue = input.duration,
            monthlyInterest = monthlyInterest,
            yearlyInterest = yearlyInterest,
            dailyInterest = dailyEquivalent,
            formulaUsed = formula,
            calculationExplanation = explanation
        )
    }

    /**
     * Calculates Vaddi for Date-based mode (Calendar Start & End date).
     *
     * In Indian tradition:
     * - Completed months: computed from start to end date (e.g. 1 Year 2 Months = 14 Months).
     * - Remaining days: remaining days in the incomplete month.
     * - Monthly Vaddi is applied directly to completed months: Principal × Rate × Months / 100.
     * - Daily portion for remaining days: Principal × (Rate / 30) × Days / 100.
     * - Total Vaddi = Completed Months Vaddi + Remaining Days Vaddi.
     */
    fun calculateDateBased(input: DateCalculationInput): CalculationResult {
        if (input.principal <= 0.0 || input.rate <= 0.0 || input.endDate.isBefore(input.startDate)) {
            val totalDays = DateUtils.calculateDaysBetween(input.startDate, input.endDate)
            return CalculationResult(
                principal = input.principal.coerceAtLeast(0.0),
                rate = input.rate.coerceAtLeast(0.0),
                interestType = input.interestType,
                scheme = input.scheme,
                mode = CalculatorMode.DATE_BASED,
                interestEarned = 0.0,
                totalAmount = input.principal.coerceAtLeast(0.0),
                durationDescription = "0 Days",
                startDate = input.startDate,
                endDate = input.endDate,
                durationBreakdown = DurationBreakdown(totalDays = totalDays)
            )
        }

        val breakdown = DateUtils.calculateBreakdown(input.startDate, input.endDate)
        val totalDays = breakdown.totalDays

        if (totalDays == 0L) {
            return CalculationResult(
                principal = input.principal,
                rate = input.rate,
                interestType = input.interestType,
                scheme = input.scheme,
                mode = CalculatorMode.DATE_BASED,
                interestEarned = 0.0,
                totalAmount = input.principal,
                durationDescription = "Same Day (0 Days)",
                durationBreakdown = breakdown,
                startDate = input.startDate,
                endDate = input.endDate,
                monthlyInterest = 0.0,
                dailyInterest = 0.0,
                completedMonths = 0,
                remainingDays = 0,
                totalDays = 0L,
                formulaUsed = "Total Days = 0",
                calculationExplanation = "Start Date and End Date are identical"
            )
        }

        val principalBd = BigDecimal.valueOf(input.principal)
        val rateBd = BigDecimal.valueOf(input.rate)
        val hundred = BigDecimal.valueOf(100)

        val completedMonths = breakdown.completedMonths
        val remainingDays = breakdown.remainingDays

        // Per month interest:
        val monthlyInterestBd = principalBd.multiply(rateBd).divide(hundred, 6, RoundingMode.HALF_UP)
        val monthlyInterest = monthlyInterestBd.toDouble()

        val (interestBd, formula, explanation) = when (input.interestType) {
            InterestType.MONTHLY -> {
                // Completed Months Interest: Principal * Rate * Months / 100
                val monthsInterestBd = principalBd
                    .multiply(rateBd)
                    .multiply(BigDecimal.valueOf(completedMonths.toLong()))
                    .divide(hundred, 6, RoundingMode.HALF_UP)

                // Remaining Days Interest (1 Month = 30 Days Indian convention):
                val dailyRateBd = rateBd.divide(BigDecimal.valueOf(30), 8, RoundingMode.HALF_UP)
                val daysInterestBd = principalBd
                    .multiply(dailyRateBd)
                    .multiply(BigDecimal.valueOf(remainingDays.toLong()))
                    .divide(hundred, 6, RoundingMode.HALF_UP)

                val totalInterestBd = monthsInterestBd.add(daysInterestBd)

                val formulaStr = "(${completedMonths} Months × ₹${monthlyInterest.toRoundedString()}) + (${remainingDays} Days × ₹${(monthlyInterest / 30.0).toRoundedString()})"
                val expStr = buildString {
                    append("• Completed Months: $completedMonths months = ₹${monthsInterestBd.setScale(2, RoundingMode.HALF_UP)}\n")
                    append("• Remaining Days: $remainingDays days (@ 30 days/month convention) = ₹${daysInterestBd.setScale(2, RoundingMode.HALF_UP)}\n")
                    append("• Total Days: $totalDays days")
                }
                Triple(totalInterestBd, formulaStr, expStr)
            }
            InterestType.YEARLY -> {
                // Time in years = totalDays / 365.0
                val timeYearsBd = BigDecimal.valueOf(totalDays).divide(BigDecimal.valueOf(365.0), 8, RoundingMode.HALF_UP)
                val totalInterestBd = principalBd.multiply(rateBd).multiply(timeYearsBd).divide(hundred, 6, RoundingMode.HALF_UP)
                val formulaStr = "Principal × Rate% × ($totalDays Days / 365) / 100"
                val expStr = "Calculated based on $totalDays exact days / 365 days per year"
                Triple(totalInterestBd, formulaStr, expStr)
            }
            InterestType.DAILY -> {
                val totalInterestBd = principalBd.multiply(rateBd).multiply(BigDecimal.valueOf(totalDays)).divide(hundred, 6, RoundingMode.HALF_UP)
                val formulaStr = "Principal × Rate% × $totalDays Days / 100"
                val expStr = "Calculated for $totalDays exact days @ ${input.rate}% daily"
                Triple(totalInterestBd, formulaStr, expStr)
            }
        }

        val interest = interestBd.setScale(2, RoundingMode.HALF_UP).toDouble()
        val totalAmount = principalBd.add(interestBd).setScale(2, RoundingMode.HALF_UP).toDouble()

        val dailyEquivalent = interest / totalDays.toDouble()

        return CalculationResult(
            principal = input.principal,
            rate = input.rate,
            interestType = input.interestType,
            scheme = input.scheme,
            mode = CalculatorMode.DATE_BASED,
            interestEarned = interest,
            totalAmount = totalAmount,
            durationDescription = "${breakdown.toReadableString()} ($totalDays Days)",
            monthlyInterest = monthlyInterest,
            yearlyInterest = monthlyInterest * 12.0,
            dailyInterest = dailyEquivalent,
            completedMonths = completedMonths,
            remainingDays = remainingDays,
            totalDays = totalDays,
            durationBreakdown = breakdown,
            startDate = input.startDate,
            endDate = input.endDate,
            formulaUsed = formula,
            calculationExplanation = explanation
        )
    }

    private fun Double.toPlainString(): String {
        return if (this % 1.0 == 0.0) this.toInt().toString() else this.toString()
    }

    private fun Double.toRoundedString(): String {
        return if (this % 1.0 == 0.0) this.toInt().toString() else String.format(java.util.Locale.US, "%.2f", this)
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
