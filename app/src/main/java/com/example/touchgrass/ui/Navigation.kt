package com.example.touchgrass.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.touchgrass.ui.home.HomeScreen
import com.example.touchgrass.ui.stepcounter.StepCounterScreen

object Navigation {
    const val HOME = "home"
    const val STEP_COUNTER = "stepCounter"
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
        composable(Navigation.STEP_COUNTER) {
            StepCounterScreen(
                navController = navController
            )
        }
    }
}