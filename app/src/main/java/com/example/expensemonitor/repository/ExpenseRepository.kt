package com.example.expensemonitor.repository

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.expensemonitor.roomdb.ExpenseDao
import com.example.expensemonitor.roomdb.ExpenseEntity
import com.example.expensemonitor.roomdb.MonthlyBudgetEntity
import com.example.expensemonitor.roomdb.SettingsDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExpenseRepository(
    private val dao: ExpenseDao,
    private val firestoreRepository: FirestoreRepository
)

{

    val expenses = dao.getAllExpenses()


//    suspend fun insert(expense: ExpenseEntity) {
//        dao.insertExpense(expense)
//    }

//    suspend fun delete(expense: ExpenseEntity) {
//        dao.deleteExpense(expense)
//    }
    suspend fun insert(expense: ExpenseEntity) {

        val id = dao.insertExpense(expense)

        firestoreRepository.insertExpense(
            expense.copy(id = id.toInt())
        )

    }
    suspend fun delete(expense: ExpenseEntity) {

        dao.deleteExpense(expense)

        firestoreRepository.deleteExpense(expense.id)

    }
    suspend fun update(expense: ExpenseEntity) {

        dao.updateExpense(expense)

        firestoreRepository.updateExpense(expense)

    }
    fun startFirestoreSync() {

        firestoreRepository.observeExpenses { firestoreList ->

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
