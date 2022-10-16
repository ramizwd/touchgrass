package com.example.touchgrass.ui.hydration

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
import kotlin.math.roundToInt

object HydrationConstants {
    const val STEP_ML_VALUE = 250
    const val BASE_ML_VALUE = 1000
    const val CUP_SIZE = 200
    const val DEFAULT_TARGET = 3000
}

/**
 * Stateful Composable which manages state.
 *
 * @param hydrationViewModel ViewModel for HydrationScreen providing LiveData for it.
 * @param navController provides navigation component.
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
    var numberGoal by remember {
        mutableStateOf(hydrationTarget ?:
        HydrationConstants.DEFAULT_TARGET)
    }
    var liquidAmount by remember { mutableStateOf(HydrationConstants.CUP_SIZE) }

    val cupSizesList: List<String> = listOf(
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
        cupSizesList = cupSizesList,
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
 * Composable for displaying the Hydration Screen
 *
 * @param waterTaken (state) amount of water consumed.
 * @param itemAmount (state) amount of cups/bottles displayed.
 * @param numberGoal (state) set target milliliters.
 * @param cupSizesList provides values (milliliters) for the cup slider.
 * @param onNumberGoalChange provides the value of clicked index from the [cupSizesList].
 * @param expanded boolean that expands or collapses the set target dropdown menu.
 * @param onExpanded closes the set target dropdown menu when an item is chosen from it.
 */
@Composable
fun HydrationScreenBody(
    hydrationViewModel: HydrationViewModel,
    waterTaken: Int?,
    itemAmount: Int?,
    cupSizesList: List<String>,
    numberGoal: Int,
    onNumberGoalChange: (Int) -> Unit,
    liquidAmount: Int,
    onChangeLiquid: (Int) -> Unit,
    expanded: Boolean,
    onExpanded: (Boolean) -> Unit,
    navController: NavController
) {

    var stateSlider by remember { mutableStateOf(200f) }

    // Updates the amount of cups depending on the drank amount and target amount.
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
                            contentDescription = stringResource(R.string.back_arrow_ic_desc)
                        )
                    }
                },
                actions = {
                    Text(text = stringResource(R.string.set_hydration_target),
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
                .fillMaxWidth()
                .padding(contentPadding)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1.5f)
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
                        // Creates the set target dropdown menu items
                        // and saves the selected target value.
                        for (i in 0.rangeTo(16)) {
                            val values = HydrationConstants.BASE_ML_VALUE +
                                    (HydrationConstants.STEP_ML_VALUE * i)
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
                        value = (waterTaken ?: 0) / numberGoal.toFloat(),
                        target = numberGoal,
                        foregroundColor = if ((waterTaken ?: 0) >= numberGoal)
                            MaterialTheme.colors.primary
                        else
                            Color(0xFF48C1EC),
                        isHydrationScreen = true,
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
                            SliderLabel(values = cupSizesList)
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
                        // Check the item amount and create the cups depending on it.
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
                                            painter = if (liquidAmount >= 800)
                                                painterResource(R.drawable.ic_water_bottle)
                                            else painterResource(R.drawable.ic_water_glass),
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

/**
 * Provided a list of cups size and sets them as the slider labels.
 */
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




