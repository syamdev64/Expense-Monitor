package com.example.expensemonitor.repository

import com.example.expensemonitor.roomdb.MonthlyBudgetEntity
import com.example.expensemonitor.roomdb.SettingsDao

class SettingsRepository(
    private val dao: SettingsDao
) {

    val settings = dao.getSettings()

    suspend fun saveBudget(budget: Double) {

        dao.saveSettings(
            MonthlyBudgetEntity(
                id = 1,
                monthlyBudget = budget
            )
        )

    }

}