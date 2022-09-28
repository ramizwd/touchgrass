package com.example.touchgrass.ui.stepcounter

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState

@Composable
fun StepCounterScreen(
    viewModel: StepCounterViewModel
) {
    val steps by viewModel.currentSteps.observeAsState()

    StepCounterScreenBody(viewModel = viewModel, steps = steps)
}

@Composable
fun StepCounterScreenBody(
    viewModel: StepCounterViewModel,
    steps: Int?
) {
    Text(text = (steps ?: 0).toString())
}