package com.example.touchgrass.ui.stepcounter

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.touchgrass.ui.shared.components.CircularProgressBar

@Composable
fun StepCounterScreen(
    viewModel: StepCounterViewModel
) {
    val steps by viewModel.currentSteps.observeAsState()

    var expanded by remember { mutableStateOf(false) }
    val targetStepsList = listOf(500, 1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 10000)
    var selectedIndex by remember { mutableStateOf(0) }
    val stepsTarget = targetStepsList[selectedIndex].toFloat()

    StepCounterScreenBody(
        steps = steps,
        stepsTarget = stepsTarget,
        expanded = expanded,
        onExpanded = { expanded = it },
        targetStepsList = targetStepsList,
        onSelectedIndex = { selectedIndex = it }
    )
}

@Composable
fun StepCounterScreenBody(
    steps: Int?,
    stepsTarget: Float,
    targetStepsList: List<Int>,
    expanded: Boolean,
    onExpanded: (Boolean) -> Unit,
    onSelectedIndex: (Int) -> Unit,
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
                    number = stepsTarget.toInt(),
                    color = if ((steps ?: 0) >= stepsTarget) Color.Green else Color.Blue
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.TopEnd)
            ) {
                IconButton(onClick = { onExpanded(true) }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "TargetSteps")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { onExpanded(false) }
                ) {
                    targetStepsList.forEachIndexed { index, value ->
                        DropdownMenuItem(onClick = {
                            onSelectedIndex(index)
                            onExpanded(false)
                        }) {
                            Text(text = value.toString())
                        }
                    }
                }
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


