package com.example.expensemonitor.repository

import com.example.expensemonitor.cloud.ExpenseFirestore
import com.example.expensemonitor.cloud.ExpenseFirestoreData
import com.example.expensemonitor.roomdb.ExpenseEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()

//    suspend fun insertExpense(expense: ExpenseEntity) {
//
//        db.collection("expenses")
//            .document(expense.id.toString())
//            .set(expense)
//            .await()
//
//    }
suspend fun insertExpense(expense: ExpenseEntity) {

    val firestoreExpense = ExpenseFirestoreData(

        amount = expense.amount,

        category = expense.category,

        description = expense.description,

        date = expense.date

    )

    db.collection("expenses")
        .document(expense.id.toString())
        .set(firestoreExpense)
        .await()
}

    suspend fun deleteExpense(id: Int) {

        db.collection("expenses")
            .document(id.toString())
            .delete()
            .await()

    }

    suspend fun updateExpense(expense: ExpenseEntity) {

        db.collection("expenses")
            .document(expense.id.toString())
            .set(expense)
            .await()

    }

    fun saveExpense(
        expense: ExpenseFirestore,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        db.collection("expenses")
            .add(expense)
            .addOnSuccessListener {

                onSuccess()

            }
            .addOnFailureListener {

                onFailure(it)

            }

    }

    fun observeExpenses(
        onResult: (List<ExpenseFirestore>) -> Unit
    ) {

        db.collection("expenses")
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

