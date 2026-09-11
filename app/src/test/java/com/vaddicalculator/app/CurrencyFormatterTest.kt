package com.vaddicalculator.app

import com.vaddicalculator.app.domain.model.CurrencyOption
import com.vaddicalculator.app.domain.util.CurrencyFormatter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CurrencyFormatterTest {

    @Test
    fun testFormatCurrency_INR_wholeAndDecimal() {
        assertEquals("₹5,000", CurrencyFormatter.formatCurrency(5000.0, CurrencyOption.INR))
        assertEquals("₹50,000", CurrencyFormatter.formatCurrency(50000.0, CurrencyOption.INR))
        assertEquals("₹1,00,000", CurrencyFormatter.formatCurrency(100000.0, CurrencyOption.INR))
        assertEquals("₹10,00,000", CurrencyFormatter.formatCurrency(1000000.0, CurrencyOption.INR))
        assertEquals("₹1,00,00,000", CurrencyFormatter.formatCurrency(10000000.0, CurrencyOption.INR))
        assertEquals("₹1,250.75", CurrencyFormatter.formatCurrency(1250.75, CurrencyOption.INR))
    }

    @Test
    fun testFormatCurrency_Negative() {
        assertEquals("-₹50,000", CurrencyFormatter.formatCurrency(-50000.0, CurrencyOption.INR))
    }

    @Test
    fun testFormatNumberOnly() {
        assertEquals("1,00,000", CurrencyFormatter.formatNumberOnly(100000.0, includeDecimals = false))
        assertEquals("50,000", CurrencyFormatter.formatNumberOnly(50000.0, includeDecimals = false))
    }

    @Test
    fun testFormatIndianInWords() {
        assertEquals("1 Thousand", CurrencyFormatter.formatIndianInWords(1000.0))
        assertEquals("50 Thousand", CurrencyFormatter.formatIndianInWords(50000.0))
        assertEquals("1 Lakh", CurrencyFormatter.formatIndianInWords(100000.0))
        assertEquals("5 Lakhs", CurrencyFormatter.formatIndianInWords(500000.0))
        assertEquals("1 Crore", CurrencyFormatter.formatIndianInWords(10000000.0))
        assertEquals("2.50 Crores", CurrencyFormatter.formatIndianInWords(25000000.0))
        assertNull(CurrencyFormatter.formatIndianInWords(0.0))
        assertNull(CurrencyFormatter.formatIndianInWords(-500.0))
    }
}
