package com.example.touchgrass.ui.hydration

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.navigation.NavController


/**
 * Stateful Composable which manages state
 */

object DEFAULTVALUE {
    const val ML = 250
    const val THOUSAND = 1000
}

@Composable
fun HydrationScreen(navController: NavController) {
    var numberString by remember { mutableStateOf(0) }
    var pressIterator by remember { mutableStateOf(0) }
    var drankAmount by remember { mutableStateOf(0) }
    var click by remember { mutableStateOf(false) }
    val iterator = (1..5).iterator()
    HydrationScreenBody(
        number = numberString,
        onNumberChange = { numberString = it },
        pressIterator = pressIterator,
        onPressIterator = { pressIterator = it },
        click = click,
        onClickChange = { click = it },
        drankAmount = drankAmount,
        onDrankChange = { drankAmount = it },
        iterator
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
    iterator: IntIterator
) {
    //var pressIterator by remember { mutableStateOf((1..5).iterator()) }
    Column() {
        if (click) {
            Text(text = if (drankAmount < number) "You have drank $drankAmount ml / $number ml" else "Congrats you have drank your goal today $drankAmount ml / $number ml")
        }
        Row() {
            iterator.forEach {
                Button(onClick = {
                    // pressIterator = (1..((it * 1000) / 250)).iterator()
                    onNumberChange(it * DEFAULTVALUE.THOUSAND)
                    onPressIterator((it * DEFAULTVALUE.THOUSAND) / DEFAULTVALUE.ML)
                    onClickChange(true)
                }) {
                    Text(text = "${it * DEFAULTVALUE.THOUSAND}")
                }
            }
        }
        LazyColumn {
            if (click) {
                items(pressIterator) {
                    Button(onClick = {
                        onPressIterator(pressIterator - 1)
                        onDrankChange(drankAmount + DEFAULTVALUE.ML)
                    }) {
                        Text(text = "250 ml")
                    }
                }
            }
        }
    }
}

/*
Column() {
        if (click) {
            pressIterator.forEach {
                Button(onClick = {
                    Log.i("dddd", it.toString())
                }) {
                    Text(text = "$it cup")
                }
            }
        }
    }
*/



