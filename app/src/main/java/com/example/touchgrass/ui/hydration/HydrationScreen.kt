package com.example.touchgrass.ui.hydration

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
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
}

@Composable
fun HydrationScreen() {
    var numberGoal by remember { mutableStateOf(3000) }
    var itemAmount by remember { mutableStateOf(12) }
    var drankAmount by remember { mutableStateOf(0) }
    var liquidAmount by remember { mutableStateOf(250) }
    var expanded by remember { mutableStateOf(false) }

    HydrationScreenBody(
        numberGoal = numberGoal,
        onNumberGoalChange = { numberGoal = it },
        itemAmount = itemAmount,
        onItemAmountChange = { itemAmount = it },
        drankAmount = drankAmount,
        onDrankChange = { drankAmount = it },
        liquidAmount = liquidAmount,
        onChangeLiquid = { liquidAmount = it },
        expanded = expanded,
        onExpanded = { expanded = it }
    )
}

/**
 * Composable for displaying the Home Screen
 */
@Composable
fun HydrationScreenBody(
    numberGoal: Int,
    onNumberGoalChange: (Int) -> Unit,
    itemAmount: Int,
    onItemAmountChange: (Int) -> Unit,
    drankAmount: Int,
    onDrankChange: (Int) -> Unit,
    liquidAmount: Int,
    onChangeLiquid: (Int) -> Unit,
    expanded: Boolean,
    onExpanded: (Boolean) -> Unit
) {
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
                            Log.i("MATH2", "${values % liquidAmount == 0}")
                            onNumberGoalChange(values)
                            onExpanded(false)
                            onItemAmountChange(if (values % liquidAmount == 0) values / liquidAmount else values / liquidAmount + 1)
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
                    percentage = drankAmount / numberGoal.toFloat(),
                    number = numberGoal,
                    color = if (drankAmount >= numberGoal) Color.Green else Color.Blue
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
                                Log.i("MATH1", "${numberGoal % values == 0}")
                                onItemAmountChange(if (numberGoal % values == 0) numberGoal / values else numberGoal / values + 1)
                                onChangeLiquid(values)
                            },
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
                    items(itemAmount) {
                        OutlinedButton(
                            onClick = {
                                Log.i("MATH", "$itemAmount")
                                onItemAmountChange(itemAmount - 1)
                                onDrankChange(drankAmount + liquidAmount)
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
                                Icons.Filled.Favorite,
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