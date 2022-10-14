package com.example.touchgrass.ui.stepcounter

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.touchgrass.R
import com.example.touchgrass.service.StepCounterService.Companion.isSensorOn
import com.example.touchgrass.ui.shared.components.CircularProgressBar

@Composable
fun StepCounterScreen(
    viewModel: StepCounterViewModel,
    stepsGraphViewModel: StepsGraphViewModel,
    navController: NavController
) {
    val steps by viewModel.currentSteps.observeAsState()
    val targetSteps by viewModel.targetStepsIndex.observeAsState()

    var expanded by remember { mutableStateOf(false) }
    val targetStepsList = mutableListOf<Int>()

    for (i in 10..100 step 10) targetStepsList.add(i)
    var selectedIndex by remember { mutableStateOf(targetSteps?.toInt() ?: 0) }
    val stepsTarget = targetStepsList[selectedIndex].toFloat()

    StepCounterScreenBody(
        viewModel = viewModel,
        stepsGraphViewModel = stepsGraphViewModel,
        steps = steps,
        stepsTarget = stepsTarget,
        expanded = expanded,
        onExpanded = { expanded = it },
        targetStepsList = targetStepsList,
        onSelectedIndex = { selectedIndex = it },
        navController = navController,
    )
}

@Composable
fun StepCounterScreenBody(
    viewModel: StepCounterViewModel,
    stepsGraphViewModel: StepsGraphViewModel,
    steps: Int?,
    stepsTarget: Float,
    targetStepsList: List<Int>,
    expanded: Boolean,
    onExpanded: (Boolean) -> Unit,
    onSelectedIndex: (Int) -> Unit,
    navController: NavController,
) {

    viewModel.onTargetStepsValueUpdate(stepsTarget)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.step_counter)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_arrow_ic_desc)
                        )
                    }
                },
                actions = {
                    Text(
                        text = stringResource(R.string.set_steps_target),
                        modifier = Modifier
                            .padding(12.dp)
                            .selectable(
                                selected = true,
                                onClick = {
                                    onExpanded(true)
                                }
                            )

                    )
                }
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
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressBar(
                        value = (steps ?: 0) / stepsTarget,
                        target = stepsTarget.toInt(),
                        foregroundColor = if ((steps ?: 0) >= stepsTarget)
                            MaterialTheme.colors.primary
                        else
                            Color(0xFFDDFC74),
                        isSensorOn = isSensorOn,
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
                    StepCounterGraph(stepsGraphViewModel)
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.TopEnd)
        ) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpanded(false) }
            ) {
                targetStepsList.forEachIndexed { index, value ->
                    DropdownMenuItem(onClick = {
                        viewModel.onTargetStepsIndexUpdate(index.toFloat())
                        viewModel.onTargetStepsValueUpdate(value.toFloat())
                        onSelectedIndex(index)
                        onExpanded(false)
                    }) {
                        Text(text = value.toString())
                    }
                }
            }
        }
    }
}




