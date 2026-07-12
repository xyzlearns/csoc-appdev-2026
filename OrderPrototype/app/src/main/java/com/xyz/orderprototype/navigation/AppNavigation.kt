package com.xyz.orderprototype.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.xyz.orderprototype.ui.screens.CartScreen
import com.xyz.orderprototype.ui.screens.HomeScreen
import com.xyz.orderprototype.ui.screens.LoginScreen
import com.xyz.orderprototype.ui.screens.MenuScreen
import com.xyz.orderprototype.ui.screens.RegisterScreen
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.xyz.orderprototype.ui.screens.OrdersScreen
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import com.xyz.orderprototype.data.local.DataStoreManager
import com.xyz.orderprototype.ui.screens.ProfileScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier
) {

    val navController = rememberNavController()
    val context = LocalContext.current
    val dataStoreManager = DataStoreManager(context)

    val token by dataStoreManager
        .tokenFlow
        .collectAsState(initial = "LOADING")

    if (token == "LOADING") {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        return
    }

    NavHost(
        navController = navController,
        startDestination =
            if (token.isNullOrEmpty()) {
                Screen.Login.route
            } else {
                Screen.Home.route
            },
        modifier = modifier
    ) {

        composable(Screen.Home.route) {
            HomeScreen(navController)
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {

            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Login.route)
                },
                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.Menu.route
        ) { backStackEntry ->

            val restaurantId =
                backStackEntry.arguments
                    ?.getString("restaurantId")
                    ?: ""

            MenuScreen(
                navController = navController,
                restaurantId = restaurantId
            )
        }

        composable(Screen.Cart.route) {
            CartScreen(navController)
        }

        composable(Screen.Profile.route) {

            ProfileScreen(
                onOrdersClick = {
                    navController.navigate(
                        Screen.Orders.route
                    )
                },
                onLogout = {

                    navController.navigate(
                        Screen.Login.route
                    ) {
                        popUpTo(0)
                    }
                }
            )
        }

        composable(Screen.Orders.route) {
            OrdersScreen()
        }
    }
}
