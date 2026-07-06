package com.example.expensemonitor.cloud


data class ExpenseFirestoreData(

    val amount: Double = 0.0,

    val category: String = "",

    val description: String = "",

    val date: Long = 0L

)