package com.example.touchgrass.ui.stepcounter

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.touchgrass.ui.shared.components.CircularProgressBar

@Composable
fun StepCounterScreen(
    viewModel: StepCounterViewModel
) {
    val steps by viewModel.currentSteps.observeAsState()
    var stepsTarget = 500f

    StepCounterScreenBody(steps = steps, stepsTarget = stepsTarget)
}

@Composable
fun StepCounterScreenBody(
    steps: Int?,
    stepsTarget: Float
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
            Column {
                    CircularProgressBar(
                        percentage = (steps ?: 0) / stepsTarget,
                        number = stepsTarget.toInt()
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

            }
        }
    }
}


