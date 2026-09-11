package com.vaddicalculatortool.app.data.local

import kotlinx.coroutines.flow.Flow

class CalculationHistoryRepository(
    private val dao: CalculationHistoryDao
) {
    val allHistory: Flow<List<CalculationHistoryEntity>> = dao.getAllHistory()

    suspend fun insert(entity: CalculationHistoryEntity): Long {
        return dao.insertHistory(entity)
    }

    suspend fun deleteById(id: Long) {
        dao.deleteHistoryById(id)
    }

    suspend fun clearAll() {
        dao.clearAllHistory()
    }
}
