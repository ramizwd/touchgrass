package com.example.touchgrass.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.touchgrass.ui.Navigation

/**
 * Stateful Composable which manages state
 */
@Composable
fun HomeScreen(navController: NavController) {
    HomeScreenBody(navController = navController)
}

/**
 * Composable for displaying the Home Screen
 */
@Composable
fun HomeScreenBody(
    navController: NavController
) {
    Column {
        Text(text = "HomeScreen")
        Button(onClick = {
            navController.navigate(Navigation.STEP_COUNTER)
        }) { Text(text = "STEP COUNTER") }
    }
}