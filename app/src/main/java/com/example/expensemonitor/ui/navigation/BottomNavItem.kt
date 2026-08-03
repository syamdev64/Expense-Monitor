package com.example.expensemonitor.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {

    object Home : BottomNavItem(
        "home",
        "Home",
        Icons.Default.Home
    )

    object Expense : BottomNavItem(
        "expense",
        "Expenses",
        Icons.Default.List
    )

    object Report : BottomNavItem(
        "report",
        "Reports",
        Icons.Default.BarChart
    )

    object Profile : BottomNavItem(
        "profile",
        "Profile",
        Icons.Default.Person
    )
}