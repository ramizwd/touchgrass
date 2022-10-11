package com.example.touchgrass.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.touchgrass.ui.Navigation
import com.example.touchgrass.ui.shared.components.CircularProgressBar

object HomeConstants {
    const val totalMinutes = 1440f
}

/**
 * Stateful Composable which manages state
 */
@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel) {
    val currentMinutes by viewModel.currentTotalMinutes.observeAsState()
    val streaks by viewModel.streaks.observeAsState()
    HomeScreenBody(navController = navController, currentMinutes = currentMinutes,streaks = streaks)
}

/**
 * Composable for displaying the Home Screen
 */
@Composable
fun HomeScreenBody(
    navController: NavController,
    currentMinutes: Int?,
    streaks: Float?
) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressBar(
                    percentage = (currentMinutes ?: 0) / HomeConstants.totalMinutes,
                    number = 0,
                    color = if ((currentMinutes ?: 0) == 0) Color.Red else Color.Black
                )
            }
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column {
                Text(text = streaks.toString())
                Button(onClick = {
                    navController.navigate(Navigation.STEP_COUNTER)
                }) { Text(text = "STEP COUNTER") }
                Button(onClick = {
                    navController.navigate(Navigation.HYDRATION)
                }) { Text(text = "HYDRATION") }
                Button(onClick = {
                    navController.navigate(Navigation.HEART_RATE_MONITOR)
                }) { Text(text = "HEART RATE MONITOR") }
            }
        }
    }
}