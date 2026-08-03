package com.example.expensemonitor.repository

import com.example.expensemonitor.cloud.ExpenseFirestore
import com.example.expensemonitor.cloud.ExpenseFirestoreData
import com.example.expensemonitor.roomdb.ExpenseEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreRepository @Inject constructor(
    private val db: FirebaseFirestore
) {

    private fun userExpensesCollection(uid: String) =
        db.collection("users").document(uid).collection("expenses")

    private fun userProfileDocument(uid: String) =
        db.collection("users").document(uid)

    suspend fun savePhoneNumber(uid: String, phoneNumber: String) {
        userProfileDocument(uid)
            .set(mapOf("phoneNumber" to phoneNumber), com.google.firebase.firestore.SetOptions.merge())
            .await()
    }

    suspend fun saveUserProfile(uid: String, name: String, email: String, phoneNumber: String, photoUrl: String?) {
        val profileData = mapOf(
            "uid" to uid,
            "displayName" to name,
            "email" to email,
            "phoneNumber" to phoneNumber,
            "photoUrl" to (photoUrl ?: "")
        )
        userProfileDocument(uid)
            .set(profileData, com.google.firebase.firestore.SetOptions.merge())
            .await()
    }

    suspend fun getUserPhoneNumber(uid: String): String? {
        return try {
            val snapshot = userProfileDocument(uid).get().await()
            snapshot.getString("phoneNumber")
        } catch (e: Exception) {
            null
        }
    }

    suspend fun insertExpense(uid: String, expense: ExpenseEntity) {
        val firestoreExpense = ExpenseFirestoreData(
            amount = expense.amount,
            category = expense.category,
            description = expense.description,
            date = expense.date
        )

        userExpensesCollection(uid)
            .document(expense.id.toString())
            .set(firestoreExpense)
            .await()
    }

    suspend fun deleteExpense(uid: String, id: Int) {
        userExpensesCollection(uid)
            .document(id.toString())
            .delete()
            .await()
    }

    suspend fun updateExpense(uid: String, expense: ExpenseEntity) {
        userExpensesCollection(uid)
            .document(expense.id.toString())
            .set(expense)
            .await()
    }

    fun observeExpenses(
        uid: String,
        onResult: (List<ExpenseFirestore>) -> Unit
    ) {
        userExpensesCollection(uid)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val list = snapshot
                    ?.toObjects(ExpenseFirestore::class.java)
                    ?: emptyList()
                onResult(list)
            }
    }
}
