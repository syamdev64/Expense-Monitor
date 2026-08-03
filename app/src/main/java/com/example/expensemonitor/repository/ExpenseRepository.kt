package com.example.expensemonitor.repository

import com.example.expensemonitor.roomdb.ExpenseDao
import com.example.expensemonitor.roomdb.ExpenseEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ExpenseRepository @Inject constructor(
    private val dao: ExpenseDao,
    private val firestoreRepository: FirestoreRepository,
    private val authRepository: AuthRepository
) {

    val expenses = dao.getAllExpenses()

    suspend fun insert(expense: ExpenseEntity) {
        val id = dao.insertExpense(expense)
        authRepository.currentUserId?.let { uid ->
            firestoreRepository.insertExpense(
                uid,
                expense.copy(id = id.toInt())
            )
        }
    }

    suspend fun delete(expense: ExpenseEntity) {
        dao.deleteExpense(expense)
        authRepository.currentUserId?.let { uid ->
            firestoreRepository.deleteExpense(uid, expense.id)
        }
    }

    suspend fun update(expense: ExpenseEntity) {
        dao.updateExpense(expense)
        authRepository.currentUserId?.let { uid ->
            firestoreRepository.updateExpense(uid, expense)
        }
    }

    fun startFirestoreSync() {
        authRepository.currentUserId?.let { uid ->
            firestoreRepository.observeExpenses(uid) { firestoreList ->
                CoroutineScope(Dispatchers.IO).launch {
                    val entities = firestoreList.map {
                        ExpenseEntity(
                            id = it.id.toInt(),
                            amount = it.amount,
                            category = it.category,
                            description = it.description,
                            date = it.date
                        )
                    }
                    dao.insertAll(entities)
                }
            }
        }
    }

    suspend fun clearLocalData() {
        dao.deleteAllExpenses()
    }

    fun getCurrentMonthExpense(
        startDate: Long,
        endDate: Long
    ): Flow<Double> {
        return dao.getCurrentMonthExpense(startDate, endDate)
    }
}
