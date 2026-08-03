package com.example.expensemonitor.expenseviewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.expensemonitor.repository.AuthRepository
import com.example.expensemonitor.repository.ExpenseRepository
import com.example.expensemonitor.repository.SettingsRepository
import com.example.expensemonitor.roomdb.ExpenseEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val repository: ExpenseRepository,
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val expenses: LiveData<List<ExpenseEntity>> =
        repository.expenses.asLiveData()

    val settings =
        settingsRepository.settings.asLiveData()

    private val monthRange = getCurrentMonthRange()
    private val startDate = monthRange.first
    private val endDate = monthRange.second
    val currentMonthExpense: LiveData<Double> =
        repository.getCurrentMonthExpense(startDate, endDate)
            .asLiveData()

    init {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                if (user != null) {
                    repository.startFirestoreSync()
                }
            }
        }
    }

    fun insert(
        amount: Double,
        category: String,
        description: String,
        date: Long
    ) {
        viewModelScope.launch {
            repository.insert(
                ExpenseEntity(
                    amount = amount,
                    category = category,
                    description = description,
                    date = date
                )
            )
        }
    }

    fun delete(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.delete(expense)
        }
    }

    fun saveBudget(budget: Double) {
        viewModelScope.launch {
            settingsRepository.saveBudget(authRepository.currentUserId, budget)
        }
    }

    fun clearDataOnLogout() {
        viewModelScope.launch {
            repository.clearLocalData()
        }
    }

    private fun getCurrentMonthRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis
        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.MILLISECOND, -1)
        val end = calendar.timeInMillis
        return Pair(start, end)
    }
}
