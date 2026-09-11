package com.vaddicalculator.app.domain.util

import com.vaddicalculator.app.domain.model.CurrencyOption
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

object CurrencyFormatter {

    /**
     * Formats an amount with currency symbol according to Indian or international numbering format.
     * Guaranteed Indian grouping: ₹1,00,000, ₹10,00,000, ₹1,00,00,000
     */
    fun formatCurrency(amount: Double, currency: CurrencyOption = CurrencyOption.INR): String {
        val bd = BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP)
        return formatCurrency(bd, currency)
    }

    fun formatCurrency(bd: BigDecimal, currency: CurrencyOption = CurrencyOption.INR): String {
        val rounded = bd.setScale(2, RoundingMode.HALF_UP)
        val isNegative = rounded.signum() < 0
        val absBd = rounded.abs()
        val isWhole = absBd.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0

        return if (currency == CurrencyOption.INR) {
            val formatted = formatIndianNumber(absBd, includeDecimals = !isWhole)
            if (isNegative) "-${currency.symbol}$formatted" else "${currency.symbol}$formatted"
        } else {
            val format = NumberFormat.getCurrencyInstance(Locale.US)
            format.currency = java.util.Currency.getInstance(currency.code)
            if (isWhole) format.maximumFractionDigits = 0
            val formatted = format.format(absBd.toDouble())
            if (isNegative) "-$formatted" else formatted
        }
    }

    /**
     * Formats pure number without symbol, suitable for edit texts or display.
     */
    fun formatNumberOnly(amount: Double, includeDecimals: Boolean = true): String {
        val bd = BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP)
        return formatIndianNumber(bd, includeDecimals)
    }

    /**
     * Converts large amounts to readable Indian denominations (Lakhs, Crores, Thousands).
     * Example: 100000 -> "1 Lakh", 5000000 -> "50 Lakhs", 10000000 -> "1 Crore"
     */
    fun formatIndianInWords(amount: Double): String? {
        if (amount <= 0.0) return null
        return when {
            amount >= 10000000.0 -> {
                val crores = amount / 10000000.0
                val formatted = if (crores % 1.0 == 0.0) crores.toInt().toString() else String.format(Locale.US, "%.2f", crores)
                "$formatted Crore${if (crores > 1.0) "s" else ""}"
            }
            amount >= 100000.0 -> {
                val lakhs = amount / 100000.0
                val formatted = if (lakhs % 1.0 == 0.0) lakhs.toInt().toString() else String.format(Locale.US, "%.2f", lakhs)
                "$formatted Lakh${if (lakhs > 1.0) "s" else ""}"
            }
            amount >= 1000.0 -> {
                val thousands = amount / 1000.0
                val formatted = if (thousands % 1.0 == 0.0) thousands.toInt().toString() else String.format(Locale.US, "%.1f", thousands)
                "$formatted Thousand"
            }
            else -> null
        }
    }

    private fun formatIndianNumber(bd: BigDecimal, includeDecimals: Boolean): String {
        val isNegative = bd.signum() < 0
        val absBd = bd.abs()
        val longVal = absBd.toLong()
        val fraction = absBd.remainder(BigDecimal.ONE).multiply(BigDecimal(100)).toInt()

        val s = longVal.toString()
        val len = s.length

        val sb = StringBuilder()
        if (len <= 3) {
            sb.append(s)
        } else {
            val lastThree = s.substring(len - 3)
            val rest = s.substring(0, len - 3)
            val restBuilder = StringBuilder()
            var count = 0
            for (i in rest.length - 1 downTo 0) {
                restBuilder.append(rest[i])
                count++
                if (count == 2 && i != 0) {
                    restBuilder.append(',')
                    count = 0
                }
            }
            sb.append(restBuilder.reverse().toString())
            sb.append(',')
            sb.append(lastThree)
        }

        if (includeDecimals && fraction > 0) {
            sb.append('.').append(String.format(Locale.US, "%02d", fraction))
        }

        if (isNegative) {
            sb.insert(0, '-')
        }

        return sb.toString()
    }
}
