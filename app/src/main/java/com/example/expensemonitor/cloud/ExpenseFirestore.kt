package com.example.expensemonitor.cloud

import com.google.firebase.firestore.DocumentId

data class ExpenseFirestore(
    @DocumentId
    val id: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val description: String = "",
    val date: Long = 0L
)