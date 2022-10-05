package com.example.touchgrass.ui.stepcounter

import android.content.Context
import android.util.Log
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
  - Update the graph view dynamically
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
    ) }
    dataSet[(dayOfWeek ?: 0) -1] = BarEntry(dayOfWeek?.toFloat() ?: 0f , steps?.toFloat() ?: 0f)
    Log.d("GRAPH", "$steps")

    AndroidView (
        modifier = Modifier.fillMaxSize(),
        factory = { context: Context ->
            val view = BarChart(context)
            view.legend.isEnabled = false
            val data = BarDataSet(dataSet, "STEPS")
            val desc = Description()
            val barData = BarData(data)
            desc.text = ""
            view.description = desc
            view.data = barData
            view
        },
        update = { view ->
            view.invalidate()
        }
    )
}