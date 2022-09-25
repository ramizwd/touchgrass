package com.example.touchgrass.ui.stepcounter

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun StepCounterScreen(navController: NavController) {
    StepCounterScreenBody(navController = navController)
}

@Composable
fun StepCounterScreenBody(navController: NavController) {
    Text(text = "StepCounterScreen")
}