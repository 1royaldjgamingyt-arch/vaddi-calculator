package com.vaddicalculator.app.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vaddicalculator.app.data.local.AppDatabase
import com.vaddicalculator.app.data.local.CalculationHistoryEntity
import com.vaddicalculator.app.data.local.CalculationHistoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: CalculationHistoryRepository =
        CalculationHistoryRepository(AppDatabase.getDatabase(application).calculationHistoryDao())

    val historyItems: StateFlow<List<CalculationHistoryEntity>> = repository.allHistory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteItem(id: Long) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }
}
