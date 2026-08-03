package com.example.expensemonitor.modelclass

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class Expense(
    val id: Int,
    val category: String,
    val description: String,
    val amount: Double,
    val date: String,
    val time: String,
    val icon: ImageVector,
    val color: Color
)