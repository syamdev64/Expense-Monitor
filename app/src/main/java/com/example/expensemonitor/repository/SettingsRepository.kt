package com.example.expensemonitor.repository

import com.example.expensemonitor.roomdb.MonthlyBudgetEntity
import com.example.expensemonitor.roomdb.SettingsDao
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SettingsRepository(
    private val dao: SettingsDao,  private val firestore: FirebaseFirestore
) {

    val settings = dao.getSettings()

    suspend fun saveBudget(budget: Double) {

        dao.saveSettings(
            MonthlyBudgetEntity(
                id = 1,
                monthlyBudget = budget
            )
        )

        firestore.collection("settings")
            .document("budget")
            .set(
                mapOf(
                    "monthlyBudget" to budget
                )
            )
            .await()
    }

}