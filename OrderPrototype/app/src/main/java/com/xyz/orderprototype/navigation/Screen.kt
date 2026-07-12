package com.xyz.orderprototype.navigation

sealed class Screen(val route: String) {

    object Home : Screen("home")

    object Menu : Screen("menu/{restaurantId}") {

        fun createRoute(restaurantId: String): String {
            return "menu/$restaurantId"
        }
    }

    object Cart : Screen("cart")

    object Login : Screen("login")
    object Register : Screen("register")
    object Profile : Screen("profile")
    object Orders : Screen("orders")
}