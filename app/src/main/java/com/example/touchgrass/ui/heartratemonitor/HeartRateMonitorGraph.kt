package com.example.touchgrass.ui.heartratemonitor

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

@Composable
fun HeartRateGraph(hr: Float?, sec: Float?) {

    val dataSets = remember { mutableStateListOf<Entry>() }
    dataSets.add(Entry(sec ?: 0f, hr ?: 0f))

    AndroidView (
        modifier = Modifier.fillMaxSize(),
        factory = { context: Context ->
            LineChart(context)
        },
        update = { lineChart ->
            lineChart.legend.isEnabled = false
            val lineDataSet = LineDataSet(dataSets, "BPM")
            val desc = Description()
            val lineData = LineData(lineDataSet)
            desc.text = ""
            lineChart.apply {
                description = desc
                data = lineData
                invalidate()
            }
        }
    )
}