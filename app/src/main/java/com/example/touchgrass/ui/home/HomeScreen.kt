package com.example.touchgrass.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.toUpperCase
import androidx.navigation.NavController
import com.example.touchgrass.R
import com.example.touchgrass.ui.Navigation
import com.example.touchgrass.ui.shared.components.CircularProgressBar
import java.util.*

object HomeConstants {
    const val TOTAL_MINUTES_OF_DAY = 1440f
}

/**
 * Stateful Composable which manages state
 */
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel
) {
    val currentMinutes by viewModel.currentTotalMinutes.observeAsState()
    val streak by viewModel.streak.observeAsState()

    HomeScreenBody(
        navController = navController,
        currentMinutes = currentMinutes,
        streak = streak
    )
}

/**
 * Composable for displaying the Home Screen
 */
@Composable
fun HomeScreenBody(
    navController: NavController,
    currentMinutes: Int?,
    streak: Float?
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.home)) }
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.5f)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressBar(
                        value = (currentMinutes ?: 0) / HomeConstants.TOTAL_MINUTES_OF_DAY,
                        target = 0,
                        streak = streak
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
                    Button(onClick = {
                        navController.navigate(Navigation.STEP_COUNTER)
                    }) { Text(text = stringResource(R.string.step_counter).uppercase()) }
                    Button(onClick = {
                        navController.navigate(Navigation.HYDRATION)
                    }) { Text(text = stringResource(R.string.hydration).uppercase()) }
                    Button(onClick = {
                        navController.navigate(Navigation.HEART_RATE_MONITOR)
                    }) { Text(text = stringResource(R.string.hr_monitor).uppercase()) }
                }
            }
        }
    }
}