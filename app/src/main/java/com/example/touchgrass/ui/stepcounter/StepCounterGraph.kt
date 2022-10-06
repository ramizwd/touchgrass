package com.example.touchgrass.ui.stepcounter

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

/*
TODO
  - Save entries to DB
  - Reset graph every week
 */
@Composable
fun StepCounterGraph(steps: Int?, dayOfWeek: Int?) {
    val dataSet = remember { mutableStateListOf(
        BarEntry(1f ,0f),
        BarEntry(2f ,0f),
        BarEntry(3f ,0f),
        BarEntry(4f ,0f),
        BarEntry(5f ,0f),
        BarEntry(6f ,0f),
        BarEntry(7f ,0f)
    )}

    dayOfWeek?.let {
        dataSet[dayOfWeek -1] = BarEntry(dayOfWeek.toFloat(), steps?.toFloat() ?: 0f)
    }

    AndroidView (
        modifier = Modifier.fillMaxSize(),
        factory = { context: Context ->
            BarChart(context)
        },
        update = { barChart ->
            val barDataSet = BarDataSet(dataSet, "Daily Steps")
            val desc = Description()
            val barData = BarData(barDataSet)
            desc.text = ""
            barChart.apply {
                description = desc
                data = barData
                axisLeft.granularity = 1f
                axisRight.granularity = 1f
                invalidate()
            }
        }
    )
}