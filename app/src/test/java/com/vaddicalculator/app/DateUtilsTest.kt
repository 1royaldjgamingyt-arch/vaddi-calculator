package com.vaddicalculator.app

import com.vaddicalculator.app.domain.util.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class DateUtilsTest {

    @Test
    fun testParseDate_validAndInvalid() {
        val parsed = DateUtils.parseDate("2024-05-15")
        assertNotNull(parsed)
        assertEquals(LocalDate.of(2024, 5, 15), parsed)

        val invalid = DateUtils.parseDate("invalid-date")
        assertNull(invalid)
    }

    @Test
    fun testDaysBetween() {
        val start = LocalDate.of(2024, 1, 1)
        val end = LocalDate.of(2024, 1, 31)
        val days = DateUtils.calculateDaysBetween(start, end)
        assertEquals(30L, days)

        // Same day
        assertEquals(0L, DateUtils.calculateDaysBetween(start, start))

        // End before start returns 0
        assertEquals(0L, DateUtils.calculateDaysBetween(end, start))
    }

    @Test
    fun testCalculateBreakdown() {
        val start = LocalDate.of(2023, 1, 15)
        val end = LocalDate.of(2024, 3, 20)
        val breakdown = DateUtils.calculateBreakdown(start, end)

        assertEquals(1, breakdown.years)
        assertEquals(2, breakdown.months)
        assertEquals(5, breakdown.days)
        assertTrue(breakdown.totalDays > 365)
    }

    @Test
    fun testIsValidDateRange() {
        val start = LocalDate.of(2024, 1, 1)
        val end = LocalDate.of(2024, 6, 1)

        assertTrue(DateUtils.isValidDateRange(start, end))
        assertTrue(DateUtils.isValidDateRange(start, start))
        assertFalse(DateUtils.isValidDateRange(end, start))
    }
}
