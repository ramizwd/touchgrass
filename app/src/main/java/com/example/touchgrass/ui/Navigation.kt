package com.example.touchgrass.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.touchgrass.ui.home.HomeScreen

object Navigation {
    const val HOME = "home"
}

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Navigation.HOME,
    ) {
        composable(Navigation.HOME) {
            HomeScreen(
                navController = navController
            )
        }
    }

}