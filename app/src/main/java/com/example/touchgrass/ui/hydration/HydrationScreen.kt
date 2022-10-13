package com.example.touchgrass.ui.hydration

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.touchgrass.R
import com.example.touchgrass.ui.shared.components.CircularProgressBar
import com.example.touchgrass.utils.Constants.BACK_ARROW_IC_DESC
import kotlin.math.roundToInt

/**
 * Stateful Composable which manages state
 */

object DefaultValue {
    const val ML = 250
    const val THOUSAND = 1000
    const val ONE_HUNDRED = 100
    const val CUP_SIZE = 200
    const val THREE_THOUSAND = 3000
}

/**
 * Composable for displaying the Hydration Screen
 */
@Composable
fun HydrationScreen(
    hydrationViewModel: HydrationViewModel,
    navController: NavController,
) {
    val hydrationTarget by hydrationViewModel.numberGoal.observeAsState()
    val waterTaken by hydrationViewModel.drankAmount.observeAsState()
    val itemAmount by hydrationViewModel.itemsAmount.observeAsState()

    var expanded by remember { mutableStateOf(false) }
    var numberGoal by remember { mutableStateOf(hydrationTarget ?: DefaultValue.THREE_THOUSAND) }
    var liquidAmount by remember { mutableStateOf(DefaultValue.CUP_SIZE) }

    val list: List<String> = listOf(
        "100",
        "200",
        "300",
        "400",
        "500",
        "600",
        "700",
        "800",
        "900",
        "1000"
    )
    HydrationScreenBody(
        hydrationViewModel,
        waterTaken,
        itemAmount,
        list,
        numberGoal = numberGoal,
        onNumberGoalChange = { numberGoal = it },
        liquidAmount = liquidAmount,
        onChangeLiquid = { liquidAmount = it },
        expanded = expanded,
        onExpanded = { expanded = it },
        navController = navController,
    )
}

/**
 * Composable for displaying the Home Screen
 */
@Composable
fun HydrationScreenBody(
    hydrationViewModel: HydrationViewModel,
    waterTaken: Int?,
    itemAmount: Int?,
    list: List<String>,
    numberGoal: Int,
    onNumberGoalChange: (Int) -> Unit,
    liquidAmount: Int,
    onChangeLiquid: (Int) -> Unit,
    expanded: Boolean,
    onExpanded: (Boolean) -> Unit,
    navController: NavController
) {
    var stateSlider by remember { mutableStateOf(200f) }

    hydrationViewModel.onItemsAmountUpdate(
        if ((waterTaken ?: 0) < numberGoal)
            if ((numberGoal - (waterTaken ?: 0)) % liquidAmount == 0) (numberGoal - (waterTaken
                ?: 0)) / liquidAmount
            else (numberGoal - (waterTaken
                ?: 0)) / liquidAmount + 1
        else 0
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.hydration)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = BACK_ARROW_IC_DESC
                        )
                    }
                },
                actions = {
                    Text(text = stringResource(R.string.set_hydration_target),
                        modifier = Modifier.selectable(
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
                .fillMaxWidth()
                .padding(contentPadding)
        ) {
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
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(9.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_water_glass),
                            contentDescription = null,
                            modifier = Modifier.size(37.dp),
                            tint = Color.Unspecified
                        )
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.weight(1f)
                        ) {
                            SliderLabel(values = list)
                            Slider(
                                value = stateSlider,
                                onValueChange = { stateSlider = it },
                                steps = 8,
                                valueRange = 100f..1000f,
                                onValueChangeFinished = {
                                    onChangeLiquid(stateSlider.roundToInt())
                                })
                        }

                        Icon(
                            painter = painterResource(R.drawable.ic_water_bottle),
                            contentDescription = null,
                            modifier = Modifier.size(42.dp),
                            tint = Color.Unspecified
                        )
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
                                        .padding(4.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = Color.Transparent,
                                    )
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            painter = if (liquidAmount >= 800) painterResource(R.drawable.ic_water_bottle) else painterResource(
                                                R.drawable.ic_water_glass
                                            ),
                                            contentDescription = null,
                                            modifier = Modifier.size(37.dp),
                                            tint = Color.Unspecified
                                        )
                                        Text(text = "$liquidAmount ml")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun SliderLabel(values: List<String>) {
    val textSize = with(LocalDensity.current) { 11.dp.toPx() }
    val padding = with(LocalDensity.current) { 10.dp.toPx() }

    val lineHeightDp = 10.dp
    val lineHeightPx = with(LocalDensity.current) { lineHeightDp.toPx() }

    val canvasHeight = 50.dp
    val textPaint = android.graphics.Paint().apply {
        color = if (isSystemInDarkTheme()) 0xffffffff.toInt() else 0xffff47586B.toInt()
        textAlign = android.graphics.Paint.Align.CENTER
        this.textSize = textSize
    }

    Box(contentAlignment = Alignment.Center) {
        Canvas(
            modifier = Modifier
                .height(canvasHeight)
                .fillMaxWidth()
                .padding(
                    top = canvasHeight
                        .div(2)
                        .minus(lineHeightDp.div(2))
                )
        ) {
            val yStart = 0f
            val distance = (size.width.minus(2 * padding)).div(values.size.minus(1))

            values.forEachIndexed { index, value ->
                drawLine(
                    color = Color.Transparent,
                    start = Offset(x = padding + index.times(distance), y = yStart),
                    end = Offset(x = padding + index.times(distance), y = lineHeightPx)
                )
                if (index.rem(2) == 1) {
                    this.drawContext.canvas.nativeCanvas.drawText(
                        value,
                        padding + index.times(distance),
                        size.height,
                        textPaint
                    )
                }
            }
        }
    }
}




