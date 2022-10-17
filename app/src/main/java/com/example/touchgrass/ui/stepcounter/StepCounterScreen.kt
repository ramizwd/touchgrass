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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.touchgrass.R
import com.example.touchgrass.service.StepCounterService.Companion.isSensorOn
import com.example.touchgrass.ui.shared.components.CircularProgressBar
import com.example.touchgrass.ui.theme.SCYellow

/**
 * Stateful composable which manages state.
 *
 * @param viewModel StepCounter ViewModel providing LiveData for the StepCounter screen composable.
 * @param stepsGraphViewModel ViewModel for the [StepCounterGraph] composable.
 * @param navController provides navigation component.
 */
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
    (1000..50000 step 1000).forEach { targetStepsList.add(it) }

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

/**
 * Stateless composable for displaying the StepCounterScreen.
 *
 * @param steps (state) total counted steps.
 * @param stepsTarget (state) set target steps.
 * @param targetStepsList list with all the target steps that can be chosen.
 * @param expanded boolean that expands or collapses the set target dropdown menu.
 * @param onExpanded closes the set target dropdown menu when an item is chosen from it.
 * @param onSelectedIndex provides the value of clicked index from the [targetStepsList].
 */
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
                        isStepCounterScreen = true,
                        foregroundColor = if ((steps ?: 0) >= stepsTarget)
                            MaterialTheme.colors.primary
                        else
                            SCYellow,
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




