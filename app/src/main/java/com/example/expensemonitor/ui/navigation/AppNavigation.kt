package com.example.expensemonitor.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import com.example.expensemonitor.ui.components.GlassBottomBar
import com.example.expensemonitor.ui.dashboard.HomeScreenMain
import com.example.expensemonitor.ui.dashboard.ProfileScreen
import com.example.expensemonitor.ui.dashboard.ReportScreen
import com.example.expensemonitor.ui.expensereport.ExpenseScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route
        ) {

            composable("home") {
                HomeScreenMain()
            }

            composable("expense") {
                ExpenseScreen()
            }

            composable("report") {
                ReportScreen()
            }

            composable("profile") {
                ProfileScreen()
            }
        }

//        Scaffold(
//            containerColor = Color.Transparent,
//            contentWindowInsets = WindowInsets(0.dp),
//            bottomBar = {
//                GlassBottomBar(navController, modifier = Modifier
//                .align(Alignment.BottomCenter)
//                .padding(bottom = 20.dp))
//            }
//        )

        GlassBottomBar(
            navController = navController,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
        )
    }
}