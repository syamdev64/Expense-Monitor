package com.example.expensemonitor.repository

import com.example.expensemonitor.roomdb.MonthlyBudgetEntity
import com.example.expensemonitor.roomdb.SettingsDao
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val dao: SettingsDao,
    private val firestore: FirebaseFirestore
) {

    val settings = dao.getSettings()

    suspend fun updateBiometricPreference(enabled: Boolean) {
        val currentSettings = settings.first() ?: MonthlyBudgetEntity(id = 1, monthlyBudget = 0.0)
        dao.saveSettings(currentSettings.copy(isBiometricEnabled = enabled))
    }

    suspend fun saveBudget(uid: String?, budget: Double) {
        dao.saveSettings(
            MonthlyBudgetEntity(
                id = 1,
                monthlyBudget = budget
            )
        )

        uid?.let {
            firestore.collection("users")
                .document(it)
                .collection("settings")
                .document("budget")
                .set(
                    mapOf(
                        "monthlyBudget" to budget
                    )
                )
                .await()
        }
    }
}
