package com.example.touchgrass.ui.hydration

import android.util.Log
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.touchgrass.ui.shared.components.CircularProgressBar
import kotlinx.coroutines.launch


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

@Composable
fun HydrationScreen(hydrationViewModel: HydrationViewModel) {
    val test by hydrationViewModel.numberGoal.observeAsState()
    val test3 by hydrationViewModel.drankAmount.observeAsState()

    var expanded by remember { mutableStateOf(false) }
    var numberGoal by remember { mutableStateOf(test ?: DefaultValue.THREE_THOUSAND) }
    var drankAmount by remember { mutableStateOf(test3 ?: 0) }
    var liquidAmount by remember { mutableStateOf(DefaultValue.ML) }
    var itemAmount by remember {
        mutableStateOf(
            if (drankAmount < numberGoal)
                if ((numberGoal - drankAmount) % liquidAmount == 0) (numberGoal - drankAmount) / liquidAmount else (numberGoal - drankAmount) / liquidAmount + 1 else 0
        )
    }

    HydrationScreenBody(
        hydrationViewModel,
        numberGoal = numberGoal,
        onNumberGoalChange = { numberGoal = it },
        itemAmount = itemAmount,
        onItemAmountChange = { itemAmount = it },
        drankAmount = drankAmount,
        onDrankChange = { drankAmount = it },
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
                            hydrationViewModel.onNumberGoalUpdate(values)
                            onNumberGoalChange(values)
                            onExpanded(false)
                            if (drankAmount < values) onItemAmountChange(
                                if ((values - drankAmount) % liquidAmount == 0) (values - drankAmount) / liquidAmount
                                else (values - drankAmount) / liquidAmount + 1
                            ) else onItemAmountChange(0)
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
                                if (drankAmount < numberGoal) onItemAmountChange(
                                    if ((numberGoal - drankAmount) % values == 0) (numberGoal - drankAmount) / values
                                    else (numberGoal - drankAmount) / values + 1
                                )
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
                                onDrankChange(drankAmount + liquidAmount)
                                hydrationViewModel.onDrankAmountUpdate(drankAmount + liquidAmount)
                                onItemAmountChange(itemAmount - 1)
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
/*                Column(
                    modifier = Modifier
                        .verticalScroll(te)
                        .fillMaxWidth()
                ) {
                    for (it in 1..itemAmount) {
                        OutlinedButton(
                            onClick = {
                                onDrankChange(drankAmount + liquidAmount)
                                hydrationViewModel.onDrankAmountUpdate(drankAmount + liquidAmount)
                                onItemAmountChange(itemAmount - 1)
                            },
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color.White ,
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
                }*/
            }
        }
    }
}
