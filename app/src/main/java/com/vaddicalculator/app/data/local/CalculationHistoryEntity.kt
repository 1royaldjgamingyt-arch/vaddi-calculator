package com.vaddicalculator.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculation_history")
data class CalculationHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val principal: Double,
    val rate: Double,
    val interestType: String,      // MONTHLY, YEARLY, DAILY
    val calculatorMode: String,    // QUICK, DATE_BASED
    val interestScheme: String = "SIMPLE",
    val durationText: String,
    val monthlyInterest: Double = 0.0,
    val startDate: String? = null,
    val endDate: String? = null,
    val interestEarned: Double,
    val totalAmount: Double,
    val currencyCode: String = "INR",
    val formulaUsed: String? = null
)
