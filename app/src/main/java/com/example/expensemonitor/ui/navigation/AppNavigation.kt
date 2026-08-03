package com.example.expensemonitor.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import androidx.fragment.app.FragmentActivity
import com.example.expensemonitor.auth.LoginScreen
import com.example.expensemonitor.auth.RegisterScreen
import com.example.expensemonitor.auth.MPINScreen
import com.example.expensemonitor.auth.AuthViewModel
import com.example.expensemonitor.ui.components.GlassBottomBar
import com.example.expensemonitor.ui.dashboard.HomeScreenMain
import com.example.expensemonitor.ui.dashboard.ProfileScreen
import com.example.expensemonitor.ui.dashboard.ReportScreen
import com.example.expensemonitor.ui.expensereport.ExpenseScreen
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.first

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val authViewModel: AuthViewModel = hiltViewModel(context as FragmentActivity)
    
    val user by authViewModel.currentUser.collectAsState()
    val isFirstTime by authViewModel.isFirstTime.collectAsState(initial = true)
    val savedMpin by authViewModel.mpin.collectAsState(initial = null)
    val isRememberMe by authViewModel.isRememberMeEnabled.collectAsState(initial = null)

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        BottomNavItem.Home.route,
        BottomNavItem.Expense.route,
        BottomNavItem.Report.route,
        BottomNavItem.Profile.route
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        NavHost(
            navController = navController,
            startDestination = "splash"
        ) {
            composable("splash") {
                LaunchedEffect(isFirstTime, user, savedMpin, isRememberMe) {
                    if (savedMpin != null && isRememberMe != null) { // Wait for DataStore to load
                        val dest = when {
                            isFirstTime -> "register"
                            user == null -> "login"
                            savedMpin!!.isNotEmpty() -> "mpin_login"
                            else -> BottomNavItem.Home.route
                        }
                        navController.navigate(dest) {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                }
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF00C853))
                }
            }

            composable("home") {
                HomeScreenMain(authViewModel)
            }

            composable("expense") {
                ExpenseScreen(authViewModel)
            }

            composable("report") {
                ReportScreen(authViewModel)
            }

            composable("profile") {
                ProfileScreen(
                    authViewModel = authViewModel,
                    onNavigateToMpinSetup = {
                        navController.navigate("mpin_setup")
                    }
                )
            }

            composable("login") {
                LoginScreen(
                    viewModel = authViewModel,
                    onLoginSuccess = {
                        // The savedMpin state should be updated by now or soon
                        if (!savedMpin.isNullOrEmpty()) {
                            navController.navigate("mpin_login") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            navController.navigate("mpin_setup") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    },
                    onRegisterClick = {
                        navController.navigate("register")
                    },
                    onMpinLoginClick = {
                        navController.navigate("mpin_login")
                    }
                )
            }

            composable("register") {
                RegisterScreen(
                    viewModel = authViewModel,
                    onRegisterSuccess = {
                        navController.navigate("mpin_setup") {
                            popUpTo("register") { inclusive = true }
                        }
                    },
                    onBackToLogin = {
                        navController.navigate("login") {
                            popUpTo("register") { inclusive = true }
                        }
                    }
                )
            }

            composable("mpin_setup") {
                MPINScreen(
                    viewModel = authViewModel,
                    isSetup = true,
                    onSuccess = {
                        navController.navigate(BottomNavItem.Home.route) {
                            popUpTo("mpin_setup") { inclusive = true }
                        }
                    }
                )
            }

            composable("mpin_login") {
                MPINScreen(
                    viewModel = authViewModel,
                    isSetup = false,
                    onSuccess = {
                        navController.navigate(BottomNavItem.Home.route) {
                            popUpTo("mpin_login") { inclusive = true }
                        }
                    }
                )
            }
        }

        if (showBottomBar) {
            GlassBottomBar(
                navController = navController,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp)
            )
        }
    }
}
