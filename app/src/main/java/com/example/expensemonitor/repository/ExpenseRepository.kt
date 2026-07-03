package com.example.expensemonitor.repository

import com.example.expensemonitor.roomdb.ExpenseDao
import com.example.expensemonitor.roomdb.ExpenseEntity
import com.example.expensemonitor.roomdb.MonthlyBudgetEntity
import com.example.expensemonitor.roomdb.SettingsDao

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
