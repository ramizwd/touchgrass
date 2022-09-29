package com.example.touchgrass.ui.hydration

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.navigation.NavController


/**
 * Stateful Composable which manages state
 */

object Defaultvalue {
    const val ML = 250
    const val THOUSAND = 1000
    const val ONEHUNRED = 100
    const val FIFTY = 50
}

@Composable
fun HydrationScreen(navController: NavController) {
    var numberString by remember { mutableStateOf(0) }
    var pressIterator by remember { mutableStateOf(0) }
    var drankAmount by remember { mutableStateOf(0) }
    var liquidAmount by remember { mutableStateOf(0) }
    var click by remember { mutableStateOf(false) }
    HydrationScreenBody(
        number = numberString,
        onNumberChange = { numberString = it },
        pressIterator = pressIterator,
        onPressIterator = { pressIterator = it },
        click = click,
        onClickChange = { click = it },
        drankAmount = drankAmount,
        onDrankChange = { drankAmount = it },
        liquidAmount = liquidAmount,
        onChangeLiquid = { liquidAmount = it }
    )
}

/**
 * Composable for displaying the Home Screen
 */
@Composable
fun HydrationScreenBody(
    number: Int,
    onNumberChange: (Int) -> Unit,
    pressIterator: Int,
    onPressIterator: (Int) -> Unit,
    click: Boolean,
    onClickChange: (Boolean) -> Unit,
    drankAmount: Int,
    onDrankChange: (Int) -> Unit,
    liquidAmount: Int,
    onChangeLiquid: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1.5f)
                .background(Color.Blue)
                .padding(3.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (number > 0) {
                    Text(
                        if (drankAmount < number) "You have drank $drankAmount ml / $number ml"
                        else "Congrats you have drank your goal today $drankAmount ml / $number ml"
                    )
                } else {
                    Text(text = "Set a goal how much water you would drink today")
                }
                var expanded by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.TopEnd)
                ) {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Settings")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        for (i in 0.rangeTo(16)) {
                            val values = Defaultvalue.THOUSAND + (Defaultvalue.ML * i)
                            DropdownMenuItem(onClick = {
                                onNumberChange(values)
                                expanded = false
                                onClickChange(true)
                            }) {
                                Text(text = "$values ml")
                            }
                        }
                    }
                }
            }

        }
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                items(19) {
                    val values = Defaultvalue.ONEHUNRED + (Defaultvalue.FIFTY * it)

                    Button(
                        onClick = {
                            Log.i("MATH", "${number / values}")
                            onPressIterator(number / values)
                            onChangeLiquid(values)
                            onClickChange(true)
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
                if (click) {
                    items(pressIterator) {
                        OutlinedButton(
                            onClick = {
                                Log.i("MATH", "$pressIterator")
                                onPressIterator(pressIterator - 1)
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


/*
LazyRow(
modifier = Modifier.fillMaxWidth(),
horizontalArrangement = Arrangement.SpaceEvenly
) {
    items(10) {
        Button(
            onClick = {
                onNumberChange((it + 1) * Defaultvalue.THOUSAND)
                onPressIterator(((it + 1) * Defaultvalue.THOUSAND) / Defaultvalue.ML)
                onClickChange(true)
            },
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth(),
        ) {
            Text(text = "${(it + 1) * Defaultvalue.THOUSAND} ml")
        }
    }
}*/
