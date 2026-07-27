package com.example.expensemonitor.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "monthtotal")
data class MonthlyBudgetEntity(
    @PrimaryKey
    val id: Int = 1,

    val monthlyBudget: Double,

    val lastResetMonth: Int = 0,

    val lastResetYear: Int = 0
)