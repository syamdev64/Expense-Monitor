package com.example.expensemonitor.categorylist

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class ExpenseCategory(

    val title: String,

    val icon: ImageVector,

    val color: Color

) {

    FOOD(
        "Food",
        Icons.Default.Fastfood,
        Color(0xFF4CAF50)
    ),

    TRAVEL(
        "Travel",
        Icons.Default.DirectionsCar,
        Color(0xFF2196F3)
    ),

    SHOPPING(
        "Shopping",
        Icons.Default.ShoppingBag,
        Color(0xFFE91E63)
    ),

    BILLS(
        "Bills",
        Icons.Default.Receipt,
        Color(0xFFFF9800)
    ),

    MEDICAL(
        "Medical",
        Icons.Default.LocalHospital,
        Color.Red
    ),

    COFFEE(
        "Coffee",
        Icons.Default.Coffee,
        Color(0xFF795548)
    ),

    EDUCATION(
        "Education",
        Icons.Default.School,
        Color(0xFF9C27B0)
    ),

    OTHER(
        "Other",
        Icons.Default.Payments,
        Color.Gray
    );

    companion object {

        fun fromName(name: String): ExpenseCategory {

            return entries.firstOrNull {

                it.name == name

            } ?: OTHER

        }

    }

}