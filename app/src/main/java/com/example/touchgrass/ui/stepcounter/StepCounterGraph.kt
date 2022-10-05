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

@Composable
fun StepCounterGraph() {
    val dataSet = remember { mutableStateListOf(
        BarEntry(1f ,1000f),
        BarEntry(2f ,50000f),
        BarEntry(3f ,2400f),
        BarEntry(4f ,5720f),
        BarEntry(5f ,42f),
        BarEntry(6f ,20050f),
        BarEntry(7f ,5500f)
    ) }

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
            Log.d("CHART", "${view.data} $barData")
            view
        },
        update = { view ->
            // Update the view
            view.invalidate()
        }
    )
}