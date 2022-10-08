package com.example.touchgrass.ui.hydration


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.touchgrass.ui.shared.components.CircularProgressBar


/**
 * Stateful Composable which manages state
 */

object DefaultValue {
    const val ML = 250
    const val THOUSAND = 1000
    const val ONE_HUNDRED = 100
    const val FIFTY = 50
    const val THREE_THOUSAND = 3000
}

/**
 * Composable for displaying the Hydration Screen
 */
@Composable
fun HydrationScreen(hydrationViewModel: HydrationViewModel) {
    val hydrationTarget by hydrationViewModel.numberGoal.observeAsState()
    val waterTaken by hydrationViewModel.drankAmount.observeAsState()
    val itemAmount by hydrationViewModel.itemsAmount.observeAsState()

    var expanded by remember { mutableStateOf(false) }

    var numberGoal by remember { mutableStateOf(hydrationTarget ?: DefaultValue.THREE_THOUSAND) }

    var liquidAmount by remember { mutableStateOf(DefaultValue.ML) }

    HydrationScreenBody(
        hydrationViewModel,
        waterTaken,
        itemAmount,
        numberGoal = numberGoal,
        onNumberGoalChange = { numberGoal = it },
        liquidAmount = liquidAmount,
        onChangeLiquid = { liquidAmount = it },
        expanded = expanded,
        onExpanded = { expanded = it })
}

/**
 * Composable for displaying the Home Screen
 */
@Composable
fun HydrationScreenBody(
    hydrationViewModel: HydrationViewModel,
    waterTaken: Int?,
    itemAmount: Int?,
    numberGoal: Int,
    onNumberGoalChange: (Int) -> Unit,
    liquidAmount: Int,
    onChangeLiquid: (Int) -> Unit,
    expanded: Boolean,
    onExpanded: (Boolean) -> Unit
) {

    hydrationViewModel.onItemsAmountUpdate(
        if ((waterTaken ?: 0) < numberGoal)
            if ((numberGoal - (waterTaken ?: 0)) % liquidAmount == 0) (numberGoal - (waterTaken
                ?: 0)) / liquidAmount
            else (numberGoal - (waterTaken
                ?: 0)) / liquidAmount + 1
        else 0
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.TopEnd)
            ) {
                IconButton(onClick = { onExpanded(true) }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "WaterAmount")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { onExpanded(false) }
                ) {
                    for (i in 0.rangeTo(16)) {
                        val values = DefaultValue.THOUSAND + (DefaultValue.ML * i)
                        DropdownMenuItem(onClick = {
                            hydrationViewModel.onNumberGoalUpdate(values)
                            onNumberGoalChange(values)
                            onExpanded(false)
                        }) {
                            Text(text = "$values ml")
                        }
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressBar(
                    percentage = (waterTaken ?: 0) / numberGoal.toFloat(),
                    number = numberGoal,
                    color = if ((waterTaken ?: 0) >= numberGoal) Color.Green else Color.Blue
                )
            }
        }

        Box(
            contentAlignment = Alignment.TopStart,
            modifier = Modifier
                .weight(1f)
        ) {
            Column {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    items(19) {
                        val values = DefaultValue.ONE_HUNDRED + (DefaultValue.FIFTY * it)
                        Button(
                            onClick = {
                                onChangeLiquid(values)
                            },
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth(),
                        ) {
                            Text(text = "$values ml")
                        }

                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(
                        start = 12.dp,
                        top = 16.dp,
                        end = 12.dp,
                        bottom = 16.dp
                    ),
                ) {
                    if (itemAmount != null) {
                        items(itemAmount) {
                            OutlinedButton(
                                onClick = {
                                    hydrationViewModel.onDrankAmountPlus(liquidAmount)
                                    hydrationViewModel.onItemsAmountReduce()
                                },
                                modifier = Modifier
                                    .padding(4.dp)
                                    .fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color.White,
                                    contentColor = Color.Red
                                )
                            ) {
                                Icon(
                                    //Place Holder for a cup
                                    Icons.Filled.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(ButtonDefaults.IconSize)
                                )
                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                Text(text = "$liquidAmount ml")
                            }
                        }
                    }
                }
            }
        }
    }
}
