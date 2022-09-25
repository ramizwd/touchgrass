package com.example.touchgrass.ui.home

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

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
    Text(text = "HomeScreen")
}