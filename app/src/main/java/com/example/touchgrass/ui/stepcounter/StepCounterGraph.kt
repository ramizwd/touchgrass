package com.example.touchgrass.ui.stepcounter

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.example.touchgrass.service.StepCounterService.Companion.isSensorOn
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

@Composable
fun StepCounterGraph(stepsGraphViewModel: StepsGraphViewModel) {
    val dataSet = remember { mutableStateListOf(
        BarEntry(1f ,0f),
        BarEntry(2f ,0f),
        BarEntry(3f ,0f),
        BarEntry(4f ,0f),
        BarEntry(5f ,0f),
        BarEntry(6f ,0f),
        BarEntry(7f ,0f)
    )}

    val graphEntries = stepsGraphViewModel.getAllGraphEntries().observeAsState(listOf())
    graphEntries.value.forEach {
        dataSet[it.dayOfWeek.toInt() - 1] = BarEntry(it.dayOfWeek, it.steps)
    }
    val label = "Daily Steps"
    val xAxisLabels = listOf("", "MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")

    val animatedTextColor by animateColorAsState(
        targetValue = if (isSensorOn)
            MaterialTheme.colors.secondary
        else
            Color.Gray,
        animationSpec = tween(400)
    )

    AndroidView (
        modifier = Modifier.fillMaxSize(),
        factory = { context: Context ->
            BarChart(context)
        },
        update = { barChart ->

            val barDataSetFormatter = object : ValueFormatter() {
                override fun getBarLabel(barEntry: BarEntry?): String =
                     barEntry?.y?.toInt().toString()
            }

            val barDataSet = BarDataSet(dataSet, label).apply {
                valueFormatter = barDataSetFormatter
                valueTextSize = 12f
                color = animatedTextColor.toArgb()
            }

            val barData = BarData(barDataSet)
            barChart.apply {
                xAxis.apply {
                    valueFormatter = IndexAxisValueFormatter(xAxisLabels)
                    setDrawGridLines(false)
                    setExtraOffsets(0f,0f,0f,10f)
                }
                axisLeft.apply {
                    granularity = 5f
                    axisMinimum = 0f
                }
                axisRight.apply {
                    setDrawLabels(false)
                    setDrawGridLines(false)
                }
                description = null
                data = barData
                invalidate()
            }
        }
    )
}