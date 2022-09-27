package com.example.touchgrass.ui.stepcounter

import android.util.Log
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState

@Composable
fun StepCounterScreen(
    viewModel: StepCounterViewModel
) {
    StepCounterScreenBody(viewModel = viewModel)
}

@Composable
fun StepCounterScreenBody(
    viewModel: StepCounterViewModel,
) {
    val steps by viewModel.steps.observeAsState()
    Log.d("StepCounter", "HERE: $steps")

    Text(text = (steps ?: 0).toString())
}