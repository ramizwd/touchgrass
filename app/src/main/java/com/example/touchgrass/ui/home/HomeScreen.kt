package com.example.touchgrass.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    Column() {
        Text(text = "HomeScreen")
        Text(
            text = "HydrationScreen",
            modifier = Modifier.selectable(
                selected = true,
                onClick = { navController.navigate(Navigation.HYDRATION) })
        )
    }

}