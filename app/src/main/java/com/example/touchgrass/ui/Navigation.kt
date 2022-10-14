package com.example.touchgrass.ui

import android.bluetooth.BluetoothAdapter
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.touchgrass.ui.heartratemonitor.HeartRateMonitorScreen
import com.example.touchgrass.ui.heartratemonitor.HeartRateMonitorViewModel
import com.example.touchgrass.ui.home.HomeScreen
import com.example.touchgrass.ui.home.HomeViewModel
import com.example.touchgrass.ui.hydration.HydrationScreen
import com.example.touchgrass.ui.hydration.HydrationViewModel
import com.example.touchgrass.ui.stepcounter.StepCounterScreen
import com.example.touchgrass.ui.stepcounter.StepCounterViewModel
import com.example.touchgrass.ui.stepcounter.StepsGraphViewModel

object Navigation {
    const val HOME = "home"
    const val STEP_COUNTER = "stepCounter"
    const val HYDRATION = "hydration"
    const val HEART_RATE_MONITOR = "heartRateMonitor"
}

@Composable
fun Navigation(
    stepCounterViewModel: StepCounterViewModel,
    homeViewModel: HomeViewModel,
    heartRateMonitorViewModel: HeartRateMonitorViewModel,
    bluetoothAdapter: BluetoothAdapter?,
    hydrationViewModel: HydrationViewModel,
    stepsGraphViewModel: StepsGraphViewModel,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Navigation.HOME,
    ) {
        composable(Navigation.HOME) {
            HomeScreen(
                navController = navController,
                viewModel = homeViewModel
            )
        }
        composable(Navigation.HYDRATION) {
            HydrationScreen(
                hydrationViewModel = hydrationViewModel,
                navController = navController,
            )
        }
        composable(Navigation.STEP_COUNTER) {
            StepCounterScreen(
                viewModel = stepCounterViewModel,
                stepsGraphViewModel = stepsGraphViewModel,
                navController = navController,
            )
        }
        composable(Navigation.HEART_RATE_MONITOR) {
            HeartRateMonitorScreen(
                viewModel = heartRateMonitorViewModel,
                bluetoothAdapter = bluetoothAdapter,
                navController = navController,
            )
        }
    }
}