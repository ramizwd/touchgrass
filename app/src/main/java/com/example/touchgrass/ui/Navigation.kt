package com.example.touchgrass.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.touchgrass.ui.home.HomeScreen
import com.example.touchgrass.ui.stepcounter.StepCounterScreen
import com.example.touchgrass.ui.stepcounter.StepCounterViewModel

object Navigation {
    const val HOME = "home"
    const val STEP_COUNTER = "stepCounter"
}

@Composable
fun Navigation(
    stepCounterViewModel: StepCounterViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Navigation.STEP_COUNTER, // TODO once done with step counter use HOME
    ) {
        composable(Navigation.HOME) {
            HomeScreen(
                navController = navController
            )
        }
        composable(Navigation.STEP_COUNTER) {
            StepCounterScreen(
                viewModel = stepCounterViewModel
            )
        }
    }
}