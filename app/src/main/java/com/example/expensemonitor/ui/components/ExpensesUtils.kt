package com.example.expensemonitor.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

object ExpenseUtils {

//    fun getIcon(category: String): ImageVector {
//        return when (category) {
//            "🍔 Food" -> Icons.Default.Fastfood
//            "☕ Coffee" -> Icons.Default.Coffee
//            "🚕 Travel" -> Icons.Default.DirectionsCar
//            "🛍 Shopping" -> Icons.Default.ShoppingBag
//            "💡 Bills" -> Icons.Default.Receipt
//            "🏥 Medical" -> Icons.Default.LocalHospital
//            else -> Icons.Default.Payments
//        }
//    }

    fun getIcon(category: String): ImageVector {
        return when (category) {
            "Food" -> Icons.Default.Fastfood
            "Coffee" -> Icons.Default.Coffee
            "Travel" -> Icons.Default.DirectionsCar
            "Shopping" -> Icons.Default.ShoppingBag
            "Bills" -> Icons.Default.Receipt
            "Medical" -> Icons.Default.LocalHospital
            else -> Icons.Default.Payments
        }
    }

    fun getColor(category: String): Color {
        return when (category) {
            "🍔 Food" -> Color(0xFF4CAF50)
            "☕ Coffee" -> Color(0xFF795548)
            "🚕 Travel" -> Color(0xFF2196F3)
            "🛍 Shopping" -> Color(0xFFE91E63)
            "💡 Bills" -> Color(0xFFFF9800)
            "🏥 Medical" -> Color.Red
            else -> Color.Gray
        }
    }
}