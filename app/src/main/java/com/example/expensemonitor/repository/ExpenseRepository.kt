package com.example.expensemonitor.repository

import com.example.expensemonitor.roomdb.ExpenseDao
import com.example.expensemonitor.roomdb.ExpenseEntity

class ExpenseRepository(
    private val dao: ExpenseDao
) {

    val expenses = dao.getAllExpenses()

    suspend fun insert(expense: ExpenseEntity) {
        dao.insertExpense(expense)
    }

    suspend fun delete(expense: ExpenseEntity) {
        dao.deleteExpense(expense)
    }
}