package com.example.touchgrass.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
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

/**
 * Stateful Composable which manages state
 */
@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel) {
    val currentHour by viewModel.currentHour.observeAsState()

    HomeScreenBody(navController = navController, currentHour = currentHour)
}

/**
 * Composable for displaying the Home Screen
 */
@Composable
fun HomeScreenBody(
    navController: NavController,
    currentHour: Int?
) {

    var placeholder = 1
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
                    percentage = (placeholder ?: 0) / 1f,
                    number = 0,
                    color = if ((placeholder ?: 0) >= placeholder) Color.Red else Color.Black
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
                }) { Text(text = "STEP COUNTER") }
                Button(onClick = {
                    navController.navigate(Navigation.HYDRATION)
                }) { Text(text = "HYDRATION") }
            }
        }
    }
}