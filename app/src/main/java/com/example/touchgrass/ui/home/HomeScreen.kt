package com.example.touchgrass.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.touchgrass.R
import com.example.touchgrass.ui.Navigation
import com.example.touchgrass.ui.shared.components.CircularProgressBar

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(start = 14.dp)
                            .clickable {
                                navController.navigate(Navigation.STEP_COUNTER)
                            }
                            .size(114.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colors.primary)

                    ) {
                        Text(
                            text = stringResource(R.string.step_counter),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clickable {
                                navController.navigate(Navigation.HYDRATION)
                            }
                            .size(114.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colors.primary)
                    ) {
                        Text(
                            text = stringResource(R.string.hydration),
                            modifier = Modifier.padding(8.dp))

                    }
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(end = 14.dp)
                            .clickable {
                                navController.navigate(Navigation.HEART_RATE_MONITOR)
                            }
                            .size(114.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colors.primary)
                    ) {
                        Text(
                            text = stringResource(R.string.hr_monitor),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}