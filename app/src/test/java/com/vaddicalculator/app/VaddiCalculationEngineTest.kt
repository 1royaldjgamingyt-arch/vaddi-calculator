package com.vaddicalculator.app

import com.vaddicalculator.app.domain.engine.VaddiCalculationEngine
import com.vaddicalculator.app.domain.model.DateCalculationInput
import com.vaddicalculator.app.domain.model.InterestScheme
import com.vaddicalculator.app.domain.model.InterestType
import com.vaddicalculator.app.domain.model.QuickCalculationInput
import com.vaddicalculator.app.domain.util.CurrencyFormatter
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class VaddiCalculationEngineTest {

    /**
     * Case 1:
     * Principal = 1,00,000
     * Monthly Rate = 2%
     * Duration = 6 Months
     * Expected Interest = 12,000
     * Total = 1,12,000
     */
    @Test
    fun testCase1_sixMonthsTwoPercent() {
        val input = QuickCalculationInput(
            principal = 100000.0,
            rate = 2.0,
            interestType = InterestType.MONTHLY,
            duration = 6.0,
            scheme = InterestScheme.SIMPLE
        )
        val result = VaddiCalculationEngine.calculateQuick(input)

        assertEquals(12000.0, result.interestEarned, 0.01)
        assertEquals(112000.0, result.totalAmount, 0.01)
        assertEquals(2000.0, result.monthlyInterest, 0.01)
    }

    /**
     * Case 2:
     * Principal = 50,000
     * Monthly Rate = 1.5%
     * Duration = 12 Months
     * Expected Interest = 9,000
     * Total = 59,000
     */
    @Test
    fun testCase2_twelveMonthsOnePointFivePercent() {
        val input = QuickCalculationInput(
            principal = 50000.0,
            rate = 1.5,
            interestType = InterestType.MONTHLY,
            duration = 12.0,
            scheme = InterestScheme.SIMPLE
        )
        val result = VaddiCalculationEngine.calculateQuick(input)

        assertEquals(9000.0, result.interestEarned, 0.01)
        assertEquals(59000.0, result.totalAmount, 0.01)
        assertEquals(750.0, result.monthlyInterest, 0.01)
    }

    /**
     * Case 3:
     * Principal = 2,00,000
     * Monthly Rate = 3%
     * Duration = 1 Month
     * Expected Interest = 6,000
     * Total = 2,06,000
     */
    @Test
    fun testCase3_oneMonthThreePercent() {
        val input = QuickCalculationInput(
            principal = 200000.0,
            rate = 3.0,
            interestType = InterestType.MONTHLY,
            duration = 1.0,
            scheme = InterestScheme.SIMPLE
        )
        val result = VaddiCalculationEngine.calculateQuick(input)

        assertEquals(6000.0, result.interestEarned, 0.01)
        assertEquals(206000.0, result.totalAmount, 0.01)
        assertEquals(6000.0, result.monthlyInterest, 0.01)
    }

    /**
     * Case 4:
     * Principal = 1,00,000
     * Monthly Rate = 2%
     * Start Date = 01-01-2024
     * End Date = 16-07-2024
     * Duration = 6 Months 15 Days
     * Expected Interest:
     * 6 Months = 12,000
     * 15 Days = 1,000
     * Total Interest = 13,000
     * Total Amount = 1,13,000
     */
    @Test
    fun testCase4_dateBasedSixMonthsFifteenDays() {
        val start = LocalDate.of(2024, 1, 1)
        val end = LocalDate.of(2024, 7, 16)
        val input = DateCalculationInput(
            principal = 100000.0,
            rate = 2.0,
            interestType = InterestType.MONTHLY,
            startDate = start,
            endDate = end,
            scheme = InterestScheme.SIMPLE
        )
        val result = VaddiCalculationEngine.calculateDateBased(input)

        assertEquals(6, result.completedMonths)
        assertEquals(15, result.remainingDays)
        assertEquals(2000.0, result.monthlyInterest, 0.01)
        assertEquals(13000.0, result.interestEarned, 0.01)
        assertEquals(113000.0, result.totalAmount, 0.01)
    }

    /**
     * Case 5:
     * Principal = 75,000
     * Monthly Rate = 2.5%
     * Start Date = 10-02-2023
     * End Date = 25-05-2023
     * Duration = 3 Months 15 Days
     * Monthly Interest = 1,875
     * 15 Days Interest = 937.50
     * Total Interest = 6,562.50
     * Total Amount = 81,562.50
     */
    @Test
    fun testCase5_dateBasedThreeMonthsFifteenDays() {
        val start = LocalDate.of(2023, 2, 10)
        val end = LocalDate.of(2023, 5, 25)
        val input = DateCalculationInput(
            principal = 75000.0,
            rate = 2.5,
            interestType = InterestType.MONTHLY,
            startDate = start,
            endDate = end,
            scheme = InterestScheme.SIMPLE
        )
        val result = VaddiCalculationEngine.calculateDateBased(input)

        assertEquals(3, result.completedMonths)
        assertEquals(15, result.remainingDays)
        assertEquals(1875.0, result.monthlyInterest, 0.01)
        assertEquals(6562.50, result.interestEarned, 0.01)
        assertEquals(81562.50, result.totalAmount, 0.01)
    }

    /**
     * Case 6:
     * Leap Year Date Calculation:
     * Principal = 1,00,000
     * Monthly Rate = 2%
     * Start Date = 01-02-2024
     * End Date = 01-03-2024
     * (Feb 2024 is a leap year with 29 days. 1 full calendar month)
     * Expected Interest = 2,000
     * Total Amount = 1,02,000
     */
    @Test
    fun testCase6_leapYearDateCalculation() {
        val start = LocalDate.of(2024, 2, 1)
        val end = LocalDate.of(2024, 3, 1)
        val input = DateCalculationInput(
            principal = 100000.0,
            rate = 2.0,
            interestType = InterestType.MONTHLY,
            startDate = start,
            endDate = end,
            scheme = InterestScheme.SIMPLE
        )
        val result = VaddiCalculationEngine.calculateDateBased(input)

        assertEquals(1, result.completedMonths)
        assertEquals(0, result.remainingDays)
        assertEquals(29L, result.totalDays)
        assertEquals(2000.0, result.interestEarned, 0.01)
        assertEquals(102000.0, result.totalAmount, 0.01)
    }

    @Test
    fun testYearlyVaddi() {
        val input = QuickCalculationInput(
            principal = 100000.0,
            rate = 12.0,
            interestType = InterestType.YEARLY,
            duration = 2.0,
            scheme = InterestScheme.SIMPLE
        )
        val result = VaddiCalculationEngine.calculateQuick(input)

        assertEquals(24000.0, result.interestEarned, 0.01)
        assertEquals(124000.0, result.totalAmount, 0.01)
    }

    @Test
    fun testDailyVaddi() {
        val input = QuickCalculationInput(
            principal = 10000.0,
            rate = 0.1,
            interestType = InterestType.DAILY,
            duration = 30.0,
            scheme = InterestScheme.SIMPLE
        )
        val result = VaddiCalculationEngine.calculateQuick(input)

        assertEquals(300.0, result.interestEarned, 0.01)
        assertEquals(10300.0, result.totalAmount, 0.01)
    }

    @Test
    fun testIndianNumberFormatting() {
        assertEquals("₹1,00,000", CurrencyFormatter.formatCurrency(100000.0))
        assertEquals("₹12,000", CurrencyFormatter.formatCurrency(12000.0))
        assertEquals("₹1,12,000", CurrencyFormatter.formatCurrency(112000.0))
        assertEquals("₹2,000", CurrencyFormatter.formatCurrency(2000.0))
        assertEquals("₹24,000", CurrencyFormatter.formatCurrency(24000.0))
        assertEquals("₹1,24,000", CurrencyFormatter.formatCurrency(124000.0))
        assertEquals("₹50,000", CurrencyFormatter.formatCurrency(50000.0))
        assertEquals("₹6,000", CurrencyFormatter.formatCurrency(6000.0))
        assertEquals("₹56,000", CurrencyFormatter.formatCurrency(56000.0))
        assertEquals("₹6,562.50", CurrencyFormatter.formatCurrency(6562.50))
        assertEquals("₹81,562.50", CurrencyFormatter.formatCurrency(81562.50))
        assertEquals("-₹1,00,000", CurrencyFormatter.formatCurrency(-100000.0))
    }
}
